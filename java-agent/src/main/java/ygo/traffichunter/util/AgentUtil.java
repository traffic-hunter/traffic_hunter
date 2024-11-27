package ygo.traffichunter.util;

import java.net.URI;

public enum AgentUtil {
    WEBSOCKET_URL("ws://%s/traffic-hunter/tx"),
    HTTP_URL("http://%s/traffic-hunter"),
    ;

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

    public URI getUri(final String serverUrl) {
        return URI.create(String.format(url, serverUrl));
    }
}
