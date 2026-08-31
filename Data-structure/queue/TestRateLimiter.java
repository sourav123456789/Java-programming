package queue;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class TestRateLimiter {
    public static void main(String[] args) {
        RateLimiter r1 = new RateLimiter(3 , 1000L);

        System.out.println(r1.isAllowed("A" , 100L));
        System.out.println(r1.isAllowed("A" , 200L));
        System.out.println(r1.isAllowed("A" , 300L));
        System.out.println(r1.isAllowed("A" , 400L));
        System.out.println(r1.isAllowed("A" , 1500L));
        System.out.println(r1.isAllowed("A" , 1800L));
        System.out.println(r1.isAllowed("B" , 100L));
    }
}
class RateLimiter {
    private int limit;
    private Long timeWindow;
    Map<String , Queue<Long>> requests = new HashMap<>();

    public RateLimiter(int limit,  long timeWindow) {
        this.limit = limit;
        this.timeWindow = timeWindow;
    }

    public boolean isAllowed(String clientId, long timestamp) {
        // if null , created the linkedList
        requests.computeIfAbsent(clientId , k -> new LinkedList<>());
        // get the linkedlist of timestamps
        Queue<Long> timeStamps = requests.get(clientId);
        while(!timeStamps.isEmpty() && timeStamps.peek() < timestamp - timeWindow) {
            timeStamps.poll();
        }
        if(timeStamps.size() < limit) {
            requests.computeIfAbsent(clientId , k -> new LinkedList<>()).add(timestamp);
            return true;
        }
        return false;
    }

}
