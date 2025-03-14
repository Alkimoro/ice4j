package org.ice4j.util;

public class CustomDuration {

    public static CustomDuration ZERO = new CustomDuration(0L);

    public static CustomDuration ofMillis(Long millis) {
        return new CustomDuration(millis);
    }


    private Long duration = 0L;

    public CustomDuration(Long millis) {
        this.duration = millis;
    }

    public long toMillis() {
        return duration;
    }

    public long toNanos() {
        return duration * 1000000L;
    }

    public boolean isNegative() {
        return duration < 0L;
    }
    public boolean isZero() {
        return duration == 0L;
    }


}
