package com.atguigu.rabbitmq.four;

import com.atguigu.rabbitmq.utils.RabbitMqUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;

import java.util.UUID;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * @description TODO
 * @authors XiaoYu
 * @date 2022/7/11 10:47
 * 发布确认模式：（比较确认时间，确认哪一种最好）
 * 1、单个确认
 * 2、批量确认
 * 3、异步批量确认
 */
public class ConfirmMessage {

    //    批量发消息的个数
    public static final int MESSAGE_COUNT = 1000;


    public static void main(String[] args) throws Exception {

//        1、单个确认
//        publishMessageIndividually();// 发布1000个单独确认消息，耗时：8005ms
//        2、批量确认
//        publishMessageBatch();//发布1000个批量确认消息，耗时：105ms
//        3、异步批量确认
        publishMessageAsync();//发布1000个异步发布确认消息，耗时：28ms
//        发布1000个异步发布确认消息，耗时：39ms
    }

    //    单个确认
    public static void publishMessageIndividually() throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
//        队列的声明
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName, true, false, false, null);
//        开启发布确认
        channel.confirmSelect();
//        开始时间
        long begin = System.currentTimeMillis();

//        批量发消息
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String message = i + "";
            channel.basicPublish("", queueName, null, message.getBytes());
//             单个消息就马上进行发布确认
            boolean flag = channel.waitForConfirms();
            if (flag) {
                System.out.println("消息发送成功");
            }
        }
//        结束时间
        long end = System.currentTimeMillis();
        System.out.println("发布" + MESSAGE_COUNT + "个单独确认消息，耗时：" + (end - begin) + "ms");


    }

    //    批量发布确认
    public static void publishMessageBatch() throws Exception {

        Channel channel = RabbitMqUtils.getChannel();

        String queueName = UUID.randomUUID().toString();

        channel.queueDeclare(queueName, true, false, false, null);

        channel.confirmSelect();

        long begin = System.currentTimeMillis();

//        批量确认消息大小
        int batchSize = 100;
//        批量发送消息，批量发布确认
        for (int i = 1; i <= MESSAGE_COUNT; i++) {
            String message = i + "";
            channel.basicPublish("", queueName, null, message.getBytes());

//            判断达到100条消息的时候，批量确认一次
            if (i % batchSize == 0) {
//                发布确认
                boolean flag = channel.waitForConfirms();
                if (flag) {
                    System.out.println("消息发送成功");
                }
            }
        }

        //        结束时间
        long end = System.currentTimeMillis();
        System.out.println("发布" + MESSAGE_COUNT + "个批量确认消息，耗时：" + (end - begin) + "ms");

    }

    //    异步发布确认
    public static void publishMessageAsync() throws Exception {
        Channel channel = RabbitMqUtils.getChannel();

        String queueName = UUID.randomUUID().toString();

        channel.queueDeclare(queueName, true, false, false, null);

        channel.confirmSelect();


        /**
         * 线程安全有序的一个哈希表 适用于高并发的情况下
         * 1、 轻松的将序号与信息进行关联
         * 2、 轻松的批量删除条目 只要给到序号
         * 3、 支持高并发（多线程）
         *
         * key是有序的
         * 添加、删除、查找操作都是基于跳表结构（skip list）实现的
         * key和value都不能为null
         */
        ConcurrentSkipListMap<Long, String> outstandingConfirms =
                new ConcurrentSkipListMap<Long, String>();


//        准备消息的监听器，监听哪些消息成功了 哪些消息失败了
        /**
         * 消息确认成功 回调函数
         * 1.消息的标记
         * 2.是否为批量确认
         */
//        消息确认成功 回调函数
        ConfirmCallback ackCallback = (var1, var3) -> {
//            如果是批量删除
            if (var3) {
//            2：删除掉已经确认的消息 剩下的就是未确认的消息
                ConcurrentNavigableMap<Long, String> confirmed =
                        outstandingConfirms.headMap(var1);  //headmap截取的是指针指向地址，是浅拷贝，删视图的内容就会删除原地址的内容
                confirmed.clear();
                System.out.println("确认的消息：" + var1);
            }else {
                outstandingConfirms.remove(var1);
            }
        };
//        消息确认失败 回调函数
        /**
         * 消息确认失败 回调函数
         *
         * 1.消息的标记
         * 2.是否为批量确认
         */
        ConfirmCallback nackCallback = (var1, var3) -> {
//            3：打印一下未确认的消息都有哪些
            String message = outstandingConfirms.get(var1);
            System.out.println("未确认的消息是："+message+"未确认的消息tag：" + var1);
        };
        ;
        channel.addConfirmListener(ackCallback, nackCallback);//异步通知
//        批量发送消息
        long begin = System.currentTimeMillis();
        for (int i = 0; i < MESSAGE_COUNT; i++) {

            String message = "消息" + i;

            outstandingConfirms.put(channel.getNextPublishSeqNo(), message);
            channel.basicPublish("", queueName, null, message.getBytes());
//            1：此处记录下所有要发送的消息 消息的总和


        }


        long end = System.currentTimeMillis();

        System.out.println("发布" + MESSAGE_COUNT + "个异步发布确认消息，耗时：" + (end - begin) + "ms");

        /**
         * 监听器一开始就已经开始工作，但是监听器监听的速度没有生产者发送消息的速度块，当监听器监听到一百多的时候生产者的1000条就已经发完了
         */
    }


}
