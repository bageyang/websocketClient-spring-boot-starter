package xyz.icanfly.websocket.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashSet;
import java.util.StringJoiner;

/**
 * @author yang
 */
@ConfigurationProperties(prefix = "websocket.client")
public class ClientProperties {
    private HashSet<String> url;
    private Integer clientNumbers;
    private Long keepTimes;

    public HashSet<String> getUrl() {
        return url;
    }

    public void setUrl(HashSet<String> url) {
        this.url = url;
    }

    public Integer getClientNumbers() {
        return clientNumbers;
    }

    public void setClientNumbers(Integer clientNumbers) {
        this.clientNumbers = clientNumbers;
    }

    public Long getKeepTimes() {
        return keepTimes;
    }

    public void setKeepTimes(Long keepTimes) {
        this.keepTimes = keepTimes;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ClientProperties.class.getSimpleName() + "[", "]")
                .add("url=" + url)
                .add("clientNumbers=" + clientNumbers)
                .add("keepTimes=" + keepTimes)
                .toString();
    }

}
