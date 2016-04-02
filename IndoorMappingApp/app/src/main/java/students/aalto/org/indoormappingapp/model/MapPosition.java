package students.aalto.org.indoormappingapp.model;

import org.json.JSONException;
import org.json.JSONObject;

import students.aalto.org.indoormappingapp.services.NetworkJSONObject;

/**
 * A location/position on map.
 */
public class MapPosition extends NetworkJSONObject {
    public Integer X;
    public Integer Y;
    public Integer Z;
    public Integer Photos = 0;

    public MapPosition(Integer x, Integer y, Integer z) {
        X = x;
        Y = y;
        Z = z;
    }

    public MapPosition() {
        new MapPosition(0, 0, 0);
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("xCoordinate", X);
        json.put("yCoordinate", Y);
        json.put("zCoordinate", Z);
        return json;
    }

    @Override
    public void parseJSON(JSONObject json) throws JSONException {
        X = json.getInt("xCoordinate");
        Y = json.getInt("yCoordinate");
        Z = json.getInt("zCoordinate");
    }
}
