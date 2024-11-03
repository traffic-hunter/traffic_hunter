package ygo.traffichunter.agent.engine.env;

public enum Environment {

    DEFAULT_PATH("/env/agent-env.yml"),
    VERSION("1.0.0"),
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
}
