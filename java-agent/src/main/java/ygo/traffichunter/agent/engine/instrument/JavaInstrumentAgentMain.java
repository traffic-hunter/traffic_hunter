package ygo.traffichunter.agent.engine.instrument;

import java.lang.instrument.Instrumentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ygo.traffichunter.agent.engine.AgentExecutionEngine;
import ygo.traffichunter.agent.engine.env.Environment;
import ygo.traffichunter.agent.engine.instrument.bootstrap.BootState;

/**
 * main.
 * @author yungwang-o
 * @version 1.0.0
 */
public class JavaInstrumentAgentMain {

    private static final Logger log = LoggerFactory.getLogger(JavaInstrumentAgentMain.class);

    private static final BootState STATE = new BootState();

    public static void premain(String agentArgs, Instrumentation inst) {

        final boolean success = STATE.start();
        if(!success) {
            log.error("traffic-hunter-agent-bootstrap already started. skipping agent loading.");
            return;
        }

        AgentExecutionEngine.run(Environment.SYSTEM_PROFILE.systemProfile(), inst);
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        premain(agentArgs, inst);
    }
}
