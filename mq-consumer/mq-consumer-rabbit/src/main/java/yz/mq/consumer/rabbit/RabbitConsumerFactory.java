package yz.mq.consumer.rabbit;

import com.rabbitmq.client.ConnectionFactory;
import yz.mq.consumer.api.Consumer;
import yz.mq.consumer.rabbit.impl.DirectConsumerImpl;
import yz.mq.consumer.rabbit.impl.FanoutConsumerImpl;
import yz.mq.consumer.rabbit.impl.P2pConsumerImpl;
import yz.mq.consumer.rabbit.impl.TopicConsumerImpl;
import yz.serializer.api.DeSerializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class RabbitConsumerFactory {
    private ConnectionFactory connectionFactory;
    private DeSerializer deSerializer;
    private final Map<String, Consumer> cache = new HashMap<>();

    public RabbitConsumerFactory(ConnectionFactory connectionFactory, DeSerializer deSerializer) {
        this.connectionFactory = connectionFactory;
        this.deSerializer = deSerializer;
    }

    public Consumer createP2pConsumer(String queue) throws IOException, TimeoutException {
        String key = P2pConsumerImpl.class.getName().concat("_").concat(queue);
        Consumer consumer = cache.get(key);
        if (null == consumer) {
            synchronized (this) {
                consumer = cache.get(key);
                if (null == consumer) {
                    cache.put(key, new P2pConsumerImpl(connectionFactory.newConnection(), queue, deSerializer));
                }
            }
        }
        return cache.get(key);
    }

    public Consumer createDirectConsumer(String exchange, String[] routingKey) throws IOException, TimeoutException {
        String key = DirectConsumerImpl.class.getName().concat("_").concat(exchange).concat("_").concat(routingKey.toString());
        Consumer consumer = cache.get(key);
        if (null == consumer) {
            synchronized (this) {
                consumer = cache.get(key);
                if (null == consumer) {
                    cache.put(key, new DirectConsumerImpl(connectionFactory.newConnection(), exchange, routingKey, deSerializer));
                }
            }
        }
        return cache.get(key);
    }

    public Consumer createFanoutConsumer(String exchange) throws IOException, TimeoutException {
        String key = FanoutConsumerImpl.class.getName().concat("_").concat(exchange);
        Consumer consumer = cache.get(key);
        if (null == consumer) {
            synchronized (this) {
                consumer = cache.get(key);
                if (null == consumer) {
                    cache.put(key, new FanoutConsumerImpl(connectionFactory.newConnection(), exchange, deSerializer));
                }
            }
        }
        return cache.get(key);
    }

    public Consumer createTopicConsumer(String exchange, String[] routingKey) throws IOException, TimeoutException {
        String key = TopicConsumerImpl.class.getName().concat("_").concat(exchange).concat("_").concat(routingKey.toString());
        Consumer consumer = cache.get(key);
        if (null == consumer) {
            synchronized (this) {
                consumer = cache.get(key);
                if (null == consumer) {
                    cache.put(key, new TopicConsumerImpl(connectionFactory.newConnection(), exchange, routingKey, deSerializer));
                }
            }
        }
        return cache.get(key);
    }
}
