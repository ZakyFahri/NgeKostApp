package ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.ngekostapp.R;

import java.util.ArrayList;
import com.bumptech.glide.Glide;

import model.ListReviewKost;

public class ListReviewKostUserAdapter extends RecyclerView.Adapter<ListReviewKostUserAdapter.MyViewHolder> {
    Context context;
    ArrayList<ListReviewKost> list;

    public ListReviewKostUserAdapter(Context context, ArrayList<ListReviewKost> list){
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ListReviewKostUserAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.listreviewrow,parent,false);
        return new ListReviewKostUserAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ListReviewKostUserAdapter.MyViewHolder holder, int position) {
        ListReviewKost daftarReviewer = list.get(position);

        String imageurl;

        holder.namaReviewer.setText(daftarReviewer.getNamaReviewer());
        holder.UlasanReviewer.setText(daftarReviewer.getUlasanReviewer());
        holder.buatBintang.setText(daftarReviewer.getBintangReviewer());
        holder.buatBintang.setVisibility(View.GONE);

        if (holder.buatBintang.getText().toString().contains("1")){
            holder.bintang1.setBackgroundResource(R.drawable.star);
            holder.bintang2.setBackgroundResource(R.drawable.star_empty);
            holder.bintang3.setBackgroundResource(R.drawable.star_empty);
            holder.bintang4.setBackgroundResource(R.drawable.star_empty);
            holder.bintang5.setBackgroundResource(R.drawable.star_empty);
        }else if (holder.buatBintang.getText().toString().contains("2")){
            holder.bintang1.setBackgroundResource(R.drawable.star);
            holder.bintang2.setBackgroundResource(R.drawable.star);
            holder.bintang3.setBackgroundResource(R.drawable.star_empty);
            holder.bintang4.setBackgroundResource(R.drawable.star_empty);
            holder.bintang5.setBackgroundResource(R.drawable.star_empty);
        }else if (holder.buatBintang.getText().toString().contains("3")){
            holder.bintang1.setBackgroundResource(R.drawable.star);
            holder.bintang2.setBackgroundResource(R.drawable.star);
            holder.bintang3.setBackgroundResource(R.drawable.star);
            holder.bintang4.setBackgroundResource(R.drawable.star_empty);
            holder.bintang5.setBackgroundResource(R.drawable.star_empty);
        }else if (holder.buatBintang.getText().toString().contains("4")){
            holder.bintang1.setBackgroundResource(R.drawable.star);
            holder.bintang2.setBackgroundResource(R.drawable.star);
            holder.bintang3.setBackgroundResource(R.drawable.star);
            holder.bintang4.setBackgroundResource(R.drawable.star);
            holder.bintang5.setBackgroundResource(R.drawable.star_empty);
        }else if (holder.buatBintang.getText().toString().contains("5")){
            holder.bintang1.setBackgroundResource(R.drawable.star);
            holder.bintang2.setBackgroundResource(R.drawable.star);
            holder.bintang3.setBackgroundResource(R.drawable.star);
            holder.bintang4.setBackgroundResource(R.drawable.star);
            holder.bintang5.setBackgroundResource(R.drawable.star);
        }

        /** *Menggunakan Glide Library untuk menampilkan gambar postingan */
        imageurl = daftarReviewer.getFotoReviewer();
        Glide.with(context).load(imageurl).into(holder.gambarReviewer);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView namaReviewer, UlasanReviewer, buatBintang;
        ImageButton bintang1, bintang2, bintang3, bintang4, bintang5;
        ImageView gambarReviewer;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            namaReviewer = itemView.findViewById(R.id.NamaReviewer);
            UlasanReviewer = itemView.findViewById(R.id.UlasanReview);
            gambarReviewer = itemView.findViewById(R.id.GambarReviewer);
            buatBintang = itemView.findViewById(R.id.buatBintang);

            //Bintang
            bintang1 = (ImageButton) itemView.findViewById(R.id.bintang1R);
            bintang2 = (ImageButton) itemView.findViewById(R.id.bintang2R);
            bintang3 = (ImageButton) itemView.findViewById(R.id.bintang3R);
            bintang4 = (ImageButton) itemView.findViewById(R.id.bintang4R);
            bintang5 = (ImageButton) itemView.findViewById(R.id.bintang5R);
        }
    }
}
