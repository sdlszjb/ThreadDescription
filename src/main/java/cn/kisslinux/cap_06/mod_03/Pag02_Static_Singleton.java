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
