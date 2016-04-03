package students.aalto.org.indoormappingapp.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

import students.aalto.org.indoormappingapp.services.NetworkJSONObject;

/**
 * A stored data set.
 */
public class DataSet extends NetworkJSONObject {
    public String ID;
    public Date Created;
    public String Name;
    public String Description;
    public List<MapPosition> Locations;

    public DataSet(String name, String description) {
        ID = null;
        Created = new Date();
        Name = name;
        Description = description;
    }

    public DataSet() {
        new DataSet("unknown", "");
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("created", toJSONDate(Created));
        json.put("name", Name);
        json.put("description", Description);
        return json;
    }

    @Override
    public NetworkJSONObject parseJSON(JSONObject json) throws JSONException {
        DataSet object = new DataSet();
        object.ID = json.getString("_id");
        object.Created = parseJSONDate(json.getString("created"));
        object.Name = json.getString("name");
        object.Description = json.getString("description");
        return object;
    }
}
