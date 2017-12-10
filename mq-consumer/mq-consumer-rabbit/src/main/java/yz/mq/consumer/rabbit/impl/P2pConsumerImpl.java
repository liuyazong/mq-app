package yz.mq.consumer.rabbit.impl;

import com.rabbitmq.client.Connection;
import lombok.EqualsAndHashCode;
import yz.mq.consumer.rabbit.AbsP2pConsumer;
import yz.serializer.api.DeSerializer;

@EqualsAndHashCode(callSuper = true)
public class P2pConsumerImpl extends AbsP2pConsumer {

    public P2pConsumerImpl(Connection connection, String queue, DeSerializer deSerializer) {
        super(connection, queue, deSerializer);
    }
}
