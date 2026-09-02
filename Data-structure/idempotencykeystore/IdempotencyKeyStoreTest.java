package idempotencykeystore;

import java.util.*;

public class IdempotencyKeyStoreTest {
    public static void main(String[] args) {
        IdempotencyKeyStore store = new IdempotencyKeyStore(50 , 24 * 60 * 60 * 1000);

        // every time we get a /charge -> post
        Optional<StoredResponse> storedResponse = store.get("12345", 12345L);
        if(storedResponse.isPresent()) {
            System.out.println(storedResponse.get());
        }
        else {
            // make a call to db and get the latest data
            // we will update the cache.
        }
    }
}

class IdempotencyKeyStore {

    private  int max_removal;
    private int ttl;

    public IdempotencyKeyStore(int max_removal, int ttl) {
        this.max_removal = max_removal;
        this.ttl = ttl;
    }

    Map<String, StoredResponse> map = new HashMap<>();
    PriorityQueue<StoredResponse> q = new PriorityQueue<>(
            (r1, r2) -> Math.toIntExact(r1.getTimestamp() - r2.getTimestamp()));


    // o(1) t.c
    public Optional<StoredResponse> get(String key, long now) {
        StoredResponse storedResponse = map.get(key);
        if (storedResponse != null && storedResponse.getTimestamp() >= now) {
            return Optional.of(storedResponse);
        }
        return Optional.empty();
    }
    // o(log n) t.c
    public void put(String key, StoredResponse r , long now) {

        StoredResponse res = map.getOrDefault(key, null);
        if (res == null) {
            q.add(r);
            map.put(key, r);
        }
        else {
            // response exist and expired
            if (now > res.getTimestamp()) {
                res.setTimestamp(now + ttl);
                res.setResponse(r.getResponse());
            }
        }
    }


    // o(log n )scheduler method run at every 200 ms
    public void remove() {
        long now = System.currentTimeMillis();
        int count = 0;
        while (count < max_removal && !q.isEmpty() && q.peek().getTimestamp() < now) {
            StoredResponse poll = q.poll();
            map.remove(poll.getKey());
            count++;
        }
    }

    }

    class StoredResponse {

        private String key;
        private String response;
        private long timestamp;

        public StoredResponse(String key, String response, long timestamp) {
            this.key = key;
            this.timestamp = timestamp;
            this.response = response;
        }

        public String getKey() {
            return key;
        }

        public void setResponse(String response) {
            this.response = response;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public String getResponse() {
            return response;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }
