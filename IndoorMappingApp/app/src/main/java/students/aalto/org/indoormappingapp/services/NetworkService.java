package students.aalto.org.indoormappingapp.services;

import android.util.Log;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import students.aalto.org.indoormappingapp.model.DataSet;
import students.aalto.org.indoormappingapp.model.JSONAble;
import students.aalto.org.indoormappingapp.model.MapPosition;

/**
 * Communicates with the HTTP back-end service.
 * Methods return asynchronously observable variables.
 */
public class NetworkService {

    final static String SERVICE_URL = "https://indoor-mapping-app-server.herokuapp.com/api/";
    final static int SERVICE_POOL_SIZE = 3;

    public static Observable<List<DataSet>> getDatasets() {
        return get("datasets/").map(new Func1<JSONArray, List<DataSet>>() {
            @Override
            public List<DataSet> call(JSONArray jsonArray) {
                return parseArray(DataSet.class, jsonArray);
            }
        });
    }

    public static Observable<Boolean> sendLocation(Integer dataSetID, MapPosition location) {
        return post("datasets/" + dataSetID + "/locations", location);
    }

    /**
     * Sends JSON object to the service.
     */
    private static Observable<Boolean> post(final String path, final JSONAble data) {
        final OkHttpClient client = new OkHttpClient();
        ExecutorService executor = Executors.newFixedThreadPool(SERVICE_POOL_SIZE);
        Callable c = new Callable<Boolean>() {

            @Override
            public Boolean call() throws Exception {

                Log.d("service", "POST " + SERVICE_URL + path);
                Request request = new Request.Builder()
                        .url(SERVICE_URL + path)
                        .addHeader("Content-Type", "application/json")
                        .post(RequestBody.create(MediaType.parse("application/json"), data.toJSON().toString()))
                        .build();

                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
                Log.d("service", "Post succeeded.");
                return true;
            }
        };
        FutureTask<Boolean> task = new FutureTask<Boolean>(c);
        executor.execute(task);
        return Observable.from(task).subscribeOn(Schedulers.newThread());
    }

    /**
     * Reads JSON array from the service.
     */
    private static Observable<JSONArray> get(final String path) {
        final OkHttpClient client = new OkHttpClient();
        ExecutorService executor = Executors.newFixedThreadPool(SERVICE_POOL_SIZE);
        Callable c = new Callable<JSONArray>() {

            @Override
            public JSONArray call() throws Exception {

                Log.d("service", "GET " + SERVICE_URL + path);
                Request request = new Request.Builder()
                        .url(SERVICE_URL + path)
                        .get()
                        .build();

                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
                Log.d("service", "Parsing body.");
                return new JSONArray(response.body().string());
            }
        };
        FutureTask<JSONArray> task = new FutureTask<JSONArray>(c);
        executor.execute(task);
        return Observable.from(task).subscribeOn(Schedulers.newThread());
    }

    private static <T extends JSONAble> List<T> parseArray(Class<T> type, JSONArray json) {
        ArrayList<T> out = new ArrayList<T>(json.length());
        for (int i = 0; i < json.length(); i++) {
            try {
                T item = type.newInstance();
                item.parseJSON(json.getJSONObject(i));
                out.add(item);
            } catch (Exception e) {
                // Handle parsing errors in the observable.
                throw new RuntimeException(e);
            }
        }
        return out;
    }

}
