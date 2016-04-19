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
import students.aalto.org.indoormappingapp.model.Photo;

/**
 * Created by olli-mattisaario on 10.4.16.
 */
public class PhotoListAdapter extends ArrayAdapter<Photo>{

    private final Context context;

    public PhotoListAdapter(Context context, int resource) {
        super(context, resource);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Photo photo = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_photo, parent, false);
        }
        // Lookup view for data population
        TextView created = (TextView) convertView.findViewById(R.id.photo_item_created);
        ImageView image = (ImageView) convertView.findViewById(R.id.photo_item_image);
        // Populate the data into the template view using the data object
        // Return the completed view to render on screen
        created.setText(photo.Created.toString());
        Picasso.with(context)
                .load(photo.ThumbURL)
                .fit()
                .centerCrop()
                .into(image);
        return convertView;
    }

}
