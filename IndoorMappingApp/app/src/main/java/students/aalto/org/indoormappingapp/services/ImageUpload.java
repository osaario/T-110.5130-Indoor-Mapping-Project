package students.aalto.org.indoormappingapp.services;

import android.util.Log;

import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.File;

public class ImageUpload implements NetworkObject {
    public File Path;
    public Boolean Success;

    protected static final MediaType JPEGType = MediaType.parse("image/jpeg");

    public ImageUpload(File path) {
        Path = path;
        Success = false;
    }

    @Override
    public RequestBody toRequestBody() throws Exception {
        Log.d("service", "Creating multipart body " + Path);
        Log.d("service", "Image size " + Path.length());
        return new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addFormDataPart("file", Path.getName(), RequestBody.create(JPEGType, Path))
                .build();
    }

    @Override
    public NetworkObject[] parseResponse(Response response) throws Exception {
        Success = true;
        ImageUpload out[] = { this };
        return out;
    }
}
