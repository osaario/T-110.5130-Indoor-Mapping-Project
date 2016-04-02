package students.aalto.org.indoormappingapp.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.List;

/**
 * A stored data set.
 */
public class DataSet implements JSONAble {
    public Long ID;
    public String Name;
    public String Description;
    public List<MapPosition> Locations;

    public DataSet(String name, String description) {
        Name = name;
        Description = description;
    }

    public DataSet() {
        new DataSet("unknown", "");
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("name", Name);
        json.put("description", Description);
        return json;
    }

    @Override
    public void parseJSON(JSONObject json) throws JSONException {
        Name = json.getString("name");
        Description = json.getString("description");
    }
}
