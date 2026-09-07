
package rotationGenerationSet;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Same rotating-generation idea as RotatingDedupe, but each bucket is a
 * plain java.util.Set<Long> instead of a hand-rolled long[] hash table.
 *
 * Simpler to read, ~9x more memory per id (measured: 72 bytes/entry vs 8),
 * because every fingerprint is a boxed Long sitting inside a HashMap.Node
 * instead of a raw long packed into an array. Fine if your budget has room;
 * not fine at the 128MB / ~3.5M-id scale this problem started from.
 */
public final class RotatingDedupeSimple {

    // where we store our request
    private final Set<Long>[] buckets;
    private final long[] bucketEpoch;
    // per buckets[i] time range
    private final long bucketMs;
    // size of the buckets array and bucketEpoach array
    private final int nBuckets;
    private final Object rotationLock = new Object();

    @SuppressWarnings("unchecked")
    public RotatingDedupeSimple(long retentionMs, int slices) {
        this.bucketMs = retentionMs / slices;
        this.nBuckets = slices + 1;
        this.buckets = new Set[nBuckets];
        this.bucketEpoch = new long[nBuckets];
        Arrays.fill(bucketEpoch, Long.MIN_VALUE);
        for (int i = 0; i < nBuckets; i++) {
            buckets[i] = ConcurrentHashMap.newKeySet(); // thread-safe Set<Long>
        }
    }

    public boolean isDuplicate(String eventId, long now) {
        long epoch = now / bucketMs;
        long oldest = epoch - (nBuckets - 1);
        long fp = fingerprint(eventId);
        Set<Long> cur = currentBucket(epoch);

        for (int k = 1; k < nBuckets; k++) {       // skip k=0, that's `cur`
            long e = epoch - k;
            if (e < oldest) break;
            int idx = (int) Math.floorMod(e, nBuckets);
            if (bucketEpoch[idx] != e) continue;        // stale slot, not this epoch's data
            if (buckets[idx].contains(fp)) return true;
        }

        // Set.add() already IS "check and insert in one call":
        // returns true if fp was newly added, false if it was already there.
        return !cur.add(fp);
    }

    private Set<Long> currentBucket(long epoch) {
        int idx = (int) Math.floorMod(epoch, nBuckets);
        if (bucketEpoch[idx] == epoch) return buckets[idx];   // fast path, no lock
        synchronized (rotationLock) {
            if (bucketEpoch[idx] != epoch) {
                buckets[idx] = ConcurrentHashMap.newKeySet();  // O(1): old set just becomes garbage
                bucketEpoch[idx] = epoch;
            }
        }
        return buckets[idx];
    }

    private static long fingerprint(String eventId) {
        UUID u = UUID.fromString(eventId);              // allocates, unlike Fingerprints.of() earlier
        return u.getMostSignificantBits() ^ u.getLeastSignificantBits();
    }
}

class RotatingDedupeTest {
    public static void main(String[] args) {

    }
}