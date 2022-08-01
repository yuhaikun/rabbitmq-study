package com.atguigu.rabbitmq.one;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.*;
import java.util.Map;

/**
 * @description TODO
 * @authors XiaoYu
 * @date 2022/7/9 16:50
 * 生产者：发消息
 */
public class Producer {
    //        队列名称
    public static final String QUEUE_NAME = "hello1";

    //    发消息
    public static void main(String[] args) throws Exception {
//         创建一个连接工厂
        ConnectionFactory factory = new ConnectionFactory();
//        工厂IP 连接RabbitMQ的队列
        factory.setHost("1.12.254.160");
//        用户名
        factory.setUsername("admin");
//        密码
        factory.setPassword("admin");
//        创建连接

        Connection connection = factory.newConnection();

//        获取信道
        Channel channel1 = connection.createChannel();
        /**
         * 生成一个队列
         * 1.队列名称
         * 2.队列里面的消息是否持久化（磁盘）默认情况消息存储在内存中
         * 3.该队列是否只供一个消费者进行消费，是否进行消息共享，true代表可以多个消费者进行消费
         * 4.是否自动删除 最后一个消费者端开启连接以后，该队列是否自动删除，true自动删除，false不自动删除
         * 5.其他参数
         */
        channel1.queueDeclare(QUEUE_NAME, false, false, false, null);
//        发消息
        String message = "hello world";

//        JSONObject obj = new JSONObject();
//        obj.put("name","foo");
//        obj.put("num",new Integer(100));
//        obj.put("balance",new Double(1000.21));
//        obj.put("is_vip",new Boolean(true));
//        obj.put("nickname",null);




//        Scanner scanner = new Scanner(System.in);
//        while(scanner.hasNext()) {
//            String message = scanner.next();
//            channel1.basicPublish("",QUEUE_NAME,null,message.getBytes());
//
//            System.out.println("消息发送完毕！");
//        }
        /**
         *  发送一个消息
         *  1. 发送到哪个交换机
         *  2. 路由的key值是哪个，本次是队列的名称
         *  3. 其他参数
         *  4。 发送消息的消息体
         */
//        channel1.basicPublish("", QUEUE_NAME, null, obj.toString().getBytes("utf-8"));



        String str = readJsonFile("E:\\projects\\dataHandle\\littleTest.json");
//        System.out.println(str);
        channel1.basicPublish("", QUEUE_NAME, null, str.getBytes());
        System.out.println("消息发送完毕！");

    }

    //读取json文件
    public static String readJsonFile(String Filename) {
        String jsonStr = "";
        try {
            File jsonFile = new File(Filename);
            FileReader fileReader = new FileReader(jsonFile);
            Reader reader = new InputStreamReader(new FileInputStream(jsonFile), "utf-8");
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            fileReader.close();
            reader.close();
            jsonStr = sb.toString();
//            System.out.println(jsonStr.getClass().getName());
            return jsonStr;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
