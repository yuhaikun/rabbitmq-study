package com.atguigu.rabbitmq.two;

import com.atguigu.rabbitmq.cons.Constans;
import com.atguigu.rabbitmq.utils.RabbitMqUtils;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;

/**
 * @description TODO
 * @authors XiaoYu
 * @date 2022/7/10 14:32
 */
public class ThreadDemo extends Thread {

    private String threadName;

    ThreadDemo(String name) {
        threadName = name;
        System.out.println("Creating " + threadName);
    }

    public void run() {
        Channel channel = null;
        try {
            channel = RabbitMqUtils.getChannel();
        } catch (Exception e) {
            e.printStackTrace();
        }

//        消息的接收
        DeliverCallback deliverCallback = (var1, var2) -> {
            System.out.println(threadName + "接受到的消息：" + new String(var2.getBody()));
            Thread.yield();
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        };
//        消息接收被取消时，会执行下面的内容
        CancelCallback cancelCallback = var1 -> System.out.println(var1 + "消费者取消消费接口回调逻辑");

        System.out.println(threadName + "等待接收消息...");
        try {
            channel.basicConsume(Constans.QUEUE_NAME, true, deliverCallback, cancelCallback);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        System.out.println("Starting: " + threadName);
    }
}
