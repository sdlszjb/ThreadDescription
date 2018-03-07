package cn.kisslinux.cap_02.mod_01;

/**
 * @author 庄壮壮 Administrator
 * @since 2018-03-07 21:41
 */
public class Pag01_Static_Class {

    synchronized public static void staticMethod1() {
        try {
            System.out.println(Thread.currentThread().getName() + " method1 start time: " + System.currentTimeMillis());
            Thread.sleep(3000);
            System.out.println(Thread.currentThread().getName() + " method1 start time: " + System.currentTimeMillis());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    synchronized public static void staticMethod2() {
        try {
            System.out.println(Thread.currentThread().getName() + " method2 start time: " + System.currentTimeMillis());
            Thread.sleep(3000);
            System.out.println(Thread.currentThread().getName() + " method2 start time: " + System.currentTimeMillis());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    synchronized public void method3() {
        try {
            System.out.println(Thread.currentThread().getName() + " method3 start time: " + System.currentTimeMillis());
            Thread.sleep(3000);
            System.out.println(Thread.currentThread().getName() + " method3 start time: " + System.currentTimeMillis());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
