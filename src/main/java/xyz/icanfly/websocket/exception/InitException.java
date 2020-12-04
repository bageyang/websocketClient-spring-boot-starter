package xyz.icanfly.websocket.exception;

/**
 * @description Thrown when an application init error
 * @author yangbing
 * @date  2020-12-04 11:17
 */
public class InitException extends RuntimeException{
    public InitException(String s) {
        super(s);
    }
}
