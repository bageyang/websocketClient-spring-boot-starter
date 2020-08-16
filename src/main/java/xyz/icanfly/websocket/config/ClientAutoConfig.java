package xyz.icanfly.websocket.config;

import xyz.icanfly.websocket.annotation.Handler;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import xyz.icanfly.websocket.websocket.WebSocketConnector;
import xyz.icanfly.websocket.websocket.status.ObjectManager;

import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yang
 */
@EnableConfigurationProperties(ClientProperties.class)
@Component
public class ClientAutoConfig extends ApplicationObjectSupport implements SmartInitializingSingleton {

    @Override
    public void afterSingletonsInstantiated() {
        ApplicationContext context = getApplicationContext();
        ClientProperties properties = Optional.of(context.getBean(ClientProperties.class)).get();
        logger.info("select the client properties: \n"+properties.toString());
        List<String> url = properties.getUrl();
        SimpleChannelInboundHandler handler = getHandler(context);
        List<URI> uri = of(url);
        WebSocketConnector client = new WebSocketConnector().urls(uri).handler(handler);
        ObjectManager.setWebSocketConnector(client);
        client.run();
    }

    private SimpleChannelInboundHandler getHandler(ApplicationContext context) {
        return Optional.ofNullable(getBeanWithAnnotationOnBean(context, Handler.class,
                SimpleChannelInboundHandler.class))
                .orElseThrow(() -> new BeanInitializationException("can not find bean of type SimpleChannelInboundHandler"));
    }

    @SuppressWarnings("unchecked")
    private <T> T getBeanWithAnnotationOnBean(ApplicationContext context, Class<? extends Annotation> annotationType, @NonNull Class<T> type) {
        Map<String, Object> annotationBean = context.getBeansWithAnnotation(annotationType);
        Map<String, T> implementBean = context.getBeansOfType(type);
        return annotationBean.values().stream()
                .filter(e -> type.isAssignableFrom(e.getClass()))
                .map((e -> (T) e))
                .findFirst()
                .orElse(implementBean.values()
                        .stream()
                        .findAny()
                        .orElse(null)
                );
    }

    private List<URI> of(List<String> url) {
        return url.stream().map(this::ofString).collect(Collectors.toList());
    }

    private URI ofString(String s) {
        try {
            return new URI(s);
        } catch (Exception e) {
            throw new IllegalArgumentException("Illegal url: " + s);
        }
    }
}
