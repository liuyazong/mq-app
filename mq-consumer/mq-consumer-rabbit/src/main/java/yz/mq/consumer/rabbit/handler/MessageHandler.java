package yz.mq.consumer.rabbit.handler;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class MessageHandler {

    private static final Map<Class, yz.mq.consumer.api.MessageHandler> handlers = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public static <I, R> void handle(I obj) throws Exception {
        Class<I> aClass = (Class<I>) obj.getClass();
        yz.mq.consumer.api.MessageHandler<I, R> handler = MessageHandler.getHandler(aClass);
        R result = handler.handle(obj);
        log.debug("received message {} handled by {},result {}", obj, handler, result);
    }

    public static <I, R> void addHandler(Class<I> aClass, yz.mq.consumer.api.MessageHandler<I, R> handler) {
        handlers.put(aClass, handler);
    }

    @SuppressWarnings("unchecked")
    private static <I, R> yz.mq.consumer.api.MessageHandler<I, R> getHandler(Class<I> aClass) {
        return (yz.mq.consumer.api.MessageHandler<I, R>) handlers.get(aClass);
    }

}
