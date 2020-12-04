package xyz.icanfly.websocket.config;

import io.netty.handler.codec.http.websocketx.WebSocketScheme;
import org.springframework.util.CollectionUtils;
import xyz.icanfly.websocket.annotation.Handler;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import xyz.icanfly.websocket.exception.InitException;
import xyz.icanfly.websocket.websocket.NettyWebSocketConnector;
import xyz.icanfly.websocket.websocket.handler.RetryHandler;
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
        try {
            ApplicationContext context = getApplicationContext();
            List<WebSocketResource> resources = checkAndGetResources(context);
            doBeforeStartConnect(context);
            SimpleChannelInboundHandler handler = getHandler(context);
            NettyWebSocketConnector client = new NettyWebSocketConnector().resources(resources).handler(handler);
            ClientHolder.setWebsocketClient(client);
            client.run();
        }catch (Exception e){
            logger.error("websocket init error!",e);
        }
    }

    private List<WebSocketResource> checkAndGetResources(ApplicationContext context) {
        if(context == null){
            throw new InitException("the application is null!");
        }
        ClientProperties properties = context.getBean(ClientProperties.class);
        RetryProperties retry = properties.getRetry();
        RetryHandler.setRetryProperties(retry);
        Map<String, String> url = properties.getResources();
        if(CollectionUtils.isEmpty(url)){
            throw new InitException("can not load the websocket resources!");
        }
        logger.info("load the config of websocket connection\n"+properties.toString());
        return of(url);
    }

    /**
     * do something before start connect
     * @param context ApplicationContext
     */
    private void doBeforeStartConnect(ApplicationContext context) {
        Map<String, BeforeConnect> beforeEvents = context.getBeansOfType(BeforeConnect.class);
        if(CollectionUtils.isEmpty(beforeEvents)){
            return;
        }
        if (!beforeEvents.isEmpty()) {
            for (BeforeConnect beforeEvent : beforeEvents.values()) {
                beforeEvent.doBefore(context);
            }
        }
    }


    /**
     * select the channelHandler of websocket data
     * @param context ApplicationContext
     * @return a subclass of SimpleChannelInboundHandler
     */
    private SimpleChannelInboundHandler getHandler(ApplicationContext context) {
        return Optional.ofNullable(getBeanWithAnnotationOnBean(context, Handler.class,
                SimpleChannelInboundHandler.class))
                .orElseThrow(() -> new BeanInitializationException("can not find bean of type " +
                        "SimpleChannelInboundHandler"));
    }

    @SuppressWarnings("unchecked")
    private <T> T getBeanWithAnnotationOnBean(ApplicationContext context, Class<? extends Annotation> annotationType,
                                              @NonNull Class<T> type) {
        Map<String, Object> annotationBean = context.getBeansWithAnnotation(annotationType);
        Map<String, T> implementBean = context.getBeansOfType(type);
        return annotationBean.values().stream()
                .filter(e -> type.isAssignableFrom(e.getClass()))
                .map((e -> (T) e))
                .findFirst()
                .orElse(
                        implementBean.values()
                                .stream()
                                .findAny()
                                .orElse(null)
                );
    }

    private List<WebSocketResource> of(Map<String, String> url) {
        List<WebSocketResource> marks = new ArrayList<WebSocketResource>(8);
        url.forEach((k, v) -> {
            URI uri = ofString(v);
            String scheme = uri.getScheme();
            boolean startWithWss = false;
            if (WebSocketScheme.WSS.name().contentEquals(scheme)) {
                startWithWss = true;
            }
            WebSocketResource webSocketResource = new WebSocketResource(k, uri, startWithWss);
            marks.add(webSocketResource);
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
