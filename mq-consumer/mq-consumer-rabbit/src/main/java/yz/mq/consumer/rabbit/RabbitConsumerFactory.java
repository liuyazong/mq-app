package yz.mq.consumer.rabbit;

import com.rabbitmq.client.ConnectionFactory;
import yz.mq.consumer.api.Consumer;
import yz.mq.consumer.rabbit.impl.DirectConsumerImpl;
import yz.mq.consumer.rabbit.impl.FanoutConsumerImpl;
import yz.mq.consumer.rabbit.impl.P2pConsumerImpl;
import yz.mq.consumer.rabbit.impl.TopicConsumerImpl;
import yz.serializer.api.DeSerializer;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

public class RabbitConsumerFactory {
    private ConnectionFactory connectionFactory;
    private DeSerializer deSerializer;
    private final Map<String, Consumer> cache = new ConcurrentHashMap<>();

    public RabbitConsumerFactory(ConnectionFactory connectionFactory, DeSerializer deSerializer) {
        this.connectionFactory = connectionFactory;
        this.deSerializer = deSerializer;
    }

    public Consumer createP2pConsumer(String queue) {
        String key = P2pConsumerImpl.class.getName().concat("_").concat(queue);
        Consumer consumer = cache.get(key);
        if (null == consumer) {
            Consumer v = cache.computeIfAbsent(key, (Void) -> {
                try {
                    return new P2pConsumerImpl(connectionFactory.newConnection(), queue, deSerializer);
                } catch (IOException | TimeoutException e) {
                    e.printStackTrace();
                }
                return null;
            });
            return v;
        }
        return consumer;
    }

    public Consumer createDirectConsumer(String exchange, String[] routingKey) {
        String key = DirectConsumerImpl.class.getName().concat("_").concat(exchange).concat("_").concat(routingKey.toString());
        Consumer consumer = cache.get(key);
        if (null == consumer) {
            Consumer v = cache.computeIfAbsent(key, (Void) -> {
                try {
                    return new DirectConsumerImpl(connectionFactory.newConnection(), exchange, routingKey, deSerializer);
                } catch (IOException | TimeoutException e) {
                    e.printStackTrace();
                }
                return null;
            });
            return v;
        }
        return consumer;
    }


    public Consumer createFanoutConsumer(String exchange) {
        String key = FanoutConsumerImpl.class.getName().concat("_").concat(exchange);
        Consumer consumer = cache.get(key);
        if (null == consumer) {
            Consumer v = cache.computeIfAbsent(key, (Void) -> {
                try {
                    return new FanoutConsumerImpl(connectionFactory.newConnection(), exchange, deSerializer);
                } catch (IOException | TimeoutException e) {
                    e.printStackTrace();
                }
                return null;
            });
            return v;
        }
        return consumer;
    }

    public Consumer createTopicConsumer(String exchange, String[] routingKey) {
        String key = TopicConsumerImpl.class.getName().concat("_").concat(exchange).concat("_").concat(routingKey.toString());
        Consumer consumer = cache.get(key);
        if (null == consumer) {
            Consumer v = cache.computeIfAbsent(key, (Void) -> {
                try {
                    return new TopicConsumerImpl(connectionFactory.newConnection(), exchange, routingKey, deSerializer);
                } catch (IOException | TimeoutException e) {
                    e.printStackTrace();
                }
                return null;
            });
            return v;
        }
        return consumer;
    }
}
