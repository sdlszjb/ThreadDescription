package cn.kisslinux.cap_03.mod_01;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 庄壮壮 Administrator
 * @since 2018-03-11 17:07
 */
public class Pag04_Producer_consumer_02 {
    @Test
    public void testClient() throws InterruptedException {
        ProductStack stack = new ProductStack();
        Producer producer = new Producer(stack);
        Consumer consumer = new Consumer(stack);

        Thread t1 = new Thread(producer);
        Thread t2 = new Thread(producer);
        Thread t3 = new Thread(producer);
        Thread t4 = new Thread(producer);
        Thread t5 = new Thread(producer);

        t1.setName("t1");
        t2.setName("t2");
        t3.setName("t3");
        t4.setName("t4");
        t5.setName("t5");

        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();

        Thread c1 = new Thread(consumer);
        Thread c2 = new Thread(consumer);

        c1.setName("c1");
        c2.setName("c2");

        c1.start();
        c2.start();

        Thread.sleep(50000);
    }
}

/**
 * 产品类
 */
class Product {
    private String id;

    public Product(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return id;
    }
}

/**
 * 产品缓冲池
 */
class ProductStack {
    public static final int MAX_SIZE = 10;
    private int index = 0;
    private List<Product> pool = new ArrayList<>();

    synchronized public void push(Product product) {
        try {
            while (index == MAX_SIZE) { // 如果已经达到池的最大容量，那么暂停向里面放入产品
                System.out.println("Push操作中的：" + Thread.currentThread().getName() + "线程处于wait状态！");
                this.wait();
                System.out.println("Push操作中的：" + Thread.currentThread().getName() + "线程处于被唤醒了！");
            }
            pool.add(product);
            index++;
            System.out.println("Add project. Now size is: " + pool.size());
            this.notify(); // 通知其他线程，当前的池已经放入了新的产品
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    synchronized public Product pop() {
        try {
            while (index == 0) {
                System.out.println("Pop操作中的：" + Thread.currentThread().getName() + "线程处于wait状态！");
                this.wait(); // 如果池中已经木有产品，等待
                System.out.println("Pop操作中的：" + Thread.currentThread().getName() + "线程处于被唤醒了！");
            }
            index--;
            Product product = pool.remove(index);
            pool.remove(product);
            System.out.println("Del project. Now size is: " + pool.size());
            this.notify(); // 如果池中有产品，那么取出一个产品，并告诉其他线程，我已经取出一个产品了。
            return product;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}

/**
 * 生产者
 */
class Producer implements Runnable {

    private ProductStack stack;

    public Producer(ProductStack stack) {
        this.stack = stack;
    }

    @Override
    public void run() {
        while (true) {
            Product product = new Product(System.currentTimeMillis() + "");
            System.out.println(Thread.currentThread().getName() + " P: " + product);
            stack.push(product);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

/**
 * 消费者
 */
class Consumer implements Runnable {
    private ProductStack stack;

    public Consumer(ProductStack stack) {
        this.stack = stack;
    }

    @Override
    public void run() {
        while (true) {
            Product product = stack.pop();
            System.out.println(Thread.currentThread().getName() + " C: " + product);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
