package yz.serializer.api;

public interface Serializer {
    <T> byte[] serialize(T t);
}
