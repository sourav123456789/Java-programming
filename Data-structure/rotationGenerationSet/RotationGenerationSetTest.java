package rotationGenerationSet;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RotationGenerationSetTest {
    public static void main(String[] args) {
        long window = 15 * 60 * 1000;
        RotationGenerationSet rotate = new RotationGenerationSet(window);

    }
}

class RotationGenerationSet {

    private long window;
    Map<String , Long> m;

    public RotationGenerationSet(long window) {
        this.window = window;
        m = new ConcurrentHashMap<>();
    }

    public boolean isDuplicate(String eventId, long now) {
        Long i = m.computeIfAbsent(eventId, k -> null);
        if(i == null) {
            m.put(eventId, now);
            return false;
        }
        // if the lastTimeStamp < window
        if(now - window > i) {
            m.put(eventId, now);
            return false;
        }
        m.put(eventId, now);
        return true;
    }


}
