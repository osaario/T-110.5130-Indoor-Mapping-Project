package students.aalto.org.indoormappingapp.services;

import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class ImageDownload implements NetworkObject {
    public String Path;
    public Boolean Success;

    public ImageDownload(String path) {
        Path = path;
        Success = false;
    }

    @Override
    public RequestBody toRequestBody() throws Exception {
        return null;
    }

    @Override
    public NetworkObject[] parseResponse(Response response) throws Exception {
        BufferedInputStream input = new BufferedInputStream(response.body().byteStream());
        OutputStream output = new FileOutputStream(Path);
        byte data[] = new byte[1024];
        int count = 0;
        while ((count = input.read(data)) != -1) {
            output.write(data, 0, count);
        }
        output.flush();
        output.close();
        input.close();

        Success = true;
        ImageDownload out[] = { this };
        return out;
    }
}
