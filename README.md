
RabbitMQ是一个开源的AMQP(高级消息队列协议)实现，服务器端用Erlang语言编写，支持多种客户端，如Java、Python等。

RabbitMQ是一个用于接收、存储、转发消息的代理(broker)。

## 部署
### 下载

[Erlang下载](http://www.erlang.org/downloads)

[RabbitMQ下载](https://www.rabbitmq.com/download.html)

### 远程访问

默认guest用户只能本地访问，故需要添加新用户

    rabbitmqctl add_user admin admin #添加用户admin，密码admin
    rabbitmqctl set_user_tags admin administrator #设置用户角色
    rabbitmqctl set_permissions -p / admin ".*" ".*" ".*" #设置用户权限
    
并编辑rabbitmq-env.conf，将NODE_IP_ADDRESS设置为本级ip

    ➜  etc cat ./rabbitmq/rabbitmq-env.conf
    CONFIG_FILE=/usr/local/etc/rabbitmq/rabbitmq
    NODE_IP_ADDRESS=192.168.1.37
    NODENAME=rabbit@localhost
    
这样就可以远程访问了

## 基础概念

### Producer

生产者，生产消息，将消息发送到RabbitMQ

### Consumer 

消费者，消费消息，等待并处理来自RabbitMQ的消息

### Queue

消息队列，存储消息

### Exchange

消息路由，根据routingKey和Exchange类型将消息发送到不同的Queue


### Exchange Type

有4种内建类型(`com.rabbitmq.client.BuiltinExchangeType`)

    DIRECT("direct"), FANOUT("fanout"), TOPIC("topic"), HEADERS("headers");
    
* DIRECT
生产者routing key与消费者routing key完全匹配的模式
* FANOUT
忽略routing key，即将消息发送到所有绑定到该Exchange的Queue
* TOPIC
将生产者routing key与消费者routing key进行模糊匹配：

    `*` 仅匹配一个单词
    
    `#` 匹配零个到多个单词

* HEADERS
没看这个😄

### routing key

routing key是一个以`.`分割的字符串。
生产者发送消息到Exchange时，需要一个routing key并配合Exchange类型来配置消息路由规则，即指定消息流向哪里。
routing key的长度最大为255bytes。
 
同生产者，消费者消费消息时，也需要一个routing key(或者叫binding key)，来绑定Queue到Exchange。

当生产者routing key与消费者routing key根据Exchange Type能匹配到时，消息将会被路由到消费者所绑定的Queue中。

## Api

    com.rabbitmq.client.ConnectionFactory
    com.rabbitmq.client.Connection
    com.rabbitmq.client.Channel

## 示例

[看代码](https://github.com/liuyazong/mq-app)

## 消息可靠投递

RabbitMQ怎样避免消息丢失？

### 生产者

#### 事务

据说性能不好，暂时无视

#### Publisher Confirms

发布确认，broker会在接收到消息并正确处理后给客户端发送ack消息，不可与事务混合使用。
消息何时被确认？

* 对于无路由的消息，broker会在Exchange确认该消息不会被路由到任何Queue时发送basic.ack回客户端进行确认。

    如果客户端发送消息使用了mandatory参数，则先发送basic.return再发送basic.ack回客户端进行确认。
    可以在channel添加ReturnListener来接收broker返回的无路由的消息做相应的处理。
    
* 对于可路由的消息，当所有Queue已接收到消息后，broker发送basic.ack回客户端进行确认。
    对于要发送到durable queue的消息，消息持久化到磁盘后broker发送确认。
    对于mirrored queues，所有队列都接收该消息后broker发送确认。

### 消费者

对于消费者，设置autoAck为false，只有当消息处理成功才发送ack；若消费失败，则发送nack（注意requeue参数）

## 完

[代码](https://github.com/liuyazong/mq-app)
