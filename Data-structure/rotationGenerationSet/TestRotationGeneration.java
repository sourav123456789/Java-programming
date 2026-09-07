package rotationGenerationSet;

public class TestRotationGeneration {
    public static void main(String[] args) {
        long bucketMs = 180_000;
        long now = System.currentTimeMillis();
        long epoch = now / bucketMs;
        System.out.println(now);
        System.out.println(epoch);
    }
}
