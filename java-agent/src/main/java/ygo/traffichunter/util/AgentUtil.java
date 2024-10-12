package ygo.traffichunter.util;

public enum AgentUtil {
    URL("ws://%s/traffic-hunter");

    private static final String pattern = "^(localhost|\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}):\\d{1,5}$";

    private final String url;

    AgentUtil(final String url) {
        this.url = url;
    }

    public static boolean isAddr(final String serverUrl) {
        return serverUrl.matches(pattern);
    }

    public String getUrl(final String serverUrl) {
        return String.format(url, serverUrl);
    }
}
