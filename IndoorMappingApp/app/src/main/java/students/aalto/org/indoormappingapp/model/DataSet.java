package students.aalto.org.indoormappingapp.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import students.aalto.org.indoormappingapp.services.NetworkJSONObject;
import students.aalto.org.indoormappingapp.services.NetworkObject;

/**
 * A stored data set.
 */
public class DataSet extends NetworkJSONObject {
    public String ID;
    public String Name;
    public String Description;
    public List<MapPosition> Locations;

    public DataSet(String name, String description) {
        ID = null;
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
        ID = json.getString("_id");
        Name = json.getString("name");
        Description = json.getString("description");
    }
}
