# 单例模式与多线程

## 立即加载/饿汉模式
```java
package cn.kisslinux.cap_06.mod_01;

import org.junit.Test;

/**
 * 饿汉式
 * @author 庄壮壮 Administrator
 * @since 2018-03-14 20:39
 */
public class Singleton01 {

    private static Singleton01 instance = new Singleton01();

    // 在实际项目中，这个应该为private
    // private Singleton01() {}

    public static Singleton01 getInstance() {
        return instance;
    }

    @Test
    public void testClient() {

        for (int i=0; i<5; i++) {
            Thread thread = new MyThread();
            thread.start();
        }
    }

    private class MyThread extends Thread {
        @Override
        public void run() {
            System.out.println(Singleton01.getInstance().hashCode());
        }
    }
}
```

## 延时加载/懒汉模式
```java
package cn.kisslinux.cap_06.mod_02;

/**
 * 懒汉模式
 * 
 * @author 庄壮壮 Administrator
 * @since 2018-03-14 20:47
 */
public class Pag01_Singleton02 {
    
    private static Pag01_Singleton02 instance = null;
    
    private Pag01_Singleton02() {
    }

    /**
     * 效率较低的懒汉模式
     * @return
     */
    synchronized public static Pag01_Singleton02 getInstance() {
        if (instance == null) {
            instance = new Pag01_Singleton02();
        }
        return instance;
    }

    /**
     * 此方法和上面的等同
     * @return
     */
    public static Pag01_Singleton02 getInstance2() {
        synchronized (Pag01_Singleton02.class) {
            if (instance == null) {
                instance = new Pag01_Singleton02();
            }
            return instance;
        }
    }

    /**
     * 即解决线程安全问题
     * 又提高了执行效率
     * @return
     */
    public static Pag01_Singleton02 getInstance3() {
        if (instance == null) {
            synchronized (Pag01_Singleton02.class) {
                if (instance == null) {
                    instance = new Pag01_Singleton02();
                }
            }
        }
        return instance;
    }
}

```

## 静态内置类实现单例模式
```java
package cn.kisslinux.cap_06.mod_03;

/**
 * 内部类形式的单例模式
 * 
 * Kotlin采用这种模式
 * 
 * @author 庄壮壮 Administrator
 * @since 2018-03-14 20:56
 */
public class Pag01_Inner_Class_Singleton {
    
    private static class SingletonHandler {
        private static Pag01_Inner_Class_Singleton instance = new Pag01_Inner_Class_Singleton();
    }
    
    private Pag01_Inner_Class_Singleton() {}
    
    public static Pag01_Inner_Class_Singleton getInstance() {
        return SingletonHandler.instance;
    }
}

```

## 序列号与反序列化的单例模式实现
```java
package cn.kisslinux.cap_06.mod_03;

import java.io.ObjectStreamException;

/**
 * 内部类形式的单例模式
 * 
 * Kotlin采用这种模式
 * 
 * @author 庄壮壮 Administrator
 * @since 2018-03-14 20:56
 */
public class Pag01_Inner_Class_Singleton {
    
    private static class SingletonHandler {
        private static Pag01_Inner_Class_Singleton instance = new Pag01_Inner_Class_Singleton();
    }
    
    private Pag01_Inner_Class_Singleton() {}
    
    public static Pag01_Inner_Class_Singleton getInstance() {
        return SingletonHandler.instance;
    }

    /**
     * 为解决反序列化对象不一致的问题，使用该方法
     * @return
     * @throws ObjectStreamException
     */
    protected Object readResolve() throws ObjectStreamException {
        return SingletonHandler.instance;
    }
}

```

## 静态代码块实现单例模式
```java
package cn.kisslinux.cap_06.mod_03;

/**
 * 使用static代码块实现单例模式
 * 
 * @author 庄壮壮 Administrator
 * @since 2018-03-14 21:04
 */
public class Pag02_Static_Singleton {
    private static Pag02_Static_Singleton instance = null;
    
    private Pag02_Static_Singleton() {
        
    }
    
    static {
        instance = new Pag02_Static_Singleton();
    }

    public static Pag02_Static_Singleton getInstance() {
        return instance;
    }
}

```