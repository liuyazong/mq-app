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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

@Slf4j
public class RabbitProducerFactory {
    private ConnectionFactory connectionFactory;
    private Serializer serializer;
    private final Map<String, Producer> cache = new ConcurrentHashMap<>();

    public RabbitProducerFactory(ConnectionFactory connectionFactory, Serializer serializer) {
        this.connectionFactory = connectionFactory;
        this.serializer = serializer;
    }

    public Producer createP2pProducer(String routingKey) {
        String key = P2pProducerImpl.class.getName().concat("_").concat(routingKey);
        Producer producer = cache.get(key);
        if (null == producer) {
            Producer v = cache.computeIfAbsent(key, (Void) -> {
                try {
                    return new P2pProducerImpl(connectionFactory.newConnection(), routingKey, serializer);
                } catch (IOException | TimeoutException e) {
                    e.printStackTrace();
                }
                return null;
            });
            return v;
        }
        return producer;
    }

    public Producer createDirectProducer(String exchange, String routingKey) {
        String key = DirectProducerImpl.class.getName().concat("_").concat(exchange).concat("_").concat(routingKey);
        Producer producer = cache.get(key);
        if (null == producer) {
            Producer v = cache.computeIfAbsent(key, (Void) -> {
                try {
                    return  new DirectProducerImpl(connectionFactory.newConnection(), exchange, routingKey, serializer);
                } catch (IOException | TimeoutException e) {
                    e.printStackTrace();
                }
                return null;
            });
            return v;
        }
        return producer;
    }

    public Producer createFanoutProducer(String exchange) {
        String key = FanoutProducerImpl.class.getName().concat("_").concat(exchange);
        Producer producer = cache.get(key);
        if (null == producer) {
            Producer v = cache.computeIfAbsent(key, (Void) -> {
                try {
                    return new FanoutProducerImpl(connectionFactory.newConnection(), exchange, serializer);
                } catch (IOException | TimeoutException e) {
                    e.printStackTrace();
                }
                return null;
            });
            return v;
        }
        return producer;
    }

    public Producer createTopicProducer(String exchange, String routingKey) {
        String key = TopicProducerImpl.class.getName().concat("_").concat(exchange).concat("_").concat(routingKey);
        Producer producer = cache.get(key);
        if (null == producer) {
            Producer v = cache.computeIfAbsent(key, (Void) -> {
                try {
                    return new TopicProducerImpl(connectionFactory.newConnection(), exchange, routingKey, serializer);
                } catch (IOException | TimeoutException e) {
                    e.printStackTrace();
                }
                return null;
            });
            return v;
        }
        return producer;
    }
}
