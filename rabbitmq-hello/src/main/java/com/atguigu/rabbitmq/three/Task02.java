package com.atguigu.rabbitmq.three;

import com.atguigu.rabbitmq.cons.Constans;
import com.atguigu.rabbitmq.utils.RabbitMqUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;

import java.util.Scanner;

/**
 * @description TODO
 * @authors XiaoYu
 * @date 2022/7/10 16:25
 * <p>
 * target:  消息在手动应答时是不丢失的，并且会放回队列中重新消费
 * 生产者
 */
public class Task02 {

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
//        信道开启发布确认
        channel.confirmSelect();
//        声明队列
        channel.queueDeclare(Constans.TASK_QUEUE_NAME, true, false, false, null);
//        从控制台中输入信息
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String message = scanner.next();
//            设置生产者发送消息为持久化消息（要求保存到磁盘上）
            channel.basicPublish("", Constans.TASK_QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes("UTF-8"));

            System.out.println("生产者发出消息：" + message);



        }
    }

}
