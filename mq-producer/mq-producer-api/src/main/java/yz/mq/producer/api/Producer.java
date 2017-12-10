package yz.mq.producer.api;

public interface Producer {
    @SuppressWarnings("unchecked")
    <T> void send(T... message) throws Exception;
}
