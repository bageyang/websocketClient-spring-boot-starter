package xyz.icanfly.websocket.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;
import java.util.StringJoiner;

/**
 * the config of websocket clients
 * @author yang
 */
@ConfigurationProperties(prefix = "websocket.client")
public class ClientProperties {
    /**
     * urls of type mapping UrlMark
     */
    private Map<String, String> resources;

    private RetryProperties retry;

    public Map<String, String> getResources() {
        return resources;
    }

    public void setResources(Map<String, String> url) {
        this.resources = url;
    }

    public RetryProperties getRetry() {
        return retry;
    }

    public void setRetry(RetryProperties retry) {
        this.retry = retry;
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner("\n\t");
        joiner.add("urls:");
        resources.forEach((k, v) -> joiner.add("mark: " + k + ",url: " + v));
        return joiner.toString();
    }
}
