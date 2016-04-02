package students.aalto.org.indoormappingapp.model;

import org.json.JSONException;
import org.json.JSONObject;

public interface JSONAble {
    public JSONObject toJSON() throws JSONException;
    public void parseJSON(JSONObject json) throws JSONException;
}
