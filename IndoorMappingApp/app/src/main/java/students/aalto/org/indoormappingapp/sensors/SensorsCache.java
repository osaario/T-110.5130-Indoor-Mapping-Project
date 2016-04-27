package students.aalto.org.indoormappingapp.sensors;

import java.util.ArrayList;
import java.util.List;

/**
 * Caches limited number of sensor readings for search by timestamp.
 */
public class SensorsCache {

    private int size;
    private boolean noCycling;
    private int p;
    private int n;
    private SensorsSnapshot[] cache;

    public SensorsCache(int size) {
        this(size, false);
    }

    public SensorsCache(int size, boolean noCycling) {
        this.size = size;
        this.noCycling = noCycling;
        p = 0;
        n = 0;
        cache = new SensorsSnapshot[size];
    }

    public void clear() {
        for (int i = 0; i < size; i++) {
            cache[i] = null;
        }
        p = 0;
        n = 0;
    }

    public void add(SensorsSnapshot sensors) {
        if (!noCycling || n < size) {
            cache[p] = sensors;
            p = (p + 1) % size;
            n++;
        }
    }

    public int count() {
        return n;
    }

    public SensorsSnapshot search(long timestamp) {
        for (int i = 0; i < size; i++) {
            SensorsSnapshot s = last(i);
            if (s == null) {
                return null;
            }
            if (s.Timestamp <= timestamp) {
                return s;
            }
        }
        return cache[(p + 1) % size];
    }

    public SensorsSnapshot last(int skip) {
        return cache[(size + p - skip) % size];
    }

    public List<SensorsSnapshot> getList() {
        ArrayList<SensorsSnapshot> list = new ArrayList<>(Math.min(n, size));
        for (int i = 0; i < Math.min(n, size); i++) {
            list.add(cache[i]);
        }
        return list;
    }
}
