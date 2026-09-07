package rotationGenerationSet;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class RotationDeduplicationSimple1Test {
    public static void main(String[] args) {
        RotationDeduplicationSimple t = new
                RotationDeduplicationSimple(15 * 60 * 1000 , 5);

    }
}

class RotationDeduplicationSimple {

    private Set<String>[] buckets;
    private long[] bucketEpoch;
    private long bucketWindowMs;
    private int bucketSize;
    private final Object lock = new Object();


    public RotationDeduplicationSimple(long retentionMs , int slices) {
        this.bucketWindowMs = retentionMs / slices;
        bucketSize = slices + 1;
        buckets = new Set[bucketSize];
        bucketEpoch = new long[bucketSize];
        Arrays.fill(bucketEpoch, Long.MIN_VALUE);
        for (int i = 0; i < bucketSize; i++) {
            buckets[i] = new HashSet<>();
        }
    }

    public boolean isDuplicate(String eventId , long now) {
        synchronized (lock) {
            long epoach = now / bucketWindowMs;
            long oldest = epoach - (bucketSize - 1);
            Set<String> curr = currentBucket(epoach);

            for (int k = 1; k < bucketSize; k++) {
                long e = epoach - k;
                if (e < oldest) {
                    break;
                }
                if (bucketEpoch[k] != e) continue;
                int index = (int) Math.floorMod(e, bucketSize);
                return buckets[index].contains(eventId);
            }
            return !curr.add(eventId);
        }
    }

    private Set<String> currentBucket(long epoach) {
        int index = (int) Math.floorMod(epoach, bucketSize);
        // if we are at the current window
        if(epoach == bucketEpoch[index]) {
            return buckets[index];
        }
        bucketEpoch[index] = epoach;
        buckets[index] = new HashSet<>();
        return buckets[index];
    }


}
