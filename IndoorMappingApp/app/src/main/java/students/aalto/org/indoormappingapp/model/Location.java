package students.aalto.org.indoormappingapp.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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
    public List<Photo> Photos;
    public List<Path> Paths;

    public Location(Integer x, Integer y, Integer z, String name) {
        ID = null;
        Created = new Date();
        X = x;
        Y = y;
        Z = z;
        Name = name;
        Photos = new ArrayList<Photo>();
        Paths = new ArrayList<Path>();
    }

    public Location() {
        this(0, 0, 0, "");
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

        JSONArray jsonArray = json.optJSONArray("photos");
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                Photo p = new Photo();
                p.parseJSON(jsonArray.getJSONObject(i));
                Photos.add(p);
            }
        }

        jsonArray = json.optJSONArray("paths");
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                Path p = new Path();
                p.parseJSON(jsonArray.getJSONObject(i));
                Paths.add(p);
            }
        }
    }

}
