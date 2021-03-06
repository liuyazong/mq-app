package yz.mq.consumer.rabbit.impl;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Connection;
import lombok.EqualsAndHashCode;
import yz.mq.consumer.rabbit.AbsPubSubConsumer;
import yz.serializer.api.DeSerializer;

@EqualsAndHashCode(callSuper = true)
public class TopicConsumerImpl extends AbsPubSubConsumer {

    public TopicConsumerImpl(Connection connection, String exchange, String[] routingKey, DeSerializer deSerializer) {
        super(connection, exchange, routingKey, deSerializer, BuiltinExchangeType.TOPIC);
    }

}
