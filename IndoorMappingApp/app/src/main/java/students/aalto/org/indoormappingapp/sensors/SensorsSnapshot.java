package students.aalto.org.indoormappingapp.sensors;

/**
 * Holds a snapshot of all sensor values.
 */
public class SensorsSnapshot {
    public long Timestamp;
    public float[] Magnetic;
    public float[] Accelerometer;
    public float[] Orientation;
    public Integer Azimut;

    public SensorsSnapshot(long timestamp, float[] magnetic, float[] accelerometer) {
        Timestamp = timestamp;
        Magnetic = magnetic;
        Accelerometer = accelerometer;
        Orientation = null;
        Azimut = null;
    }
}
