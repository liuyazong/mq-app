package yz.serializer.api;

public interface DeSerializer {
    <T> T deSerialize(byte[] bytes, Class<T> tClass);
}
