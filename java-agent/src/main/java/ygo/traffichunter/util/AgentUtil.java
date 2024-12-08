package ygo.traffichunter.util;

import java.net.URI;

/**
 * The {@code AgentUtil} enum provides utility methods for constructing
 * WebSocket and HTTP URLs for the TrafficHunter Agent. It also includes
 * methods for validating and formatting server addresses.
 *
 * @author yungwang-o
 * @version 1.0.0
*/
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
