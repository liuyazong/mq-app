package yz.mq.producer.rabbit.impl;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Connection;
import lombok.EqualsAndHashCode;
import yz.mq.producer.rabbit.AbsPubSubProducer;
import yz.serializer.api.Serializer;

@EqualsAndHashCode(callSuper = true)
public class FanoutProducerImpl extends AbsPubSubProducer {

    public FanoutProducerImpl(Connection connection, String exchange, Serializer serializer) {
        super(connection, exchange, "", serializer, BuiltinExchangeType.FANOUT);
    }

}
