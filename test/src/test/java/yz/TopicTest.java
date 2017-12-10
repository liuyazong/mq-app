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
public class TopicTest {
    private ConnectionFactory connectionFactory;
    private Serializer serializer;
    private DeSerializer deSerializer;
    private RabbitProducerFactory producerFactory;
    private RabbitConsumerFactory consumerFactory;

    @Before
    public void init() {
        connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("192.168.1.37");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("admin");
        connectionFactory.setPassword("admin");
        serializer = new SerializerImpl();
        deSerializer = new DeSerializerImpl();
        producerFactory = new RabbitProducerFactory(connectionFactory, serializer);
        consumerFactory = new RabbitConsumerFactory(connectionFactory, deSerializer);
        MessageHandler.addHandler(Message.class, obj -> obj);
    }

    @Test
    public void send0() throws IOException, TimeoutException, InterruptedException {
        int nThreads = Runtime.getRuntime().availableProcessors() << 1;
        Producer producer = producerFactory.createTopicProducer("topic_exchange_01", "routing.key.0000");
        ExecutorService pool = Executors.newFixedThreadPool(nThreads);
        CountDownLatch latch = new CountDownLatch(nThreads);
        for (int i = 0; i < nThreads; i++) {
            int j = i;
            pool.execute(() -> {
                try {
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
    public void send1() throws IOException, TimeoutException, InterruptedException {
        Producer producer = producerFactory.createTopicProducer("topic_exchange_01", "routing.key.0001");
        int nThreads = Runtime.getRuntime().availableProcessors() << 1;
        ExecutorService pool = Executors.newFixedThreadPool(nThreads);
        CountDownLatch latch = new CountDownLatch(nThreads);
        for (int i = 0; i < nThreads; i++) {
            int j = i;
            pool.execute(() -> {
                try {
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
    public void send2() throws IOException, TimeoutException, InterruptedException {
        Producer producer = producerFactory.createTopicProducer("topic_exchange_01", "routing.key2.0001");
        int nThreads = Runtime.getRuntime().availableProcessors() << 1;
        ExecutorService pool = Executors.newFixedThreadPool(nThreads);
        CountDownLatch latch = new CountDownLatch(nThreads);
        for (int i = 0; i < nThreads; i++) {
            long j = System.currentTimeMillis();
            pool.execute(() -> {
                try {
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
    public void receive0() throws Exception {
        Consumer consumer = consumerFactory.createTopicConsumer("topic_exchange_01", new String[]{"routing.*.0001"});
        consumer.receive();
        Thread.currentThread().join();
    }

    @Test
    public void receive1() throws Exception {
        Consumer consumer = consumerFactory.createTopicConsumer("topic_exchange_01", new String[]{"routing.#"});
        consumer.receive();
        Thread.currentThread().join();
    }

    @Test
    public void receive2() throws Exception {
        Consumer consumer = consumerFactory.createTopicConsumer("topic_exchange_01", new String[]{"*.key2.*"});
        consumer.receive();
        Thread.currentThread().join();
    }
}


