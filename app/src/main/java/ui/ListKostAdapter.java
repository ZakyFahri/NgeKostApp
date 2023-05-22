package ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.ngekostapp.R;

import java.util.ArrayList;
import com.squareup.picasso.Picasso;
import com.bumptech.glide.Glide;
import model.ListKost;

public class ListKostAdapter extends RecyclerView.Adapter<ListKostAdapter.MyViewHolder>{
    Context context;
    ArrayList<ListKost> list;
    private RecylerViewClickListener listener;

    public ListKostAdapter(Context context, ArrayList<ListKost> list, RecylerViewClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.listkostrow,parent,false);
        return new ListKostAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ListKostAdapter.MyViewHolder holder, int position) {
        ListKost daftarkost = list.get(position);
        String imageurl;
        holder.name.setText(daftarkost.getNamakost());
        holder.address.setText(daftarkost.getAlamat());
        holder.price.setText(daftarkost.getRangeharga());
        holder.type.setText(daftarkost.getJeniskelaminkost());
        imageurl = daftarkost.getGambarkost();

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
        TextView name, address, price, type;
        ImageView gambarkost;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvNamaKost);
            address = itemView.findViewById(R.id.tvAlamat);
            price = itemView.findViewById(R.id.tvHarga);
            type = itemView.findViewById(R.id.tvJenisKost);
            gambarkost = itemView.findViewById(R.id.imageKost);

            //Klik Listener
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {listener.onClick(view, getAdapterPosition());
        }
    }

}
