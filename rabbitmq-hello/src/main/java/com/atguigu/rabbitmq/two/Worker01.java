package com.atguigu.rabbitmq.two;

import com.atguigu.rabbitmq.utils.RabbitMqUtils;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

/**
 * @description TODO
 * @authors XiaoYu
 * @date 2022/7/9 20:57
 * 这是一个工作线程（相当于之前消费者）
 */
public class Worker01 {

    //    队列的名称
    public static final String QUEUE_NAME = "hello";

    //    接收消息
    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();

//        消息的接收
        DeliverCallback deliverCallback = (var1,var2) -> System.out.println("接受到的消息："+new String(var2.getBody()));
//        消息接收被取消时，会执行下面的内容
        CancelCallback cancelCallback = var1 -> System.out.println(var1 + "消费者取消消费接口回调逻辑");

        System.out.println("C2等待接收消息...");
        channel.basicConsume(QUEUE_NAME,true,deliverCallback,cancelCallback);
    }
}
