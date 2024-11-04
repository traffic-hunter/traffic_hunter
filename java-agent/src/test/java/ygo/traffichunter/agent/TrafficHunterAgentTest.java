package ygo.traffichunter.agent;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import org.junit.jupiter.api.Test;
import ygo.TestExt;
import ygo.traffichunter.agent.engine.jvm.JVMSelector;

class TrafficHunterAgentTest extends TestExt {

    @Test
    void 타겟_jvm_actuator를_에이전트_jmx로_연결하여_메트릭을_수집한다() throws Exception {
        // given
        JMXServiceURL jmxUrl = JVMSelector.getVMXServiceUrl("ygo.testapp.TestAppApplication");

        // when
        JMXConnector jmxConnector = JMXConnectorFactory.connect(jmxUrl);

        MBeanServerConnection mbsc = jmxConnector.getMBeanServerConnection();

        ObjectName objectName = new ObjectName("org.springframework.boot:type=Endpoint,name=Metrics");

        Object health = mbsc.invoke(objectName, "metric", new Object[] {"jvm.memory.used"}, new String[] {String.class.getName()});

        // then
        System.out.println(health);
    }
}