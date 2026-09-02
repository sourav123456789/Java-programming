package linkedlist;

import java.util.HashMap;
import java.util.Map;

public class MruCacheTest {
    public static void main(String[] args) {
        MruCache test = new MruCache(5);
        for (int i = 0; i < 10; i++) {
            test.add(i , i);
        }

        System.out.println(test.get(6));


    }
}
class MruCache {

    private class Node {
        Integer key;
        Integer value;
        Node next;
        Node prev;
        public Node(Integer key, Integer value) {
            this.key = key;
            this.value = value;
        }
    }

    private int capacity;
    private Node head = new Node(null, null);
    private Node tail = new Node(null, null);

    private Map<Integer, Node> map = new HashMap<>();

    // constructor
    public MruCache(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be positive");
        }
        this.capacity = capacity;
        this.head.next = tail;
        this.tail.prev = head;
    }

    // time complexity should be o(1)
    public int get(int key) {
        Node node = map.get(key);
        if(node == null) return -1;
        // deleteNode
        remove(node);
        map.remove(node.key);

        // addToList
        addToLast(node);
        map.put(key, node);
        return  node.value;
    }

    // time complexity should be o(1)
    public void add(int key, int value) {
        Node node = map.get(key);
        if(node != null) {
            remove(node);
            node.value = value;
            return;
        }
        // we have to add a fresh node
        Node newNode = new Node(key, value);
        if(map.size() == capacity){
            map.remove(tail.prev.key);
            remove(tail.prev);
        }
        addToLast(newNode);
        map.put(key, newNode);
    }

    private void remove(Node n) {
      n.prev.next = n.next;
      n.next.prev = n.prev;
    }

    private void addToLast(Node n) {
        n.prev = tail.prev;
        n.next = tail;
        tail.prev.next = n;
        tail.prev = n;
    }
}
