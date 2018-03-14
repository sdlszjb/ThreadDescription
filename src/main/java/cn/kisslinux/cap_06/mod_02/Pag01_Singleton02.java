package cn.kisslinux.cap_06.mod_02;

/**
 * 懒汉模式
 *
 * @author 庄壮壮 Administrator
 * @since 2018-03-14 20:47
 */
public class Pag01_Singleton02 {

    private static Pag01_Singleton02 instance = null;

    // 正常应该是private
    // private Pag01_Singleton02() {
    public Pag01_Singleton02() {

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
