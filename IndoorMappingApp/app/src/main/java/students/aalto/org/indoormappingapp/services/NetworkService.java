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
import students.aalto.org.indoormappingapp.model.Location;
import students.aalto.org.indoormappingapp.model.Photo;

/**
 * Communicates with the HTTP back-end service.
 * Methods return asynchronously observable variables.
 */
public class NetworkService {

    final static String SERVICE_URL = "https://indoor-mapping-app-server.herokuapp.com/api/";
    final static int SERVICE_POOL_SIZE = 3;
    enum Method { GET, POST, PUT, DELETE };

    public static Observable<List<DataSet>> getDataSets() {
        return get("datasets", new DataSet());
    }

    public static Observable<DataSet> saveDataSet(DataSet dataSet) {
        if (dataSet.ID != null) {
            return postOrPut("datasets/" + dataSet.ID, dataSet, true);
        }
        return postOrPut("datasets", dataSet, false);
    }

    public static Observable<List<Location>> getLocations(String dataSetID) {
        return get("datasets/" + dataSetID + "/locations", new Location());
    }

    public static Observable<Location> saveLocation(String dataSetID, Location location) {
        if (location.ID != null) {
            return postOrPut("datasets/" + dataSetID + "/locations/" + location.ID, location, true);
        }
        return postOrPut("datasets/" + dataSetID + "/locations", location, false);
    }

    public static Observable<List<Photo>> getPhotos(String dataSetID, String locationID) throws IOException {
        return get("datasets/" + dataSetID + "/locations/" + locationID + "/photos", new Photo());
    }

    public static Observable<Photo> savePhoto(String dataSetID, String locationID, Photo photo) {
        if (photo.ID != null) {
            return postOrPut("datasets/" + dataSetID + "/locations/" + locationID + "/photos/" + photo.ID, photo, true);
        }
        return postOrPut("datasets/" + dataSetID + "/locations/" + locationID + "/photos", photo, false);
    }

    public static Observable<ImageDownload> getImage(Photo photo) {
        return get("images/" + photo.ID, new ImageDownload(photo.FilePath)).map(new Func1<List<ImageDownload>, ImageDownload>() {
            @Override
            public ImageDownload call(List<ImageDownload> imageDownloads) {
                return imageDownloads.get(0);
            }
        });
    }

    public static Observable<ImageUpload> saveImage(Photo photo) {
        return postOrPut("images/" + photo.ID, new ImageUpload(photo.FilePath), false);
    }

    private static <T extends NetworkObject> Observable<List<T>> get(String path, T empty) {
        return requestService(path, Method.GET, empty);
    }

    private static <T extends NetworkObject> Observable<T> postOrPut(String path, T data, Boolean updateFlag) {
        return requestService(path, updateFlag ? Method.PUT : Method.POST, data).map(new Func1<List<T>, T>() {
            @Override
            public T call(List<T> ts) {
                return ts.get(0);
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
                } else if (method == Method.POST) {
                    requestBuilder.post(data.toRequestBody());
                } else if (method == Method.PUT) {
                    requestBuilder.put(data.toRequestBody());
                } else if (method == Method.DELETE) {
                    requestBuilder.delete();
                }
                Request request = requestBuilder.build();

                // Make request to service.
                Log.d("service", request.method() + " " + request.urlString());
                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                // Parse response.
                NetworkObject objects[] = data.parseResponse(response);
                List<T> result = new ArrayList<T>(objects.length);
                for (NetworkObject o: objects) {
                    result.add((T) o);
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

    /*
    private static <T extends Object> Observable<T> pickFirst(Observable<List<T>> observable) {
        return observable.flatMap(new Func1<List<T>, Observable<T>>() {
            @Override
            public Observable<T> call(List<T> objects) {
                if (objects.size() > 0) {
                    return Observable.just(objects.get(0));
                }
                return Observable.error(new Exception("No object in service response."));
            }
        });
    }*/
}
