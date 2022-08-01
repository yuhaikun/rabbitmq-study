package com.atguigu.rabbitmq.one;

import com.rabbitmq.client.*;


/**
 * @description TODO
 * @authors XiaoYu
 * @date 2022/7/9 19:47
 * <p>
 * 消费者  接收消息
 */
public class Consumer {
    //    队列的名称
    public static final String QUEUE_NAME = "hello";


    //    接收的消息
    public static void main(String[] args) throws Exception {
//        创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("1.12.254.160");
        factory.setUsername("admin");
        factory.setPassword("admin");

        Connection connection = factory.newConnection();

        Channel channel = connection.createChannel();

//        处理推送过来消息的回调函数
        DeliverCallback deliverCallback = (var1,var2) -> {


            System.out.println(new String(var2.getBody()));
        };
//        取消消息时的回调
        CancelCallback cancelCallback = var1 -> {
            System.out.println("消息消费被中断");

        };
        /**
         * 消费者消费消息
         * 1. 消费哪个队列
         * 2. 消费成功之后是否要自动应答 true 代表自动应答 false 代表手动应答
         * 3. 消费者成功消费的回调
         * 4. 消费者取消消费的回调
         */
        channel.basicConsume(QUEUE_NAME,true,deliverCallback,cancelCallback);

    }

}
