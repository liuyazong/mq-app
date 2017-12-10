package yz.mq.producer.rabbit;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import yz.mq.producer.api.Producer;
import yz.serializer.api.Serializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@EqualsAndHashCode
public abstract class AbsProducer implements Producer {
    private Connection connection;
    private String exchange;
    private String routingKey;
    private Serializer serializer;

    public AbsProducer(Connection connection, String exchange, String routingKey, Serializer serializer) {
        this.connection = connection;
        this.exchange = exchange;
        this.routingKey = routingKey;
        this.serializer = serializer;
    }

    protected Connection getConnection() {
        return connection;
    }

    protected String getRoutingKey() {
        return routingKey;
    }

    protected String getExchange() {
        return exchange;
    }

    protected Serializer getSerializer() {
        return serializer;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final <T> void send(T... message) throws Exception {
        Channel channel = this.prepareChannel();
        this.send(channel, message);
        this.destroyChannel(channel);
        log.debug("sent message {}, exchange:{}, routingKey:{}", message, getExchange(), getRoutingKey());
    }


    private <T> void send(Channel channel, T[] message) throws IOException {
        for (T aMessage : message) {
            Map<String, Object> headers = new HashMap<>();
            headers.put("type", aMessage.getClass().getName());
            AMQP.BasicProperties props = new AMQP.BasicProperties()
                    .builder()
                    .contentType("text/plain")
                    .headers(headers)
                    .deliveryMode(2)
                    .priority(0)
                    .build();
            channel.basicPublish(getExchange(), getRoutingKey(), true, props, serializer.serialize(aMessage));
        }
    }

    protected abstract Channel prepareChannel() throws Exception;

    protected abstract void destroyChannel(Channel channel) throws Exception;

}
