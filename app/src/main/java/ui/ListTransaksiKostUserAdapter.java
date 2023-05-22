package ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.ngekostapp.DetailKostPemilik;
import com.android.ngekostapp.ListKostPemilik;
import com.android.ngekostapp.ListTransaksiKostUser;
import com.android.ngekostapp.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import com.google.firebase.Timestamp;
import com.squareup.picasso.Picasso;
import com.bumptech.glide.Glide;

import model.ListKost;
import model.ListTransaksiKost;

public class ListTransaksiKostUserAdapter extends RecyclerView.Adapter<ListTransaksiKostUserAdapter.MyViewHolder> {
    Context context;
    ArrayList<ListTransaksiKost> list;
    private String statusTransaksi;
    private ListTransaksiKostUserAdapter.RecylerViewClickListener listener;


    public ListTransaksiKostUserAdapter(Context context, ArrayList<ListTransaksiKost> list, ListTransaksiKostUserAdapter.RecylerViewClickListener listener){
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ListTransaksiKostUserAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.listtransaksiuserrow,parent,false);
        return new ListTransaksiKostUserAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ListTransaksiKostUserAdapter.MyViewHolder holder, int position) {
        ListTransaksiKost daftartransaksi = list.get(position);
        String imageurl;
        holder.name.setText(daftartransaksi.getNamaKost());
        holder.price.setText(daftartransaksi.getTotalHarga());
        holder.date.setText(daftartransaksi.getWaktuDitambahkan());

        statusTransaksi = daftartransaksi.getStatusTransaksi();
        holder.status.setText(statusTransaksi);
        if (holder.status.getText().toString().contains("MENUNGGU KONFIRMASI")){
            holder.status.setTextColor(Color.RED);
        }else if (holder.status.getText().toString().contains("TELAH DIKONFIRMASI")){
            holder.status.setTextColor(Color.parseColor("#fc8a17"));
        }else if(holder.status.getText().toString().contains("TRANSAKSI SELESAI")){
            holder.status.setTextColor(Color.parseColor("#009606"));
        }else if(holder.status.getText().toString().contains("TRANSAKSI DITOLAK")){
            holder.status.setTextColor(Color.RED);
        }

        imageurl = daftartransaksi.getGambarkost();
        /** *Menggunakan Glide Library untuk menampilkan gambar postingan */

        Glide.with(context).load(imageurl).into(holder.gambarkost);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface RecylerViewClickListener{
        void onClick(View view, int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView name, price, date, status;
        ImageView gambarkost;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvNamaKost);
            price = itemView.findViewById(R.id.HargaKost);
            date = itemView.findViewById(R.id.Tanggalbeli);
            status = itemView.findViewById(R.id.StatusTransaksi);
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
