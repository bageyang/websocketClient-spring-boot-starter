package com.websocket.client.config;

import com.websocket.client.annotation.Handler;
import com.websocket.client.annotation.HandlerMap;
import com.websocket.client.websocket.DefaultWebSocketChannelMap;
import com.websocket.client.websocket.NettyWebSocketClient;
import com.websocket.client.websocket.WebSocketUriMap;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

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
        HashSet<String> url = properties.getUrl();
        SimpleChannelInboundHandler handler = getHandler(context);
        WebSocketUriMap map = getHandlerMap(context);
        List<URI> uri = of(url);
        new NettyWebSocketClient().urls(uri).handler(handler).map(map).run();
    }

    private SimpleChannelInboundHandler getHandler(ApplicationContext context) {
        return Optional.ofNullable(getBeanWithAnnotationOnBean(context, Handler.class,
                SimpleChannelInboundHandler.class))
                .orElseThrow(() -> new BeanInitializationException("can not find bean of type SimpleChannelInboundHandler"));
    }

    private WebSocketUriMap getHandlerMap(ApplicationContext context) {
        WebSocketUriMap annotationOnBean = getBeanWithAnnotationOnBean(context, HandlerMap.class, WebSocketUriMap.class);
        if(Objects.isNull(annotationOnBean)){
            annotationOnBean = new DefaultWebSocketChannelMap();
        }
        return annotationOnBean;
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

    private List<URI> of(HashSet<String> url) {
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
