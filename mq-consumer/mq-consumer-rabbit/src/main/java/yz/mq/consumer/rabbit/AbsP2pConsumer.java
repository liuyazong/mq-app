package yz.mq.consumer.rabbit;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import yz.serializer.api.DeSerializer;

@Slf4j
@EqualsAndHashCode(callSuper = true)
public abstract class AbsP2pConsumer extends AbsConsumer {
    private String queue;

    protected AbsP2pConsumer(Connection connection, String queue, DeSerializer deSerializer) {
        super(connection, deSerializer);
        this.queue = queue;
    }

    @Override
    protected String prepareQueue(Channel channel) throws Exception {
        return channel.queueDeclare(this.queue, true, false, true, null).getQueue();
    }

    protected Channel prepareChannel() throws Exception {
        Channel channel = this.getConnection().createChannel();
        return channel;
    }


    @Override
    protected void destroyChannel(Channel channel) throws Exception {
        //do nothing
    }
}
