package students.aalto.org.indoormappingapp.services;

import android.util.Log;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import students.aalto.org.indoormappingapp.model.DataSet;
import students.aalto.org.indoormappingapp.model.MapPosition;

/**
 * Communicates with the HTTP back-end service.
 * Methods return asynchronously observable variables.
 */
public class NetworkService {

    final static String SERVICE_URL = "https://indoor-mapping-app-server.herokuapp.com/api/";
    final static int SERVICE_POOL_SIZE = 3;
    enum Method { GET, POST, PUT };

    public static Observable<List<DataSet>> getDataSets() {
        return get("datasets/", new DataSet());
    }

    public static Observable<DataSet> saveDataSet(DataSet dataSet) {
        return postOrPut("datasets/", dataSet, dataSet.ID != null);
    };

    public static Observable<MapPosition> saveLocation(Integer dataSetID, MapPosition location) {
        return postOrPut("datasets/" + dataSetID + "/locations", location, false);
    }

    /**
     * Gets objects from the service.
     */
    private static <T extends NetworkObject> Observable<List<T>> get(String path, T empty) {
        return requestService(path, Method.GET, empty);
    }

    /**
     * Posts or puts object to the service.
     */
    private static <T extends NetworkObject> Observable<T> postOrPut(String path, T data, Boolean updateFlag) {
        Observable<List<T>> observable = null;
        if (updateFlag) {
            observable = requestService(path, Method.PUT, data);
        } else {
            observable = requestService(path, Method.POST, data);
        }
        return observable.flatMap(new Func1<List<T>, Observable<T>>() {
            @Override
            public Observable<T> call(List<T> objects) {
                if (objects.size() > 0) {
                    return Observable.just(objects.get(0));
                }
                return Observable.error(new Exception("No object in service response."));
            }
        });
    }

    private static <T extends NetworkObject> Observable<List<T>> requestService(final String path, final Method method, final T data) {
        final OkHttpClient client = new OkHttpClient();
        ExecutorService executor = Executors.newFixedThreadPool(SERVICE_POOL_SIZE);
        Callable c = new Callable<List<T>>() {
            @Override
            public List<T> call() throws Exception {

                // Build HTTP request.
                Request.Builder requestBuilder = new Request.Builder().url(SERVICE_URL + path);
                if (method == Method.GET) {
                    requestBuilder.get();
                } else if (method == Method.POST || method == Method.PUT) {
                    RequestBody body = RequestBody.create(
                            MediaType.parse(data.requestContentType()),
                            data.toRequestBody()
                    );
                    requestBuilder.addHeader("Content-Type", data.requestContentType());
                    if (method == Method.PUT) {
                        requestBuilder.put(body);
                    } else {
                        requestBuilder.post(body);
                    }
                }
                Request request = requestBuilder.build();

                // Make request to service.
                Log.d("service", request.method() + " " + request.urlString());
                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                // Parse response.
                String[] parts = data.splitResponseBody(response.body().string());
                List<T> result = new ArrayList<T>(parts.length);
                for (String body: parts) {
                    T element = (T) data.getClass().newInstance();
                    element.parseResponseBody(body);
                    result.add(element);
                }
                return result;
            }
        };
        FutureTask<List<T>> task = new FutureTask<List<T>>(c);
        executor.execute(task);

        // Make the request and parse it in a new thread, observe on main UI thread.
        return Observable.from(task)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
