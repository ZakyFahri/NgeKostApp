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

public class AdapterJenisKelamin extends ArrayAdapter<ItemJenisKelaminKost> {

    public AdapterJenisKelamin(@NonNull Context context, ArrayList<ItemJenisKelaminKost> ListKelamin) {
        super(context, 0, ListKelamin);
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
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_jeniskelamin_customlayout, parent, false);
        }

        ItemJenisKelaminKost items = getItem(position);
        ImageView spinnerImage = convertView.findViewById(R.id.jeniskelamingambar);
        TextView textitems = convertView.findViewById(R.id.jeniskelamintext);
        if(items != null){
            spinnerImage.setImageResource(items.getImageKelaminSpinner());
            textitems.setText(items.getJenisKelaminText());
        }
        return convertView;
    }
}
