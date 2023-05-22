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
import com.android.ngekostapp.ListTransaksiKostPemilik;
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

public class ListTransaksiKostPemilikAdapter extends RecyclerView.Adapter<ListTransaksiKostPemilikAdapter.MyViewHolder>  {
    Context context;
    ArrayList<ListTransaksiKost> list;
    private ListTransaksiKostPemilikAdapter.RecylerViewClickListener listener;

    public ListTransaksiKostPemilikAdapter(Context context, ArrayList<ListTransaksiKost> list, ListTransaksiKostPemilikAdapter.RecylerViewClickListener listener){
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ListTransaksiKostPemilikAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.listtransaksipemilikrow,parent,false);
        return new ListTransaksiKostPemilikAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ListTransaksiKostPemilikAdapter.MyViewHolder holder, int position) {
        ListTransaksiKost daftartransaksi = list.get(position);
        String imageurl;
        holder.name.setText(daftartransaksi.getNamaKost());
        holder.customer.setText(daftartransaksi.getNamaPembeli());
        holder.price.setText(daftartransaksi.getTotalHarga());
        holder.date.setText(daftartransaksi.getWaktuDitambahkan());

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
        TextView name, price, date, customer;
        ImageView gambarkost;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvNamaKost);
            price = itemView.findViewById(R.id.Hargasewa);
            date = itemView.findViewById(R.id.Tanggalbeli);
            customer = itemView.findViewById(R.id.NamaPembeli);
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
