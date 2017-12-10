package yz.mq.producer.rabbit;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import yz.serializer.api.Serializer;

@Slf4j
@EqualsAndHashCode(callSuper = true)
public abstract class AbsP2pProducer extends AbsProducer {

    public AbsP2pProducer(Connection connection, String queue, Serializer serializer) {
        super(connection, "", queue, serializer);
    }

    @Override
    protected Channel prepareChannel() throws Exception {
        Channel channel = this.getConnection().createChannel();
        channel.confirmSelect();
        channel.addConfirmListener((deliveryTag, multiple) -> log.debug("confirm ack: deliveryTag:{},multiple:{}", deliveryTag, multiple),
                (deliveryTag, multiple) -> log.debug("confirm nack: deliveryTag:{},multiple:{}", deliveryTag, multiple));
        channel.addReturnListener((replyCode, replyText, exchange, routingKey, properties, body) -> log.debug("return {},{},{},{},{},{}", replyCode, replyText, exchange, routingKey, properties, body));
        String queue = getRoutingKey();
        channel.queueDeclare(queue, true, false, true, null);
        return channel;
    }

    @Override
    protected void destroyChannel(Channel channel) throws Exception {
        channel.waitForConfirmsOrDie();
        channel.close();
    }

}
