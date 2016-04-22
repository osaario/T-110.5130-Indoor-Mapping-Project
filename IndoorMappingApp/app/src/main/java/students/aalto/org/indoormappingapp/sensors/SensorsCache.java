package students.aalto.org.indoormappingapp.sensors;

/**
 * Caches limited number of sensor readings for search by timestamp.
 */
public class SensorsCache {

    private int size;
    private int p;
    private SensorsSnapshot[] cache;

    public SensorsCache(int size) {
        this.size = size;
        cache = new SensorsSnapshot[size];
        p = 0;
    }

    public void add(SensorsSnapshot sensors) {
        cache[p] = sensors;
        p = (p + 1) % size;
    }

    public SensorsSnapshot search(long timestamp) {
        for (int i = 0; i < size; i++) {
            SensorsSnapshot s = cache[(size + p - i) % size];
            if (s != null && s.Timestamp <= timestamp) {
                return s;
            }
        }
        return cache[(p + 1) % size];
    }
}
