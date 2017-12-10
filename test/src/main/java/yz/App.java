package yz;

import com.rabbitmq.client.ConnectionFactory;
import yz.mq.consumer.api.Consumer;
import yz.mq.consumer.rabbit.RabbitConsumerFactory;
import yz.mq.consumer.rabbit.handler.MessageHandler;
import yz.mq.producer.api.Producer;
import yz.mq.producer.rabbit.RabbitProducerFactory;
import yz.serializer.api.DeSerializer;
import yz.serializer.api.Serializer;
import yz.serializer.protobuf.DeSerializerImpl;
import yz.serializer.protobuf.SerializerImpl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws Exception {

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("192.168.1.37");
        connectionFactory.setUsername("admin");
        connectionFactory.setPassword("admin");
        connectionFactory.setPort(5672);
        Serializer serializer = new SerializerImpl();
        DeSerializer deSerializer = new DeSerializerImpl();
        RabbitProducerFactory producerFactory = new RabbitProducerFactory(connectionFactory, serializer);
        RabbitConsumerFactory consumerFactory = new RabbitConsumerFactory(connectionFactory, deSerializer);
        MessageHandler.addHandler(Message.class, obj -> obj);

        String arg0 = "p";
        if (null == args || args.length == 0) {
            arg0 = "p";
        } else {
            arg0 = args[0];
        }

        switch (arg0) {
            case "p":
                int nThreads = Runtime.getRuntime().availableProcessors() << 1;
                ExecutorService pool = Executors.newFixedThreadPool(nThreads);
                Producer producer = producerFactory.createP2pProducer("test_queue");
                for (; ; ) {
                    pool.execute(() -> {
                        try {
                            producer.send(new Message());
                        } catch (Exception e) {
                        }
                    });
                }
            case "c":
                Consumer consumer = consumerFactory.createP2pConsumer("test_queue");
                consumer.receive();
                break;
            default:
                break;
        }

    }
}
