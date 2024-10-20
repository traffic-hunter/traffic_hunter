package ygo.traffic_hunter.common.util.ip;

import jakarta.servlet.http.HttpServletRequest;

public class ClientInfoUtil {

    public static String getClientIp(final HttpServletRequest request) {
        String clientIp = request.getHeader("X-Forwarded-For");

        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("Proxy-Client-IP");
        }
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("WL-Proxy-Client-IP");
        }
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (clientIp  == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp )) {
            clientIp  = request.getHeader("X-Real-IP");
        }
        if (clientIp  == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp )) {
            clientIp  = request.getHeader("X-RealIP");
        }
        if (clientIp  == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp )) {
            clientIp  = request.getHeader("REMOTE_ADDR");
        }
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getRemoteAddr();
        }

        return clientIp;
    }
}
