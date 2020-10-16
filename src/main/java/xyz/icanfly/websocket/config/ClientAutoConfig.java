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
import xyz.icanfly.websocket.websocket.NettyWebSocketClient;
import xyz.icanfly.websocket.websocket.status.ClientHolder;

import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.*;

/**
 * @author yang
 */
@EnableConfigurationProperties(ClientProperties.class)
@Component
public class ClientAutoConfig extends ApplicationObjectSupport implements SmartInitializingSingleton {

    @Override
    public void afterSingletonsInstantiated() {
        ApplicationContext context = getApplicationContext();
        if(context == null){
            System.exit(1);
        }
        ClientProperties properties = Optional.of(context.getBean(ClientProperties.class)).get();
        logger.info("select the client properties: \n"+properties.toString());
        Map<String,String> url = properties.getMarks();
        SimpleChannelInboundHandler handler = getHandler(context);
        List<UrlMark> marks = of(url);
        NettyWebSocketClient client = new NettyWebSocketClient().marks(marks).handler(handler);
        ClientHolder.setWebsocketClient(client);
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

    private List<UrlMark> of(Map<String,String> url) {
        List<UrlMark> marks = new ArrayList<UrlMark>(8);
        url.forEach((k,v)->{
            marks.add(new UrlMark(k,ofString(v)));
        });
        return marks;
    }

    private URI ofString(String s) {
        try {
            return new URI(s);
        } catch (Exception e) {
            throw new IllegalArgumentException("Illegal url: " + s);
        }
    }
}
