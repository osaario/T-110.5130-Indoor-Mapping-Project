package students.aalto.org.indoormappingapp.model;

import android.os.Environment;

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
    public File FilePath;
    public double XR;
    public double YR;
    public double ZR;
    public String Description;

    public Photo(double xr, double yr, double zr, String description) throws IOException {
        ID = null;
        Created = new Date();
        FilePath = createImageFile();
        XR = xr;
        YR = yr;
        ZR = zr;
        Description = description;
    }

    public Photo() throws IOException {
        new Photo(0, 0, 0, "");
    }

    public Boolean hasImage() {
        return FilePath.exists();
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
    public NetworkJSONObject empty() throws IOException {
        return new Photo();
    }

    @Override
    public void parseJSON(JSONObject json) throws JSONException, IOException {
        ID = json.getString("_id");
        Created = parseJSONDate(json.getString("created"));
        XR = json.getDouble("xRotation");
        YR = json.getDouble("yRotation");
        ZR = json.getDouble("zRotation");
        Description = json.getString("description");
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IndoorMapping_" + timeStamp;
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }
}
