package students.aalto.org.indoormappingapp.model;

import java.util.ArrayList;

/**
 * Created by olli-mattisaario on 24.3.16.
 */
public class RenderData {

    public final ArrayList<MapPosition> Positions;
    public final ArrayList<MapPosition> Photos;
    public final Float ZoomLevel;

    public RenderData(ArrayList<MapPosition> positions, ArrayList<MapPosition> photos, Float zoomLevel) {
        Positions = positions;
        Photos = photos;
        ZoomLevel = zoomLevel;
    }
}
