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
