package ygo.traffichunter.agent.engine;

import com.sun.management.OperatingSystemMXBean;
import com.sun.management.ThreadMXBean;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Logger;
import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import ygo.traffichunter.agent.engine.systeminfo.SystemInfo;
import ygo.traffichunter.agent.engine.systeminfo.memory.MemoryStatusInfo;
import ygo.traffichunter.agent.engine.systeminfo.memory.MemoryStatusInfo.MemoryUsage;
import ygo.traffichunter.agent.engine.systeminfo.cpu.CpuStatusInfo;
import ygo.traffichunter.agent.engine.systeminfo.thread.ThreadStatusInfo;
import ygo.traffichunter.agent.property.TrafficHunterAgentProperty;
import ygo.traffichunter.http.HttpBuilder;
import ygo.traffichunter.retry.backoff.BackOffPolicy;

/**
 * After selecting a JVM, this agent execution engine collects the metrics of the JVM at regular intervals and transmits them to the server.
 * <br/>
 * <br/>
 * The transmission method is HTTP.
 * <br/>
 * <br/>
 * what metric is it?
 * <br/>
 * <br/>
 * garbage collection (GC), thread, tomcat, memory heap, cpu usage
 * <br/>
 */
public final class AgentExecutionEngine {

    private static final Logger log = Logger.getLogger(AgentExecutionEngine.class.getName());

    public static void run(final TrafficHunterAgentProperty property) {

        final ScheduledExecutorService es = Executors.newSingleThreadScheduledExecutor();

        es.scheduleAtFixedRate(() -> {
            final SystemInfo systemInfo = execute(property.targetJVMPath());

            final HttpResponse<String> httpResponse = HttpBuilder.newBuilder(property.uri())
                    .header("Content-Type", "application/json")
                    .timeOut(Duration.ofSeconds(10))
                    .request(systemInfo)
                    .build();

            log.info("http status code : " + httpResponse.statusCode());

        }, 0, property.scheduleInterval(), property.timeUnit());
    }

    private static SystemInfo execute(final String targetJVMPath) {

        try (final JMXConnector jmxConnector = JMXConnectorFactory.connect(getVM(targetJVMPath))) {

            final MBeanServerConnection mBeanServerConnection = jmxConnector.getMBeanServerConnection();

            return new SystemMetric(mBeanServerConnection)
                    .getSystemInfo();

        } catch (IOException e) {
            log.warning("Failed to start local management agent: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static JMXServiceURL getVM(final String targetJVMPath) {

        try {
            final VirtualMachineDescriptor vmDescriptor = VirtualMachine.list().stream()
                    .filter(vm -> Objects.equals(vm.displayName(), targetJVMPath))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("No virtual machine found"));

            final VirtualMachine vm = VirtualMachine.attach(vmDescriptor.id().trim());

            final String jmxUrl = vm.startLocalManagementAgent();

            return new JMXServiceURL(jmxUrl);
        } catch (AttachNotSupportedException | IOException e) {
            log.warning("Not found jvm service url : " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private record SystemMetric(MBeanServerConnection mbsc) {

        public SystemInfo getSystemInfo() {
            return new SystemInfo(getMemoryStatus(mbsc), getThreadStatus(mbsc), getCpuStatus(mbsc));
        }

        private MemoryStatusInfo getMemoryStatus(final MBeanServerConnection mbsc) {

            try {
                final MemoryMXBean memoryMXBean = ManagementFactory.newPlatformMXBeanProxy(
                        mbsc,
                        ManagementFactory.MEMORY_MXBEAN_NAME,
                        MemoryMXBean.class
                );

                final java.lang.management.MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
                final java.lang.management.MemoryUsage nonHeapMemoryUsage = memoryMXBean.getNonHeapMemoryUsage();

                return new MemoryStatusInfo(
                        new MemoryUsage(
                                heapMemoryUsage.getInit(),
                                heapMemoryUsage.getUsed(),
                                heapMemoryUsage.getCommitted(),
                                heapMemoryUsage.getMax()
                        ),
                        new MemoryUsage(
                                nonHeapMemoryUsage.getInit(),
                                nonHeapMemoryUsage.getUsed(),
                                nonHeapMemoryUsage.getCommitted(),
                                nonHeapMemoryUsage.getMax()
                        )
                );
            } catch (IOException e) {
                log.warning("Failed to get heap memory metric: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }

        private ThreadStatusInfo getThreadStatus(final MBeanServerConnection mbsc) {

            try {
                final ThreadMXBean threadMXBean = ManagementFactory.newPlatformMXBeanProxy(
                        mbsc,
                        ManagementFactory.THREAD_MXBEAN_NAME,
                        ThreadMXBean.class
                );

                return new ThreadStatusInfo(
                        threadMXBean.getThreadCount(),
                        threadMXBean.getPeakThreadCount(),
                        threadMXBean.getTotalStartedThreadCount()
                );
            } catch (IOException e) {
                log.warning("Failed to get thread metric: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }

        private CpuStatusInfo getCpuStatus(final MBeanServerConnection mbsc) {

            try {
                final OperatingSystemMXBean osMXBean = ManagementFactory.newPlatformMXBeanProxy(
                        mbsc,
                        ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME,
                        OperatingSystemMXBean.class
                );

                return new CpuStatusInfo(
                        osMXBean.getCpuLoad(),
                        osMXBean.getProcessCpuLoad(),
                        osMXBean.getAvailableProcessors()
                );
            } catch (IOException e) {
                log.warning("Failed to get cpu metric: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }
}
