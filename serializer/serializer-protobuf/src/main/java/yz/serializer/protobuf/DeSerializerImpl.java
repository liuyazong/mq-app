package yz.serializer.protobuf;

import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import lombok.extern.slf4j.Slf4j;
import yz.serializer.api.DeSerializer;

@Slf4j
public class DeSerializerImpl implements DeSerializer {
    @Override
    public <T> T deSerialize(byte[] bytes, Class<T> tClass) {
        if (null == bytes || 0 == bytes.length) {
            return null;
        }
        Schema<T> schema = RuntimeSchema.<T>getSchema(tClass);
        T t = schema.newMessage();
        ProtobufIOUtil.mergeFrom(bytes, t, schema);
        log.debug("[DeSerializer.deSerialize2object] input:{},output:{}", bytes, t);
        return t;
    }
}
