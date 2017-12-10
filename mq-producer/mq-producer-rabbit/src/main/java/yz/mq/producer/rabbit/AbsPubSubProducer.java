package yz.mq.producer.rabbit;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import yz.serializer.api.Serializer;

@Slf4j
@EqualsAndHashCode(callSuper = true)
public abstract class AbsPubSubProducer extends AbsProducer {
    private BuiltinExchangeType type;

    public AbsPubSubProducer(Connection connection, String exchange, String routingKey, Serializer serializer, BuiltinExchangeType type) {
        super(connection, exchange, routingKey, serializer);
        this.type = type;
    }


    private BuiltinExchangeType getType() {
        return type;
    }

    @Override
    protected Channel prepareChannel() throws Exception {
        Channel channel = getConnection().createChannel();
        channel.confirmSelect();
        channel.addConfirmListener((deliveryTag, multiple) -> log.debug("confirm ack: deliveryTag:{},multiple:{}", deliveryTag, multiple),
                (deliveryTag, multiple) -> log.debug("confirm nack: deliveryTag:{},multiple:{}", deliveryTag, multiple));
        channel.addReturnListener((replyCode, replyText, exchange, routingKey, properties, body) -> log.debug("return {},{},{},{},{},{}", replyCode, replyText, exchange, routingKey, properties, body));
        channel.exchangeDeclare(getExchange(), this.getType(), true, true, null);
        return channel;
    }

    @Override
    protected void destroyChannel(Channel channel) throws Exception {
        channel.waitForConfirmsOrDie();
        channel.close();
    }

}
