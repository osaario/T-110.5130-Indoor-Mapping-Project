package students.aalto.org.indoormappingapp.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.LinkedList;
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
        Locations = new LinkedList<>();
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
    public NetworkJSONObject empty() {
        return new DataSet();
    }

    @Override
    public void parseJSON(JSONObject json) throws JSONException {
        ID = json.getString("_id");
        Created = parseJSONDate(json.getString("created"));
        Name = json.getString("name");
        Description = json.getString("description");
    }
}
