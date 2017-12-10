package yz.mq.consumer.api;

public interface MessageHandler<I, R> {
    R handle(I obj) throws Exception;
}
