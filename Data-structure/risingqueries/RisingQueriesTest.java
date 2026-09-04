package risingqueries;

import java.util.*;
import java.util.stream.Collectors;

public class RisingQueriesTest {
    public static void main(String[] args) {
        RisingQueries rq = new RisingQueries(3, 10_000L);
    }
}

class RisingQueries {

    private final int k;
    private final long bucketMs;

    Map<String, Queue<Long>> m = new HashMap<>();
    PriorityQueue<Query> pq = new PriorityQueue<>((q1, q2) -> q1.getCount() - q2.getCount());


    /**
     * k = how many to surface, refreshMs = how often results may change (10_000).
     */

    public RisingQueries(int k, long bucketMs) {
        this.k = k;
        this.bucketMs = bucketMs;
    }


    /* to ingest the data */
    void observe(String query, long ts) {
        m.computeIfAbsent(query, k -> new LinkedList<>()).add(ts);
    }

    // get the top k elements
    List<String> top(int k, long now) {
        for (Map.Entry<String, Queue<Long>> entry : m.entrySet()) {
            Queue<Long> q = m.get(entry.getKey());
            while (!q.isEmpty() && q.peek() < now - bucketMs) {
                q.poll();
            }
            pq.add(new Query(entry.getKey(), q.size()));
        }


        while (!pq.isEmpty() && pq.size() > k) {
            pq.poll();
        }

        List<Query> res = new ArrayList<>(pq);
        pq.clear();
        List<String> collect = res.stream().sorted(Comparator.comparing(Query::getCount).reversed()).map(Query::getS).toList();
        return collect;
    }
}

class Query {
    String s;
    int count;

    public Query(String s, int count) {
        this.s = s;
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public String getS() {
        return s;
    }
}
