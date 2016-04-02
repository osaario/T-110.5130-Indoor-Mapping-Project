package students.aalto.org.indoormappingapp.services;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

public abstract class NetworkJSONObject extends NetworkObject {

    abstract public JSONObject toJSON() throws Exception;
    abstract public void parseJSON(JSONObject object) throws Exception;

    @Override
    public String requestContentType() {
        return "application/json; charset=utf-8";
    }

    @Override
    public String toRequestBody() throws Exception {
        return toJSON().toString();
    }

    @Override
    public void parseResponseBody(String body) throws Exception {
        parseJSON(new JSONObject(body));
    }

    @Override
    public String[] splitResponseBody(String body) throws Exception {

        // Introduces duplicated JSON parsing but helps to maintain code clarity.
        Object json = new JSONTokener(body).nextValue();
        if (json instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) json;
            String out[] = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                out[i] = jsonArray.getJSONObject(i).toString();
            }
            return out;
        }

        String out[] = { body };
        return out;
    }
}
