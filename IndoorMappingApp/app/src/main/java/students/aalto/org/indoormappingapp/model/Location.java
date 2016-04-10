package students.aalto.org.indoormappingapp.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import students.aalto.org.indoormappingapp.services.NetworkJSONObject;

public class Location extends NetworkJSONObject {
    public String ID;
    public Date Created;
    public Integer X;
    public Integer Y;
    public Integer Z;
    public String Name;
    public List<Photo> Photos;

    public Location(Integer x, Integer y, Integer z, String name) {
        ID = null;
        Created = new Date();
        X = x;
        Y = y;
        Z = z;
        Name = name;
        Photos = new LinkedList<>();
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
    public NetworkJSONObject empty() {
        return new Location();
    }

    @Override
    public void parseJSON(JSONObject json) throws JSONException {
        ID = json.getString("_id");
        Created = parseJSONDate(json.getString("created"));
        X = json.getInt("xCoordinate");
        Y = json.getInt("yCoordinate");
        Z = json.getInt("zCoordinate");
        Name = json.getString("name");

        JSONArray photos = json.getJSONArray("photos");
        if (photos != null) {
            for (int i = 0; i < photos.length(); i++) {
                Photo p = new Photo();
                p.parseJSON(photos.getJSONObject(i));
                Photos.add(p);
            }
        }
    }
}
