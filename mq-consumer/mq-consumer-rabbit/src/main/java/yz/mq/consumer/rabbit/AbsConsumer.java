package yz.mq.consumer.rabbit;

import com.rabbitmq.client.*;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import yz.mq.consumer.api.Consumer;
import yz.mq.consumer.rabbit.handler.MessageHandler;
import yz.serializer.api.DeSerializer;

import java.io.IOException;

@Slf4j
@EqualsAndHashCode
public abstract class AbsConsumer implements Consumer {
    private Connection connection;
    private DeSerializer deSerializer;

    AbsConsumer(Connection connection, DeSerializer deSerializer) {
        this.connection = connection;
        this.deSerializer = deSerializer;
    }

    Connection getConnection() {
        return connection;
    }

    protected DeSerializer getDeSerializer() {
        return deSerializer;
    }

    @Override
    public void receive() throws Exception {
        Channel channel = this.prepareChannel();
        String queue = this.prepareQueue(channel);
        this.consume(channel, queue);
        this.destroyChannel(channel);
    }

    protected abstract String prepareQueue(Channel channel) throws Exception;

    protected abstract Channel prepareChannel() throws Exception;

    protected abstract void destroyChannel(Channel channel) throws Exception;

    protected void consume(Channel channel, String queue) throws Exception {
        channel.basicConsume(queue, false, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                try {
                    String type = properties.getHeaders().get("type").toString();
                    Object obj = getDeSerializer().deSerialize(body, Class.forName(type));
                    MessageHandler.handle(obj);
                    channel.basicAck(envelope.getDeliveryTag(), true);
                } catch (Exception e) {
                    channel.basicNack(envelope.getDeliveryTag(), true, true);
                    log.error("receive error ", e);
                }
            }
        });
    }


}
