package linkedlist;

import java.util.HashMap;
import java.util.Map;

public class LruCacheTest {
    public static void main(String[] args) {
        LruCache lruCache = new LruCache(10);
        for (int i = 0; i <= 15; i++) {
            lruCache.add(i , i);
        }
        Integer i = lruCache.get(6);
        System.out.println(i);
    }
}
class LruCache {
    // we have a cache capacity , if the capacity increased , then
    // will remove the cache which is least recently used.
    class Node {
        Integer key;
        Integer value;
        Node prev;
        Node next;
        public Node(Integer key, Integer value) {
            this.key = key;
            this.value = value;
        }
    }
    private int capacity;
    private Map<Integer, Node> map = new HashMap<>();
    Node head = new Node(null , null);
    Node tail = new Node(null , null);

    public LruCache(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("capacity must be > 0");
        this.capacity = capacity;
        head.next = tail;
        tail.prev = head;
    }

    public void add(int key , int value) {
        Node node = map.get(key);
        if (node != null) {
            node.value = value;
            remove(node);
            addFirst(node);
            return;
        }
        // we have to add a fresh node
        Node newNode = new Node(key, value);
        if(capacity == map.size()) {
            Node last = tail.prev;
            remove(last);
            map.remove(last.key);
        }
        addFirst(newNode);
        map.put(key, newNode);
    }

    public Integer get(int key) {
        Node node = map.get(key);
        if(node == null) return null;
        remove(node);
        addFirst(node);
        return node.value;
    }

    private void remove(Node n) {
        n.prev.next = n.next;
        n.next.prev = n.prev;
    }

    private void addFirst(Node node) {
        node.prev = head;
        node.next = head.next;
        head.next.prev = node;
        head.next = node;
    }
}
