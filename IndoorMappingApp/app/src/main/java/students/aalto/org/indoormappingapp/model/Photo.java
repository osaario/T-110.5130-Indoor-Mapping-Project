package students.aalto.org.indoormappingapp.model;

import android.os.Environment;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import students.aalto.org.indoormappingapp.services.NetworkJSONObject;

public class Photo extends NetworkJSONObject {
    public String ID;
    public Date Created;
    public String Description;
    public String URL;
    public String ThumbURL;

    public File FilePath;
    public Sensor Sensor;

    public Photo(String description, Sensor sensor) {
        ID = null;
        Created = new Date();
        Description = description;
        URL = null;
        ThumbURL = null;
        FilePath = null;
        Sensor = sensor;
    }

    public Photo(String description) {
        this(description, null);
    }

    public Photo() {
        this("");
    }

    public Boolean hasImage() {
        return FilePath.exists();
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("created", toJSONDate(Created));
        json.put("description", Description);
        if (Sensor != null) {
            json.put("sensorobject", Sensor.toJSON());
        }
        return json;
    }

    @Override
    public NetworkJSONObject empty() throws IOException {
        return new Photo();
    }

    @Override
    public void parseJSON(JSONObject json) throws JSONException {
        ID = json.getString("_id");
        Created = parseJSONDate(json.getString("created"));
        Description = json.getString("description");
        String url = json.optString("image");
        if (url != null) {
            URL = url + "/small";
            ThumbURL = url + "/tiny";
        } else {
            URL = null;
            ThumbURL = null;
        }
    }

    public File createFilePath() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IndoorMapping_" + timeStamp;
        try {
            FilePath = File.createTempFile(imageFileName, ".jpg", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
        } catch (IOException e) {
            Log.e("photo", "Unlikely error, application will crash and burn soon enough.");
            Log.e("photo", e.toString());
        }
        return FilePath;
    }
}
