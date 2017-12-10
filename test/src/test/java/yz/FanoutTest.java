package yz;

import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import yz.mq.consumer.api.Consumer;
import yz.mq.consumer.rabbit.RabbitConsumerFactory;
import yz.mq.consumer.rabbit.handler.MessageHandler;
import yz.mq.producer.api.Producer;
import yz.mq.producer.rabbit.RabbitProducerFactory;
import yz.serializer.api.DeSerializer;
import yz.serializer.api.Serializer;
import yz.serializer.protobuf.DeSerializerImpl;
import yz.serializer.protobuf.SerializerImpl;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

@Slf4j
public class FanoutTest {
    private ConnectionFactory connectionFactory;
    private Serializer serializer;
    private DeSerializer deSerializer;
    private RabbitProducerFactory producerFactory;
    private RabbitConsumerFactory consumerFactory;

    @Before
    public void init() {
        connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);
        serializer = new SerializerImpl();
        deSerializer = new DeSerializerImpl();
        producerFactory = new RabbitProducerFactory(connectionFactory, serializer);
        consumerFactory = new RabbitConsumerFactory(connectionFactory, deSerializer);
        MessageHandler.addHandler(Message.class, obj -> {
            log.info("handle message: {}", obj);
            return null;
        });
    }

    @Test
    public void send() throws IOException, TimeoutException, InterruptedException {
        int nThreads = Runtime.getRuntime().availableProcessors() << 1;
        ExecutorService pool = Executors.newFixedThreadPool(nThreads);
        CountDownLatch latch = new CountDownLatch(nThreads);
        for (int i = 0; i < nThreads; i++) {
            int j = i;
            pool.execute(() -> {
                try {
                    Producer producer = producerFactory.createFanoutProducer("fanout_exchange_01");
                    producer.send(new Message());
                    latch.countDown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        latch.await();
        pool.shutdown();
    }

    @Test
    public void receive() throws Exception {
        Consumer consumer = consumerFactory.createFanoutConsumer("fanout_exchange_01");
        consumer.receive();
        Thread.currentThread().join();
    }
}
