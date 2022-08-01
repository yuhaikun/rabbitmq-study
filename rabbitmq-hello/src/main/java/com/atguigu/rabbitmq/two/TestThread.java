package com.atguigu.rabbitmq.two;

/**
 * @description TODO
 * @authors XiaoYu
 * @date 2022/7/10 14:32
 */
public class TestThread {

    public static void main(String[] args) {
        ThreadDemo threadDemo1 = new ThreadDemo("C1");
        threadDemo1.start();
        ThreadDemo threadDemo2 = new ThreadDemo("C2");
        threadDemo2.start();
        while(true) {
            threadDemo1.run();
            threadDemo2.run();
        }
    }
}
