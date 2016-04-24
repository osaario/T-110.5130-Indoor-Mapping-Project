package students.aalto.org.indoormappingapp.services;

import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class OKResponse implements NetworkObject {
    public Boolean success;

    @Override
    public RequestBody toRequestBody() throws Exception {
        return null;
    }

    @Override
    public NetworkObject[] parseResponse(Response response) throws Exception {
        success = true;
        OKResponse out[] = { this };
        return out;
    }
}
