package client;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 彦祖 .
 */
public class TestClass {


    public static void main(String[] args) {
        Map map = new HashMap();
        map.put(1,111);
        WeakReference<Map> sr = new WeakReference<>(map);
        System.out.println(sr.get().get(1));
        map = null;
        System.out.println(sr.get().get(1));
    }
}
