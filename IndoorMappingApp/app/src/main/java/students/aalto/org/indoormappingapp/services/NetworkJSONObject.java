package students.aalto.org.indoormappingapp.services;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class NetworkJSONObject implements NetworkObject {

    abstract public JSONObject toJSON() throws Exception;
    abstract public NetworkJSONObject empty() throws Exception;
    abstract public void parseJSON(JSONObject object) throws Exception;

    protected static final MediaType JSONType = MediaType.parse("application/json; charset=utf-8");
    protected static final SimpleDateFormat ISO8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    static public String toJSONDate(Date date) {
        return ISO8601.format(date);
    }

    static public Date parseJSONDate(String date) throws JSONException {
        try {
            return ISO8601.parse(date);
        } catch (ParseException e) {
            throw new JSONException("Unable to parse date.");
        }
    }

    @Override
    public RequestBody toRequestBody() throws Exception {
        return RequestBody.create(JSONType, toJSON().toString());
    }

    @Override
    public NetworkObject[] parseResponse(Response response) throws Exception {
        Object json = new JSONTokener(response.body().string()).nextValue();

        if (json instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) json;
            NetworkJSONObject out[] = new NetworkJSONObject[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                out[i] = empty();
                out[i].parseJSON(jsonArray.getJSONObject(i));
            }
            return out;
        }

        parseJSON((JSONObject) json);
        NetworkJSONObject out[] = { this };
        return out;
    }
}
