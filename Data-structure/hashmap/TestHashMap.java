package hashmap;

import java.util.HashMap;
import java.util.Map;

public class TestHashMap {
    public static void main(String[] args) {
        Map<Integer, Integer> map = new HashMap<>();
        map.put(1, 1);
        map.put(2, 2);
        System.out.println(map.size());
        map.remove(1);
        map.remove(2);
        System.out.println(map.size());
    }
}
