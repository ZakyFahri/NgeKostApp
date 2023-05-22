package ui;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.ngekostapp.DetailKostPemilik;
import com.android.ngekostapp.ListKostPemilik;
import com.android.ngekostapp.R;

import java.util.ArrayList;
import com.squareup.picasso.Picasso;
import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;
import model.JenisKamarKost;

public class ListJenisKamarPemilikAdapter extends RecyclerView.Adapter<ListJenisKamarPemilikAdapter.MyViewHolder> {
    Context context;
    ArrayList<JenisKamarKost> listKamar;
    private RecylerViewClickListener listener;

    public ListJenisKamarPemilikAdapter(Context context, ArrayList<JenisKamarKost> listKamar, RecylerViewClickListener listener) {
        this.context = context;
        this.listKamar = listKamar;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ListJenisKamarPemilikAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.horizontaljeniskamarshow,parent,false);
        return new ListJenisKamarPemilikAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ListJenisKamarPemilikAdapter.MyViewHolder holder, int position) {
        JenisKamarKost daftarJenisKamar = listKamar.get(position);
        String imageurl;
        holder.namaKamar.setText(daftarJenisKamar.getNamaKamar());
        imageurl = daftarJenisKamar.getGambarKamar();



        /** *Menggunakan Glide Library untuk menampilkan gambar postingan */

        Glide.with(context).load(imageurl).into(holder.gambarKamar);
    }

    @Override
    public int getItemCount() {
        return listKamar.size();
    }

    public interface RecylerViewClickListener{
        void onClick(View view, int position);
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        CircleImageView gambarKamar;
        TextView namaKamar;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            gambarKamar = itemView.findViewById(R.id.GambarKamar);
            namaKamar = itemView.findViewById(R.id.NamaKamar);

            //Klik Listener
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition());
        }
    }
}
