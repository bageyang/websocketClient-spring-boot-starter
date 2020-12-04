package xyz.icanfly.websocket.websocket.handler;

import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import xyz.icanfly.websocket.config.RetryProperties;
import xyz.icanfly.websocket.config.WebSocketResource;
import xyz.icanfly.websocket.websocket.NettyWebSocketConnector;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author : yangbing
 * @version : v1.0
 * @description TimeOutHandler
 * @date : 2020/11/17 15:35
 */
public class RetryHandler {
    static Timer timer = new HashedWheelTimer(50L, TimeUnit.MILLISECONDS, 128);
    private static final ConcurrentHashMap<WebSocketResource, Long> RETRY_MAP = new ConcurrentHashMap<>();
    private static RetryProperties retryProperties;

    public static void setRetryProperties(RetryProperties retryProperties) {
        RetryHandler.retryProperties = retryProperties;
    }

    public static void add(WebSocketResource webSocketResource) {
        if (retryProperties.getEnabled()) {
            RetryTimer retryTimer = new RetryTimer(webSocketResource, 0);
            long nextInterval = retryProperties.getInitialInterval();
            if (RETRY_MAP.containsKey(webSocketResource)) {
                Long lastInterval = RETRY_MAP.get(webSocketResource);
                Long maxInterval = retryProperties.getMaxInterval();
                Long multiplier = retryProperties.getMultiplier();
                long delayTime = lastInterval * multiplier;
                nextInterval = delayTime > maxInterval ? maxInterval : delayTime;
            }
            addTask(retryTimer, nextInterval);
            RETRY_MAP.put(webSocketResource, nextInterval);
        }
    }

    static class RetryTimer implements TimerTask {
        private WebSocketResource webSocketResource;

        public RetryTimer(WebSocketResource webSocketResource, int retryTimes) {
            this.webSocketResource = webSocketResource;
        }

        @Override
        public void run(Timeout timeout) throws Exception {
            NettyWebSocketConnector.connection(webSocketResource);
        }
    }

    private static void addTask(TimerTask task, long nextInterval) {
        timer.newTimeout(task, nextInterval, TimeUnit.SECONDS);
    }

    public static void remove(WebSocketResource webSocketResource) {
        RETRY_MAP.remove(webSocketResource);
    }
}
