package ygo.traffichunter.retry.annotation;

public @interface BackOff {
    long intervalMillis() default 500;
    int multiplier() default 2;
}
