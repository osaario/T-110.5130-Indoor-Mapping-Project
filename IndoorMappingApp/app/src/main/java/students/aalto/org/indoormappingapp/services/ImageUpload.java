package students.aalto.org.indoormappingapp.services;

import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.File;

public class ImageUpload implements NetworkObject {
    public String Path;
    public Boolean Success;

    protected static final MediaType JPEGType = MediaType.parse("image/jpeg");

    public ImageUpload(String path) {
        Path = path;
        Success = false;
    }

    @Override
    public RequestBody toRequestBody() throws Exception {
        return new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"file\""),
                        RequestBody.create(JPEGType, new File(Path)))
                .build();
    }

    @Override
    public NetworkObject[] parseResponse(Response response) throws Exception {
        Success = true;
        ImageUpload out[] = { this };
        return out;
    }
}
