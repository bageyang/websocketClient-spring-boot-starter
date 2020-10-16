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
    private Map<String, String> marks;

    public Map<String, String> getMarks() {
        return marks;
    }

    public void setMarks(Map<String, String> url) {
        this.marks = url;
    }


    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner("\n\t");
        joiner.add("urls:");
        marks.forEach((k, v) -> joiner.add("mark: " + k + ",url: " + v));
        return joiner.toString();
    }
}
