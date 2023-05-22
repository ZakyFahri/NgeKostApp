package item;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.ngekostapp.R;

import java.util.ArrayList;
import java.util.List;

public class AdapterJenisLogin extends ArrayAdapter<ItemJenisLogin> {

    public AdapterJenisLogin(@NonNull Context context, ArrayList<ItemJenisLogin> ListLogin) {
        super(context, 0, ListLogin);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return customView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return customView(position, convertView, parent);
    }

    public View customView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_jenislogin_customlayout, parent, false);
        }

        ItemJenisLogin items = getItem(position);
        ImageView spinnerImage = convertView.findViewById(R.id.jenislogingambar);
        TextView textitems = convertView.findViewById(R.id.jenislogintext);
        if(items != null){
            spinnerImage.setImageResource(items.getImageSpinner());
            textitems.setText(items.getJenisLoginText());
        }
        return convertView;
    }
}
