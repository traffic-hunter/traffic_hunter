package ygo.traffichunter.agent.engine.env;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public enum Environment {

    DEFAULT_PATH("/env/agent-env.yml"),
    VERSION("1.0.0"),
    SYSTEM_PROFILE("traffichunter.config"),
    ;

    private final String env;

    Environment(final String env) {
        this.env = env;
    }

    public String path() {
        return env;
    }

    public String version() {
        return env;
    }

    public String systemProfile() {
        return env;
    }
}
