package ygo.traffichunter.agent.tracking;

import com.sun.tools.attach.VirtualMachine;
import java.nio.file.Paths;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import ygo.AbstractTest;
import ygo.traffichunter.agent.engine.jvm.JVMSelector;

class TransactionTrackingTest extends AbstractTest {

    @Test
    @Disabled
    void 타겟_JVM의_트랜잭션을_확인한다() throws Exception {
        // given
        VirtualMachine vm = JVMSelector.getVM("ygo.testapp.TestAppApplication");

        // when
        String agentPath = Paths.get("build/libs/java-agent-0.0.1-SNAPSHOT-all.jar")
                .toAbsolutePath()
                .toString();

        vm.loadAgent(agentPath);

        // then
    }
}
