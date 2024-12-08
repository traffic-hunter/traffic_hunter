package ygo.traffichunter.agent.engine.instrument.annotation;

/**
 * The {@code AnnotationPath} enum defines commonly used annotation paths
 * for Spring-based applications, which are used for bytecode manipulation
 * with ByteBuddy.
 *
 * @author yungwang-o
 * @version 1.0.0
 */
public enum AnnotationPath {

    TRANSACTIONAL("org.springframework.transaction.annotation.Transactional"),
    SERVICE("org.springframework.stereotype.Service"),
    REPOSITORY("org.springframework.stereotype.Repository"),
    REST_CONTROLLER("org.springframework.web.bind.annotation.RestController"),
    CONTROLLER("org.springframework.stereotype.Controller"),
    ;

    private final String path;

    AnnotationPath(final String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public static boolean filter(final String path) {
        return path.equals("join") || path.equals("wait") || path.equals("notify") || path.equals("notifyAll") ||
                path.equals("hashcode") || path.equals("equals") || path.equals("toString");
    }
}
