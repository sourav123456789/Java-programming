package queue;

import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

/*
Metered billing API.
Steady state is 10 requests per second per key,
but a client that has been idle should be allowed a burst of 50.
Bulk endpoints cost more than one token.
 */
public class TokenBucketWithBurstTest {
    public static void main(String[] args) {
        TokenBucketWithBurst test = new TokenBucketWithBurst();
    }
}

class TokenBucketWithBurst {
    /*
      apiKey = from which client is identified
      tokens = allowed request per second 10
      nowNanos = timestamp now
     */
    Map<String, Capacity> map = new HashMap<>();
    // on every request the try consume will be called
    public boolean tryConsume(String apiKey, int tokens, long nowNanos) {
        // for each user compute the capacity
        Capacity capacity = map.computeIfAbsent(apiKey, k -> new Capacity(
                0
                ,
                null,
                nowNanos
        ));

        if(nowNanos - (capacity.lastSentTime == null ? 0 : capacity.lastSentTime) > 1) {
            capacity.tokens = Math.min((int)(nowNanos - (capacity.lastSentTime == null ? 0 : capacity.lastSentTime)) * 10 , 50);
        }
        capacity.lastSentTime = nowNanos;
        capacity.currentTime = null;

        if(capacity.tokens > 0) {
            capacity.tokens--;
            return true;
        }
        return false;
    }

}
class Capacity {
    int tokens;
    Long lastSentTime;
    Long currentTime;

    public Capacity(int tokens, Long lastSentTime , Long currentTime) {
        this.tokens = tokens;
        this.lastSentTime = lastSentTime;
        this.currentTime = currentTime;
    }

}