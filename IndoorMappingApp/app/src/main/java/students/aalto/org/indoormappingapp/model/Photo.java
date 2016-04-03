package students.aalto.org.indoormappingapp.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import students.aalto.org.indoormappingapp.services.NetworkJSONObject;

public class Photo extends NetworkJSONObject {
    public String ID;
    public Date Created;
    public String FilePath;
    public double XR;
    public double YR;
    public double ZR;
    public String Description;

    public Photo(String filePath, double xr, double yr, double zr, String description) {
        ID = null;
        Created = new Date();
        FilePath = filePath;
        XR = xr;
        YR = yr;
        ZR = zr;
        Description = description;
    }

    public Photo() {
        new Photo(null, 0, 0, 0, "");
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("created", toJSONDate(Created));
        json.put("xRotation", XR);
        json.put("yRotation", YR);
        json.put("zRotation", ZR);
        json.put("description", Description);
        return json;
    }

    @Override
    public NetworkJSONObject parseJSON(JSONObject json) throws JSONException {
        Photo object = new Photo();
        object.ID = json.getString("_id");
        object.Created = parseJSONDate(json.getString("created"));
        object.XR = json.getDouble("xRotation");
        object.YR = json.getDouble("yRotation");
        object.ZR = json.getDouble("zRotation");
        object.Description = json.getString("description");
        return object;
    }
}
