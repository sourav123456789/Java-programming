package circuitBreaker;

import java.util.Arrays;

public class CircuitBreakerTest {
    public static void main(String[] args) {
        CircuitBreaker breaker = new CircuitBreaker(10 , 50 , 20);
    }
}
class CircuitBreaker {

    private final int bucketSize;
    private final long[] bucketEpoach;
    private final int[] failures;
    private final int[] totalRequests;
    private final int errorThreshold;
    private final int sampleSize;

    public CircuitBreaker(int bucketSize , int errorThreshold , int sampleSize) {
        this.bucketSize = bucketSize;
        bucketEpoach = new long[bucketSize] ;
        failures = new int[bucketSize];
        totalRequests = new int[bucketSize];
        this.errorThreshold = errorThreshold;
        this.sampleSize = sampleSize;
        Arrays.fill(bucketEpoach, Integer.MAX_VALUE);
    }

    public boolean allow(long now) {
        int failurCount = 0;
        int totalRequestCount = 0;
        long epoch = now / 1000;
        long oldest = epoch - (bucketSize - 1);
        for (int k = 0; k < bucketSize; k++) {
            long e = epoch - k;
            if(e < oldest) {
                break;
            }
            int index = (int) Math.floorMod(e, bucketSize);
            if(bucketEpoach[index] != e) continue;
            failurCount += failures[index];
            totalRequestCount += totalRequests[index];
        }

        if(totalRequestCount < sampleSize) {
            return true;
        }

        if(failurCount * 100L >  (long) errorThreshold * totalRequestCount) {
            return false;
        }
        return true;
    }

    public void record(boolean success, long now) {
        long epoch = now / 1000;
        int index = Math.floorMod(epoch , bucketSize);
        // rotation
        if(bucketEpoach[index] != epoch) {
            bucketEpoach[index] = epoch;
            failures[index] = success ? 0 : 1;
            totalRequests[index] = 1;
        }
        // with in the same second
        else {
           failures[index] = success ? failures[index] : failures[index] + 1;
           totalRequests[index]++;
        }
    }
}
