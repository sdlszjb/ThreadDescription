# Lock的使用
- ReentrantLock的使用
- ReentrantReadWriteLock的使用。

## 使用ReentrantLock类
1. **Example**
```java
package cn.kisslinux.cap_04.mod_01;

import org.junit.Test;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author 庄壮壮 Administrator
 * @since 2018-03-12 21:56
 */
public class Pag01_ReentrantLock_Example {
    @Test
    public void testClient() throws InterruptedException {
        Service service = new Service();
        new MyThread(service).start();
        new MyThread(service).start();
        new MyThread(service).start();
        new MyThread(service).start();

        Thread.sleep(50000);
    }

    private class Service {
        private ReentrantLock lock = new ReentrantLock();

        void testMethod() {
            lock.lock();
            for (int i=0; i<5; i++) {
                System.out.println(Thread.currentThread().getName() + ": " + i);
            }
            lock.unlock();
        }
    }

    private class MyThread extends Thread {

        Service service;

        public MyThread(Service service) {
            this.service = service;
        }

        @Override
        public void run() {
            service.testMethod();
        }
    }
}
```

2. **使用Condition实现等待/通知**