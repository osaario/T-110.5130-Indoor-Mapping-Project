package students.aalto.org.indoormappingapp.sensors;

/**
 * Holds a snapshot of all sensor values.
 */
public class SensorsSnapshot {
    public long Timestamp;
    public float[] Orientation;
    public float[] Gyroscope;
    public float[] Magnetic;
    public float[] Accelerometer;
    public float[] Coordinates;

    public static SensorsSnapshot initial(float[] coordinates) {
        SensorsSnapshot sensors = new SensorsSnapshot();
        sensors.Coordinates = coordinates;
        return sensors;
    }

    public SensorsSnapshot() {
        Timestamp = 0;
        Orientation = null;
        Gyroscope = null;
        Magnetic = null;
        Accelerometer = null;
        Coordinates = null;
    }

    public SensorsSnapshot(long timestamp, float[] orientation) {
        Timestamp = timestamp;
        Orientation = orientation;
        Gyroscope = null;
        Magnetic = null;
        Accelerometer = null;
        Coordinates = null;
    }

    public float azimuth() {
        return Orientation != null ? Orientation[0] : 0;
    }
}
