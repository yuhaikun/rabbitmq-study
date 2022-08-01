package com.atguigu.rabbitmq.three;

import com.atguigu.rabbitmq.cons.Constans;
import com.atguigu.rabbitmq.utils.RabbitMqUtils;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.util.concurrent.TimeUnit;

/**
 * @description TODO
 * @authors XiaoYu
 * @date 2022/7/10 16:41
 * <p>
 * 消息在手动应答时是不丢失、放回队列中重新进行消费
 */
public class Work03 {

//    private String workName;
//
//    Work03(String name) {
//        workName = name;
//        System.out.println("Created" + workName);
//    }


    public static void main(String[] args) throws Exception {

        Channel channel = RabbitMqUtils.getChannel();

        System.out.println("C1等待接收消息处理时间较短");
//        采用手动应答

        DeliverCallback deliverCallback = (var1, var2) -> {
//            沉睡1s
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("C1接收到的消息：" + new String(var2.getBody(), "UTF-8"));

            channel.basicAck(var2.getEnvelope().getDeliveryTag(), false);

        };

        /**
         *
         * basicQos该值定义通道上允许的未确认消息的最大数量，prefetchcount设为1的话就是确认一条发送一条，这种情况下，
         * 如果是多线程作为多个消费者，谁的处理速度越快，谁就收消息越快，造成了不公平分发（能者多劳），但本质上还是轮询
         * 通常，增加预取将提高向消费者传递消息的速度，虽然自动应答传输消息速率是最佳的，但是，在这种情况下
         * 已传递但尚未处理消息的数量也会增加，从而增加了消费者的RAM消耗
         * 预取值为 1 是最保守的。当然这
         * 将使吞吐量变得很低，特别是消费者连接延迟很严重的情况下，特别是在消费者连接等待时间较长的环境
         * 中。对于大多数应用来说，稍微高一点的值将是最佳的。
         */
        channel.basicQos(2);
//            手动应答
        /**
         * 1.消息的标记 tag
         * 2.是否批量应答，false:不批量应答信道中的消息 true：批量
         */

        CancelCallback cancelCallback = var1 -> {
            System.out.println(var1 + "消费者取消消费接口回调逻辑");
        };

        channel.basicConsume(Constans.TASK_QUEUE_NAME, false, deliverCallback, cancelCallback);
    }
}
