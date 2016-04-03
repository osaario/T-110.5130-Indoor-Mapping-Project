package students.aalto.org.indoormappingapp.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

import students.aalto.org.indoormappingapp.services.NetworkJSONObject;

public class Location extends NetworkJSONObject {
    public String ID;
    public Date Created;
    public Integer X;
    public Integer Y;
    public Integer Z;
    public String Name;
    public List<Photo> photos;

    public Location(Integer x, Integer y, Integer z, String name) {
        ID = null;
        Created = new Date();
        X = x;
        Y = y;
        Z = z;
        Name = name;
    }

    public Location() {
        new Location(0, 0, 0, "");
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("created", toJSONDate(Created));
        json.put("xCoordinate", X);
        json.put("yCoordinate", Y);
        json.put("zCoordinate", Z);
        json.put("name", Name);
        return json;
    }

    @Override
    public NetworkJSONObject parseJSON(JSONObject json) throws JSONException {
        Location object = new Location();
        object.ID = json.getString("_id");
        object.Created = parseJSONDate(json.getString("created"));
        object.X = json.getInt("xCoordinate");
        object.Y = json.getInt("yCoordinate");
        object.Z = json.getInt("zCoordinate");
        object.Name = json.getString("name");
        return object;
    }
}
