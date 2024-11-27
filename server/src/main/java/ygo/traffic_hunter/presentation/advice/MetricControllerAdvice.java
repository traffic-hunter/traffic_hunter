package ygo.traffic_hunter.presentation.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ygo.traffic_hunter.common.exception.TrafficHunterException;

@Slf4j
@RestControllerAdvice(annotations = RestController.class)
public class MetricControllerAdvice {

    @ExceptionHandler(TrafficHunterException.class)
    public ErrorResponse handleTrafficHunterException(final TrafficHunterException e) {
        return null;
    }

    @ExceptionHandler(RuntimeException.class)
    public ErrorResponse handleRuntimeException(final RuntimeException e) {
        return null;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResponse handleIllegalArgumentException(final IllegalArgumentException e) {
        return null;
    }

    @ExceptionHandler(IllegalStateException.class)
    public ErrorResponse handleIllegalStateException(final IllegalStateException e) {
        return null;
    }
}
