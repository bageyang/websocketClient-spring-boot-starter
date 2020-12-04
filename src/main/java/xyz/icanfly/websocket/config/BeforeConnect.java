package xyz.icanfly.websocket.config;

import org.springframework.context.ApplicationContext;

/**
 * @author : yangbing
 * @version : v1.0
 * @description BeforeConnect
 * @date : 2020/10/17 13:57
 */
public interface BeforeConnect {
    /**
     * do something before connector work
     * @param context ApplicationContext
     */
    void doBefore(ApplicationContext context);
}
