package students.aalto.org.indoormappingapp.model;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by olli-mattisaario on 10.4.16.
 */

public class ApplicationState {

    private static ApplicationState _instance;

    private DataSet selectedDataSet;
    private Location selectedLocation;

    public static ApplicationState Instance() {
        if (_instance == null) {
            _instance = new ApplicationState();
        }
        return _instance;
    }

    public DataSet getSelectedDataSet() {
        return selectedDataSet;
    }

    public void setSelectedDataSet(DataSet selectedDataSet) {
        this.selectedDataSet = selectedDataSet;
    }

    public Location getSelectedLocation() {
        return selectedLocation;
    }

    public void setSelectedLocation(Location selectedLocation) {
        this.selectedLocation = selectedLocation;
    }
}
