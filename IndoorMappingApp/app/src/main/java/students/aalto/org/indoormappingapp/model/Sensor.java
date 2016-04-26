package students.aalto.org.indoormappingapp.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import students.aalto.org.indoormappingapp.sensors.SensorsSnapshot;
import students.aalto.org.indoormappingapp.services.NetworkJSONObject;

public class Sensor extends NetworkJSONObject {
    public String ID;
    public Date Created;
    public String FromLocationID;
    public String ToLocationID;
    public List<SensorsSnapshot> path;

    public Sensor(List<SensorsSnapshot> path, Location to) {
        this(path, null, to);
    }

    public Sensor(List<SensorsSnapshot> path, Location from, Location to) {
        ID = null;
        Created = new Date();
        FromLocationID = from != null ? from.ID : null;
        ToLocationID = to != null ? to.ID : null;
        this.path = path;
    }

    public Sensor() {
        this(new ArrayList<SensorsSnapshot>(), null, null);
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("created", toJSONDate(Created));
        json.put("from", FromLocationID);

        JSONArray timestamp = new JSONArray(),
                xOrientation = new JSONArray(), yOrientation = new JSONArray(), zOrientation = new JSONArray(),
                xGyroscope = new JSONArray(), yGyroscope = new JSONArray(), zGyroscope = new JSONArray(),
                xMagnetic = new JSONArray(), yMagnetic = new JSONArray(), zMagnetic = new JSONArray(),
                xAccelerometer = new JSONArray(), yAccelerometer = new JSONArray(), zAccelerometer = new JSONArray(),
                xCoordinate = new JSONArray(), yCoordinate = new JSONArray(), zCoordinate = new JSONArray();
        for (int i = 0; i < path.size(); i++) {
            SensorsSnapshot reading = path.get(i);
            timestamp.put(reading.Timestamp);
            xOrientation.put(reading.Orientation[0]);
            yOrientation.put(reading.Orientation[1]);
            zOrientation.put(reading.Orientation[2]);
            xGyroscope.put(reading.Gyroscope[0]);
            yGyroscope.put(reading.Gyroscope[1]);
            zGyroscope.put(reading.Gyroscope[2]);
            xMagnetic.put(reading.Magnetic[0]);
            yMagnetic.put(reading.Magnetic[1]);
            zMagnetic.put(reading.Magnetic[2]);
            xAccelerometer.put(reading.Accelerometer[0]);
            yAccelerometer.put(reading.Accelerometer[1]);
            zAccelerometer.put(reading.Accelerometer[2]);
            xCoordinate.put(reading.Coordinate[0]);
            yCoordinate.put(reading.Coordinate[1]);
            zCoordinate.put(reading.Coordinate[2]);
        }
        json.put("timestamp", timestamp);
        json.put("xOrientation", xOrientation);
        json.put("yOrientation", yOrientation);
        json.put("zOrientation", zOrientation);
        json.put("xGyroscope", xGyroscope);
        json.put("yGyroscope", yGyroscope);
        json.put("zGyroscope", zGyroscope);
        json.put("xMagnetic", xMagnetic);
        json.put("yMagnetic", yMagnetic);
        json.put("zMagnetic", zMagnetic);
        json.put("xAccelerometer", xAccelerometer);
        json.put("yAccelerometer", yAccelerometer);
        json.put("zAccelerometer", zAccelerometer);
        json.put("xCoordinate", xCoordinate);
        json.put("yCoordinate", yCoordinate);
        json.put("zCoordinate", zCoordinate);
        return json;
    }

    @Override
    public NetworkJSONObject empty() {
        return new Sensor();
    }

    @Override
    public void parseJSON(JSONObject json) throws JSONException {
        ID = json.getString("_id");
        Created = parseJSONDate(json.getString("created"));
        FromLocationID = json.optString("from");

        JSONArray timestamp = json.getJSONArray("timestamp");
        //JSONArray xOrientation = json.getJSONArray("xOrientation");
        //JSONArray yOrientation = json.getJSONArray("yOrientation");
        //JSONArray zOrientation = json.getJSONArray("zOrientation");
        //JSONArray xGyroscope = json.getJSONArray("xGyroscope");
        //JSONArray yGyroscope = json.getJSONArray("yGyroscope");
        //JSONArray zGyroscope = json.getJSONArray("zGyroscope");
        //JSONArray xMagnetic = json.getJSONArray("xMagnetic");
        //JSONArray yMagnetic = json.getJSONArray("yMagnetic");
        //JSONArray zMagnetic = json.getJSONArray("zMagnetic");
        //JSONArray xAccelerometer = json.getJSONArray("xAccelerometer");
        //JSONArray yAccelerometer = json.getJSONArray("yAccelerometer");
        //JSONArray zAccelerometer = json.getJSONArray("zAccelerometer");
        JSONArray xCoordinate = json.getJSONArray("xCoordinate");
        JSONArray yCoordinate = json.getJSONArray("yCoordinate");
        JSONArray zCoordinate = json.getJSONArray("zCoordinate");
        int n = Math.min(timestamp.length(), Math.min(xCoordinate.length(), Math.min(yCoordinate.length(), zCoordinate.length())));
        for (int i = 0; i < n; i++) {
            SensorsSnapshot s = new SensorsSnapshot();
            s.Timestamp = timestamp.getLong(i);
            //s.Orientation = new float[] {(float) xOrientation.getDouble(i), (float) yOrientation.getDouble(i), (float) zOrientation.getDouble(i)};
            //s.Gyroscope = new float[] {(float) xGyroscope.getDouble(i), (float) yGyroscope.getDouble(i), (float) zGyroscope.getDouble(i)};
            //s.Magnetic = new float[] {(float) xMagnetic.getDouble(i), (float) yMagnetic.getDouble(i), (float) zMagnetic.getDouble(i)};
            //s.Accelerometer = new float[] {(float) xAccelerometer.getDouble(i), (float) yAccelerometer.getDouble(i), (float) zAccelerometer.getDouble(i)};
            s.Coordinate = new float[] {(float) xCoordinate.getDouble(i), (float) yCoordinate.getDouble(i), (float) zCoordinate.getDouble(i)};
            path.add(s);
        }
    }
}
