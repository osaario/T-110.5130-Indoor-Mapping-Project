package students.aalto.org.indoormappingapp.services;

import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public interface NetworkObject {
    RequestBody toRequestBody() throws Exception;
    NetworkObject[] parseResponse(Response response) throws Exception;
}
