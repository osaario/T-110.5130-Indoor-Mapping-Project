package students.aalto.org.indoormappingapp.services;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Communicates with the HTTP back-end service.
 * Methods return asynchronous observable variables.
 */
public class NetworkService {

    final static String SERVICE_URL = "https://indoor-mapping-app-server.herokuapp.com/api/";

    public static Observable<Integer> sendStuffToBackend() {
        return null;
    }

    public static Observable<ArrayList<Integer>> getBackendData() {
        return null;
    }


    public static Observable<Boolean> sendLocation(final float x, final float y, final float z) {
        final OkHttpClient client = new OkHttpClient();
        ExecutorService executor = Executors.newFixedThreadPool(3);
        Callable c = new Callable<Boolean>() {

            @Override
            public Boolean call() throws Exception {
                final OutputStream out = new ByteArrayOutputStream();
                JsonFactory f = new JsonFactory();
                JsonGenerator g = f.createGenerator(out);
                g.writeStartObject();
                g.writeNumberField("xCoordinate", x);
                g.writeNumberField("yCoordinate", y);
                g.writeNumberField("zCoordinate", z);
                g.writeEndObject();
                g.close();
                String json = out.toString();
                Request request = new Request.Builder()
                        .post(RequestBody.create(MediaType.parse("application/json"), json))
                        .url("http://requestb.in/wztvrxwz")
                        .addHeader("Content-Type", "application/json")
                        .build();

                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                //String jsonResponse = response.body().string();
                //ObjectMapper mapper = new ObjectMapper();
                //JsonNode node = mapper.readTree(jsonResponse);
                //TypeReference<User> typeRef = new TypeReference<User>(){};
                //User list;
                //list = mapper.readValue(node.traverse(), typeRef);

                return true;
            }
        };

        FutureTask<Boolean> task = new FutureTask<Boolean>(c);
        executor.execute(task);
        return Observable.from(task).subscribeOn(Schedulers.newThread());
    }


}
