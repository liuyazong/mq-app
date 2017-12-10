package yz.mq.consumer.rabbit;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import yz.serializer.api.DeSerializer;

import java.io.IOException;

@Slf4j
@EqualsAndHashCode(callSuper = true)
public abstract class AbsPubSubConsumer extends AbsConsumer {
    private String exchange;
    private String[] routingKey;
    private BuiltinExchangeType type;

    protected AbsPubSubConsumer(Connection connection, String exchange, String[] routingKey, DeSerializer deSerializer, BuiltinExchangeType type) {
        super(connection, deSerializer);
        this.exchange = exchange;
        this.routingKey = routingKey;
        this.type = type;
    }


    protected String getExchange() {
        return exchange;
    }

    protected String[] getRoutingKey() {
        return routingKey;
    }

    protected BuiltinExchangeType getType() {
        return type;
    }

    @Override
    protected Channel prepareChannel() throws IOException {
        Channel channel = getConnection().createChannel();
        channel.exchangeDeclare(getExchange(), getType(), true, true, null);
        return channel;
    }


    @Override
    protected String prepareQueue(Channel channel) throws Exception {
        String queue = channel.queueDeclare().getQueue();
        for (int i = 0; i < getRoutingKey().length; i++) {
            channel.queueBind(queue, getExchange(), getRoutingKey()[i]);
        }
        return queue;
    }

    @Override
    protected void destroyChannel(Channel channel) throws IOException {
        //do nothing
    }
}
