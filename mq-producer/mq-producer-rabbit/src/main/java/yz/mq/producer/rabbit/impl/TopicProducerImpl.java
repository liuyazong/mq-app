package yz.mq.producer.rabbit.impl;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Connection;
import lombok.EqualsAndHashCode;
import yz.mq.producer.rabbit.AbsPubSubProducer;
import yz.serializer.api.Serializer;

@EqualsAndHashCode(callSuper = true)
public class TopicProducerImpl extends AbsPubSubProducer {

    public TopicProducerImpl(Connection connection, String exchange, String routingKey, Serializer serializer) {
        super(connection, exchange, routingKey, serializer, BuiltinExchangeType.TOPIC);
    }

}
