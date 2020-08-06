package xyz.icanfly.websocket.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashSet;
import java.util.List;
import java.util.StringJoiner;

/**
 * the config of websocket clients
 * @author yang
 */
@ConfigurationProperties(prefix = "websocket.client")
public class ClientProperties {
    /**
     * the urls of clients
     */
    private List<String> url;

    public List<String> getUrl() {
        return url;
    }

    public void setUrl(List<String> url) {
        this.url = url;
    }


    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner("\n\t");
        joiner.add("urls:");
        for (String s : url) {
            joiner.add(s);
        }
        return joiner.toString();
    }

}
