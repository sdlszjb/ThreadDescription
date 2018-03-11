package cn.kisslinux.cap_03.mod_01;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 庄壮壮 Administrator
 * @since 2018-03-10 17:37
 */
public class MyList {
    private static List<String> list = new ArrayList<>();

    public static void add() {
        list.add("Something.");
    }

    public static int size() {
        return list.size();
    }
}
