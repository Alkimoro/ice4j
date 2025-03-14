package org.ice4j.util;

public class CustomDuration {

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

}
