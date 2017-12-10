package yz.serializer.protobuf;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import lombok.extern.slf4j.Slf4j;
import yz.serializer.api.Serializer;

@Slf4j
public class SerializerImpl implements Serializer {
    @Override
    @SuppressWarnings("unchecked")
    public <T> byte[] serialize(T t) {
        if (null == t) {
            return null;
        }
        Class<T> tClass = (Class<T>) t.getClass();
        Schema<T> schema = RuntimeSchema.getSchema(tClass);
        byte[] result = ProtobufIOUtil.toByteArray(t, schema, LinkedBuffer.allocate());
        log.debug("[Serializer.serialize2bytes] input:{},output:{}", t, result);
        return result;
    }
}
