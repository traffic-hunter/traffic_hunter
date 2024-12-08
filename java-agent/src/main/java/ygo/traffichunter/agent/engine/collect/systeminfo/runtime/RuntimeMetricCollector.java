package ygo.traffichunter.agent.engine.collect.systeminfo.runtime;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.logging.Logger;
import javax.management.MBeanServerConnection;
import ygo.traffichunter.agent.engine.collect.AbstractMBeanMetricCollector;
import ygo.traffichunter.agent.engine.metric.systeminfo.runtime.RuntimeStatusInfo;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public class RuntimeMetricCollector extends AbstractMBeanMetricCollector<RuntimeStatusInfo> {

    private static final Logger log = Logger.getLogger(RuntimeStatusInfo.class.getName());

    @Override
    public RuntimeStatusInfo collect(final MBeanServerConnection mbsc) {
        try {
            final RuntimeMXBean runtimeMXBean = ManagementFactory.newPlatformMXBeanProxy(
                    mbsc,
                    ManagementFactory.RUNTIME_MXBEAN_NAME,
                    RuntimeMXBean.class
            );

            return new RuntimeStatusInfo(
                    runtimeMXBean.getStartTime(),
                    runtimeMXBean.getUptime(),
                    runtimeMXBean.getVmName(),
                    runtimeMXBean.getVmVersion()
            );
        } catch (IOException e) {
            log.warning("Failed to get runtime metric: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public RuntimeStatusInfo collect() {
        final RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();

        return new RuntimeStatusInfo(
                runtimeMXBean.getStartTime(),
                runtimeMXBean.getUptime(),
                runtimeMXBean.getVmName(),
                runtimeMXBean.getVmVersion()
        );
    }
}
