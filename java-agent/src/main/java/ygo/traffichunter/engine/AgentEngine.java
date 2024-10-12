package ygo.traffichunter.engine;

import com.sun.management.OperatingSystemMXBean;
import com.sun.management.ThreadMXBean;
import com.sun.management.UnixOperatingSystemMXBean;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import ygo.traffichunter.engine.systeminfo.SystemInfo;
import ygo.traffichunter.engine.systeminfo.cpu.CpuStatusInfo;
import ygo.traffichunter.engine.systeminfo.memory.MemoryStatusInfo;
import ygo.traffichunter.engine.systeminfo.memory.MemoryStatusInfo.MemoryUsage;
import ygo.traffichunter.engine.systeminfo.thread.ThreadStatusInfo;

/**
 * The agent engine collect local jvm program.
 * <br/>
 * <br/>
 * what is metric?
 * <br/>
 * <br/>
 * gc, thread, tomcat, memory heap, cpu usage
 * <br/>
 */
public final class AgentEngine {

    private static final Logger log = Logger.getLogger(AgentEngine.class.getName());
    private final List<VirtualMachineDescriptor> vmDescriptors;
    private int pid;

    private static final String SPRING_VM = "TestAppApplication";
    
    private AgentEngine(final List<VirtualMachineDescriptor> vmDescriptors) {
        log.info("Agent engine started");
        this.vmDescriptors = Objects.requireNonNull(vmDescriptors);
    }

    public static AgentEngine connect() {
        final List<VirtualMachineDescriptor> virtualMachineDescriptors = VirtualMachine.list();

        System.out.println(virtualMachineDescriptors);

        return new AgentEngine(virtualMachineDescriptors);
    }

    public AgentEngine pid(final int pid) {
        this.pid = pid;

        return this;
    }

    public SystemInfo execute() {

        try (final JMXConnector jmxConnector = JMXConnectorFactory.connect(this.getVM())) {

            final MBeanServerConnection mBeanServerConnection = jmxConnector.getMBeanServerConnection();

            return this.getSystemInfo(mBeanServerConnection);
        } catch (IOException e) {
            log.warning("Failed to start local management agent: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private JMXServiceURL getVM() {
        try {
            final VirtualMachineDescriptor vmDescriptor = vmDescriptors.stream()
                    .filter(vm -> Objects.equals(Integer.parseInt(vm.id()), pid))
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

    private SystemInfo getSystemInfo(final MBeanServerConnection mbsc) {
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
