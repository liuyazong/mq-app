package yz.mq.producer.rabbit;

import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import yz.mq.producer.api.Producer;
import yz.mq.producer.rabbit.impl.DirectProducerImpl;
import yz.mq.producer.rabbit.impl.FanoutProducerImpl;
import yz.mq.producer.rabbit.impl.P2pProducerImpl;
import yz.mq.producer.rabbit.impl.TopicProducerImpl;
import yz.serializer.api.Serializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@Slf4j
public class RabbitProducerFactory {
    private ConnectionFactory connectionFactory;
    private Serializer serializer;
    private final Map<String, Producer> cache = new HashMap<>();

    public RabbitProducerFactory(ConnectionFactory connectionFactory, Serializer serializer) {
        this.connectionFactory = connectionFactory;
        this.serializer = serializer;
    }

    public Producer createP2pProducer(String routingKey) throws IOException, TimeoutException {
        String key = P2pProducerImpl.class.getName().concat("_").concat(routingKey);
        Producer producer = cache.get(key);
        if (null == producer) {
            synchronized (this) {
                producer = cache.get(key);
                if (null == producer) {
                    cache.put(key, new P2pProducerImpl(connectionFactory.newConnection(), routingKey, serializer));
                }
            }
        }
        return cache.get(key);
    }

    public Producer createDirectProducer(String exchange, String routingKey) throws IOException, TimeoutException {
        String key = DirectProducerImpl.class.getName().concat("_").concat(exchange).concat("_").concat(routingKey);
        Producer producer = cache.get(key);
        if (null == producer) {
            synchronized (this) {
                producer = cache.get(key);
                if (null == producer) {
                    cache.put(key, new DirectProducerImpl(connectionFactory.newConnection(), exchange, routingKey, serializer));
                }
            }
        }
        return cache.get(key);
    }

    public Producer createFanoutProducer(String exchange) throws IOException, TimeoutException {
        String key = FanoutProducerImpl.class.getName().concat("_").concat(exchange);
        Producer producer = cache.get(key);
        if (null == producer) {
            synchronized (this) {
                producer = cache.get(key);
                if (null == producer) {
                    cache.put(key, new FanoutProducerImpl(connectionFactory.newConnection(), exchange, serializer));
                }
            }
        }
        return cache.get(key);
    }

    public Producer createTopicProducer(String exchange, String routingKey) throws IOException, TimeoutException {
        String key = TopicProducerImpl.class.getName().concat("_").concat(exchange).concat("_").concat(routingKey);
        Producer producer = cache.get(key);
        if (null == producer) {
            synchronized (this) {
                producer = cache.get(key);
                if (null == producer) {
                    cache.put(key, new TopicProducerImpl(connectionFactory.newConnection(), exchange, routingKey, serializer));
                }
            }
        }
        return cache.get(key);
    }
}
