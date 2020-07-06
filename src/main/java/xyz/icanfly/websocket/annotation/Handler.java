package xyz.icanfly.websocket.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * to market the implement of SimpleChannelInboundHandler
 * you can define a class which implement SimpleChannelInboundHandler interface
 * and market of this annotation
 * @author yang
 */
@Component
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Handler {
}
