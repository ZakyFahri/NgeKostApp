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
import androidx.recyclerview.widget.RecyclerViewAccessibilityDelegate;

import com.android.ngekostapp.DetailKostPemilik;
import com.android.ngekostapp.ListKostPemilik;
import com.android.ngekostapp.R;

import java.util.ArrayList;
import com.squareup.picasso.Picasso;
import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;
import model.JenisKamarKost;
import model.ListBookmark;

public class ListBookmarkAdapter extends RecyclerView.Adapter<ListBookmarkAdapter.MyViewHolder>{
    Context context;
    ArrayList<ListBookmark> listbookmars;
    private RecylerViewClickListener listener;

    public ListBookmarkAdapter(Context context, ArrayList<ListBookmark> listbookmars, RecylerViewClickListener listener){
        this.context = context;
        this.listbookmars = listbookmars;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ListBookmarkAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.listbookmarkuser,parent,false);
        return new ListBookmarkAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ListBookmarkAdapter.MyViewHolder holder, int position) {
        ListBookmark daftarBookmark = listbookmars.get(position);
        String imageurl;

        holder.name.setText(daftarBookmark.getNamaKost());
        holder.address.setText(daftarBookmark.getAlamatkost());
        holder.price.setText(daftarBookmark.getRangehargakost());
        holder.type.setText(daftarBookmark.getJeniskost());
        imageurl = daftarBookmark.getGambarKost();



        /** *Menggunakan Glide Library untuk menampilkan gambar postingan */

        Glide.with(context).load(imageurl).into(holder.gambarkost);
    }

    @Override
    public int getItemCount() {
        return listbookmars.size();
    }

    public interface RecylerViewClickListener{
        void onClick(View view, int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView name, address, price, type;
        ImageView gambarkost;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvNamaKost);
            address = itemView.findViewById(R.id.tvAlamat);
            price = itemView.findViewById(R.id.RangeHarga);
            type = itemView.findViewById(R.id.JenisKost);
            gambarkost = itemView.findViewById(R.id.imageKost);

            //Klik Listener
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition());
        }
    }
}
