package xyz.icanfly.websocket.config;

import java.util.Objects;

/**
 * @program: websocketClient-spring-boot-starter
 * @description: RetryProperties
 * @author: yangbing
 * @create: 2020-12-04 12:37
 */
public class RetryProperties {
    private Boolean enabled = true;
    private Long maxInterval = 1000L;
    private Long initialInterval = 30L;
    private Long multiplier = 2L;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Long getMaxInterval() {
        return maxInterval;
    }

    public void setMaxInterval(Long maxInterval) {
        this.maxInterval = maxInterval;
    }

    public Long getInitialInterval() {
        return initialInterval;
    }

    public void setInitialInterval(Long initialInterval) {
        this.initialInterval = initialInterval;
    }

    public Long getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(Long multiplier) {
        this.multiplier = multiplier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RetryProperties that = (RetryProperties) o;
        return Objects.equals(enabled, that.enabled) &&
                Objects.equals(maxInterval, that.maxInterval) &&
                Objects.equals(initialInterval, that.initialInterval) &&
                Objects.equals(multiplier, that.multiplier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enabled, maxInterval, initialInterval, multiplier);
    }

    @Override
    public String toString() {
        return "RetryProperties{" +
                "enabled=" + enabled +
                ", maxInterval=" + maxInterval +
                ", initialInterval=" + initialInterval +
                ", multiplier=" + multiplier +
                '}';
    }
}
