package cn.kisslinux.cap_03.mod_01;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * 生产者 - 消费者
 * @author 庄壮壮 Administrator
 * @since 2018-03-11 13:45
 */
public class Pag03_Producer_Consumer_01 {

    @Test
    public void testClient() throws InterruptedException {
        MyStack stack = new MyStack();
        SimpleProducer simpleProducer = new SimpleProducer(stack);
        SimpleConsumer consumer = new SimpleConsumer(stack);

        P_Thread pThread = new P_Thread(simpleProducer);
        C_Thread cThread = new C_Thread(consumer);

        pThread.start();
        cThread.start();

        Thread.sleep(20000);
    }
}

class MyStack {
    private List<String> data = new ArrayList<>();

    synchronized public void push() {
        try {
            if (data.size() == 1) {
                this.wait();
            }
            data.add("AnyString = " + Math.random());
            Thread.sleep(1000);
            this.notify();
            System.out.println("push: " + data.size());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    synchronized public String pop() {
        String value = "";
        try {
            if (data.size() == 0) {
                System.out.println("Pop操作中的：" + ThreadSub.currentThread().getName() + "线程呈wait状态");
                this.wait();
            }
            value = data.get(0);
            data.remove(0);
            this.notify();
            System.out.println("Pop: " + data.size());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return value;
    }
}

class SimpleProducer {
    private MyStack stack;

    public SimpleProducer(MyStack stack) {
        this.stack = stack;
    }

    public void pushService() {
        stack.push();
    }
}

class SimpleConsumer {
    private MyStack stack;

    public SimpleConsumer(MyStack stack) {
        this.stack = stack;
    }

    public void popService() {
        String result = stack.pop();
        System.out.println("pop = " + result);
    }
}

class P_Thread extends Thread {
    private SimpleProducer producer;

    public P_Thread(SimpleProducer producer) {
        this.producer = producer;
    }

    @Override
    public void run() {
        while (true) {
            producer.pushService();
        }
    }
}

class C_Thread extends Thread {
    private SimpleConsumer consumer;

    public C_Thread(SimpleConsumer consumer) {
        this.consumer = consumer;
    }

    @Override
    public void run() {
        while (true) {
            consumer.popService();
        }
    }
}