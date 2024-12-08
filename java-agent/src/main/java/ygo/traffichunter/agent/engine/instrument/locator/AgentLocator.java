package ygo.traffichunter.agent.engine.instrument.locator;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import ygo.traffichunter.agent.engine.instrument.JavaInstrumentAgentMain;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public class AgentLocator {

    public static File getAgentJarFile() throws URISyntaxException {

        ProtectionDomain protectionDomain = JavaInstrumentAgentMain.class.getProtectionDomain();
        CodeSource codeSource = protectionDomain.getCodeSource();
        if(codeSource == null) {
            throw new IllegalStateException(String.format("Unable to get agent location, protection domain = %s", protectionDomain));
        }

        URL location = codeSource.getLocation();
        if(location == null) {
            throw new IllegalStateException(String.format("Unable to get agent location, code source = %s", codeSource));
        }

        final File agentJar = new File(location.toURI());
        if(agentJar.getName().endsWith(".jar")) {
            throw new IllegalStateException("Agent is not a jar file: " + agentJar);
        }

        return agentJar.getAbsoluteFile();
    }
}
