package yz.mq.consumer.rabbit.impl;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Connection;
import lombok.EqualsAndHashCode;
import yz.mq.consumer.rabbit.AbsPubSubConsumer;
import yz.serializer.api.DeSerializer;

@EqualsAndHashCode(callSuper = true)
public class FanoutConsumerImpl extends AbsPubSubConsumer {

    public FanoutConsumerImpl(Connection connection, String exchange, DeSerializer deSerializer) {
        super(connection, exchange, new String[]{""}, deSerializer, BuiltinExchangeType.FANOUT);
    }
}
