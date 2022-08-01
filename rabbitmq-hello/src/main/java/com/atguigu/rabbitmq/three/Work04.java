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
public class Work04 {

//    private String workName;
//
//    Work03(String name) {
//        workName = name;
//        System.out.println("Created" + workName);
//    }


    public static void main(String[] args) throws Exception {

        Channel channel = RabbitMqUtils.getChannel();

        System.out.println("C2等待接收消息处理时间较长");
//        采用手动应答

        DeliverCallback deliverCallback = (var1, var2) -> {
//            沉睡1s
            try {
                TimeUnit.SECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("C2接收到的消息：" + new String(var2.getBody(),"UTF-8"));

//            手动应答
            /**
             * 1.消息的标记 tag
             * 2.是否批量应答，false:不批量应答信道中的消息 true：批量
             */
            channel.basicAck(var2.getEnvelope().getDeliveryTag(),false);

        };
//        不公平分发 prefetchCount = 1；
//        预取值是5
        channel.basicQos(5);
        CancelCallback cancelCallback = var1 -> {
            System.out.println(var1 + "消费者取消消费接口回调逻辑");
        };

        channel.basicConsume(Constans.TASK_QUEUE_NAME, false, deliverCallback, cancelCallback);
    }
}
