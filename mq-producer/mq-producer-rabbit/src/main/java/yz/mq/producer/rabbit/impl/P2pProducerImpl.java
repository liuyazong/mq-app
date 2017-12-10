package yz.mq.producer.rabbit.impl;

import com.rabbitmq.client.Connection;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import yz.mq.producer.rabbit.AbsP2pProducer;
import yz.serializer.api.Serializer;

@Slf4j
@EqualsAndHashCode(callSuper = true)
public class P2pProducerImpl extends AbsP2pProducer {

    public P2pProducerImpl(Connection connection, String queue, Serializer serializer) {
        super(connection, queue, serializer);
    }
}
