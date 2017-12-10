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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * author: liuyazong
 * datetime: 2017/12/6 下午7:12
 */
@Slf4j
public class P2pTest {

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
        MessageHandler.addHandler(Message.class, obj -> obj);
    }

    @Test
    public void send() throws Exception {
        Producer producer = producerFactory.createP2pProducer("hello");//new P2PProducerImpl (connection, "hello",serializer);
        int nThreads = Runtime.getRuntime().availableProcessors() << 1;
        ExecutorService pool = Executors.newFixedThreadPool(nThreads);
        CountDownLatch latch = new CountDownLatch(nThreads);
        for (int i = 0; i < 1; i++) {
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
    public void receive() throws Exception {
        Consumer consumer = consumerFactory.createP2pConsumer("hello");
        consumer.receive();
        Thread.currentThread().join();
    }

}
