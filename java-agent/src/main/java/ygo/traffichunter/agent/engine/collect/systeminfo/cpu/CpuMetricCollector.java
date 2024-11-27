package ygo.traffichunter.agent.engine.collect.systeminfo.cpu;

import com.sun.management.OperatingSystemMXBean;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.logging.Logger;
import javax.management.MBeanServerConnection;
import ygo.traffichunter.agent.engine.collect.MetricCollector;
import ygo.traffichunter.agent.engine.metric.systeminfo.cpu.CpuStatusInfo;

public class CpuMetricCollector implements MetricCollector<CpuStatusInfo> {

    private static final Logger log = Logger.getLogger(CpuStatusInfo.class.getName());

    @Override
    public CpuStatusInfo collect(final MBeanServerConnection mbsc) {
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

    @Override
    public CpuStatusInfo collect() {
        final OperatingSystemMXBean osMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

        return new CpuStatusInfo(
                osMXBean.getCpuLoad(),
                osMXBean.getProcessCpuLoad(),
                osMXBean.getAvailableProcessors()
        );
    }
}
