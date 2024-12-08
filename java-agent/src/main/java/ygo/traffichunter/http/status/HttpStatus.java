package ygo.traffichunter.http.status;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
@Deprecated(since = "1.0.0")
public enum HttpStatus {

    OK(200, "request ok!!"),
    NOT_FOUND(404, "not found!!"),
    INTERNAL_SERVER_ERROR(500, "internal server error!!"),
    BAD_REQUEST(400, "bad request!!"),
    ;

    private final int statusCode;
    private final String message;

    HttpStatus(final int statusCode, final String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }
}
