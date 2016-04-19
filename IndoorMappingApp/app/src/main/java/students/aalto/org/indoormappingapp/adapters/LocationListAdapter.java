package students.aalto.org.indoormappingapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import students.aalto.org.indoormappingapp.R;
import students.aalto.org.indoormappingapp.model.Location;

/**
 * Created by olli-mattisaario on 10.4.16.
 */
public class LocationListAdapter extends ArrayAdapter<Location>{

    public LocationListAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Location location = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_location, parent, false);
        }
        // Lookup view for data population
        TextView name = (TextView) convertView.findViewById(R.id.item_location_name);
        TextView created = (TextView) convertView.findViewById(R.id.item_location_created);
        TextView n_photos = (TextView) convertView.findViewById(R.id.item_location_n_photos);
        ImageView image = (ImageView) convertView.findViewById(R.id.location_item_image);
        // Populate the data into the template view using the data object
        name.setText(location.Name);
        created.setText(location.Created.toString());
        if(location.Photos != null && location.Photos.size() > 0) {
            Log.d("ada", location.Photos.toString());
            Picasso.with(parent.getContext())
                    .load(location.Photos.get(0).ThumbURL)
                    .fit()
                    .centerCrop()
                    .into(image);
            n_photos.setText(location.Photos.size() + " " + parent.getContext().getString(R.string.n_photos));
        } else {
            n_photos.setText(R.string.no_photos);
        }

        // Return the completed view to render on screen
        return convertView;
    }

}
