# MQ

RabbitMQ是一个开源的AMQP(高级消息队列协议)实现，服务器端用Erlang语言编写，支持多种客户端，如Java、Python等。

RabbitMQ是一个用于接收、存储、转发消息的代理。

## 部署
### 下载

[Erlang下载]()

[RabbitMQ下载](https://www.rabbitmq.com/download.html)

### 远程访问

默认用户只能本地访问，故需要添加新用户

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

### routing key

生产者发送消息到Exchange时，需要一个routing key并配合Exchange类型来配置消息路由规则，即指定消息流向哪里或者被丢弃。
routing key的长度最大为255bytes。
 
消费者消费消息时，也需要 
## 示例
## 消息可靠投递