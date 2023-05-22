package com.android.ngekostapp;

import androidx.annotation.NonNull;
import androidx.core.view.MenuHost;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import model.ListKost;
import account.ngekostuser;
import model.ListTransaksiKost;
import ui.ListKostPemilikAdapter;
import ui.ListTransaksiKostPemilikAdapter;
import ui.ListTransaksiKostUserAdapter;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class ListTransaksiKostUser extends AppCompatActivity {
    ImageButton tombolKembali, tombolFilter;
    BottomSheetDialog bottomSheetDialog;

    //Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;

    //Recyler
    private ArrayList<ListTransaksiKost> daftarTransaksi;
    private RecyclerView recyclerView;
    private ListTransaksiKostUserAdapter ListTransaksiUserAdapter;
    private CollectionReference collectionReference = db.collection("Data Transaksi");
    private CollectionReference dataUser = db.collection("Pengguna");

    //String ambil data
    private String namaKost, namaPembeli, namaPemilik, alamatPembeli;

    //Click Listener Recycler View
    private ListTransaksiKostUserAdapter.RecylerViewClickListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_list_transaksi_kost_user);

        bottomSheetDialog = new BottomSheetDialog(this);

        tombolKembali = findViewById(R.id.tombolkembali);
        tombolFilter = findViewById(R.id.tombolFilter);

        //Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        //Widgets
        recyclerView =findViewById(R.id.ListTransaksiUser);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Bundle extra = getIntent().getExtras();
        if(extra != null){
            namaPembeli = extra.getString("namaUser");
            alamatPembeli = extra.getString("alamatUser");
        }


        //List Transaksi User
        daftarTransaksi =new ArrayList<>();
        loadDataTransaksi(namaPembeli, alamatPembeli);

        tombolKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tombolFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewFilter(namaPembeli);
                bottomSheetDialog.show();
            }
        });
    }

    //BottomSheetDialog
    private void ViewFilter(String namaPembeli){
        View view = getLayoutInflater().inflate(R.layout.filtertransaksi,null, false);
        bottomSheetDialog.setContentView(view);

        RadioGroup filterdata = bottomSheetDialog.findViewById(R.id.filterdata);

        filterdata.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                daftarTransaksi.clear();

                RadioButton infoTransaksi = bottomSheetDialog.findViewById(i);

                if (infoTransaksi.getText().toString().contains("Transaksi : Menunggu Konfirmasi")){
                    db.collection("Data Transaksi").whereEqualTo("namaPembeli", namaPembeli).whereEqualTo("statusTransaksi", "MENUNGGU KONFIRMASI").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (DocumentSnapshot doc: task.getResult()){
                                ListTransaksiKost daftarlisttransaksi = doc.toObject(ListTransaksiKost.class);
                                daftarTransaksi.add(daftarlisttransaksi);
                            }

                            //Recycler View
                            //Buat Adapter Untuk Recycler View
                            setOnClickListener();
                            ListTransaksiUserAdapter = new ListTransaksiKostUserAdapter(ListTransaksiKostUser.this, daftarTransaksi, listener);
                            recyclerView.getRecycledViewPool().clear();
                            recyclerView.setAdapter(ListTransaksiUserAdapter);
                            ListTransaksiUserAdapter.notifyDataSetChanged();
                            bottomSheetDialog.dismiss();
                        }
                    });
                }else if (infoTransaksi.getText().toString().contains("Transaksi : Telah Dikonfirmasi")){
                    db.collection("Data Transaksi").whereEqualTo("namaPembeli", namaPembeli).whereEqualTo("statusTransaksi", "TELAH DIKONFIRMASI").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (DocumentSnapshot doc: task.getResult()){
                                ListTransaksiKost daftarlisttransaksi = doc.toObject(ListTransaksiKost.class);
                                daftarTransaksi.add(daftarlisttransaksi);
                            }

                            //Recycler View
                            //Buat Adapter Untuk Recycler View
                            setOnClickListener();
                            ListTransaksiUserAdapter = new ListTransaksiKostUserAdapter(ListTransaksiKostUser.this, daftarTransaksi, listener);
                            recyclerView.getRecycledViewPool().clear();
                            recyclerView.setAdapter(ListTransaksiUserAdapter);
                            ListTransaksiUserAdapter.notifyDataSetChanged();
                            bottomSheetDialog.dismiss();
                        }
                    });
                }else if (infoTransaksi.getText().toString().contains("Transaksi Selesai")){
                    db.collection("Data Transaksi").whereEqualTo("namaPembeli", namaPembeli).whereEqualTo("statusTransaksi", "TRANSAKSI SELESAI").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (DocumentSnapshot doc: task.getResult()){
                                ListTransaksiKost daftarlisttransaksi = doc.toObject(ListTransaksiKost.class);
                                daftarTransaksi.add(daftarlisttransaksi);
                            }

                            //Recycler View
                            //Buat Adapter Untuk Recycler View
                            setOnClickListener();
                            ListTransaksiUserAdapter = new ListTransaksiKostUserAdapter(ListTransaksiKostUser.this, daftarTransaksi, listener);
                            recyclerView.getRecycledViewPool().clear();
                            recyclerView.setAdapter(ListTransaksiUserAdapter);
                            ListTransaksiUserAdapter.notifyDataSetChanged();
                            bottomSheetDialog.dismiss();
                        }
                    });
                }else if (infoTransaksi.getText().toString().contains("Transaksi Ditolak")){
                    db.collection("Data Transaksi").whereEqualTo("namaPembeli", namaPembeli).whereEqualTo("statusTransaksi", "TRANSAKSI DITOLAK").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (DocumentSnapshot doc: task.getResult()){
                                ListTransaksiKost daftarlisttransaksi = doc.toObject(ListTransaksiKost.class);
                                daftarTransaksi.add(daftarlisttransaksi);
                            }

                            //Recycler View
                            //Buat Adapter Untuk Recycler View
                            setOnClickListener();
                            ListTransaksiUserAdapter = new ListTransaksiKostUserAdapter(ListTransaksiKostUser.this, daftarTransaksi, listener);
                            recyclerView.getRecycledViewPool().clear();
                            recyclerView.setAdapter(ListTransaksiUserAdapter);
                            ListTransaksiUserAdapter.notifyDataSetChanged();
                            bottomSheetDialog.dismiss();
                        }
                    });
                }
            }
        });
    }


    private void setOnClickListener(){
        listener = new ListTransaksiKostUserAdapter.RecylerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent keDetailTransaksi = new Intent(getApplicationContext(), DetailTransaksiUser.class);
                keDetailTransaksi.putExtra("namakost", daftarTransaksi.get(position).getNamaKost());
                keDetailTransaksi.putExtra("namaPembeli", daftarTransaksi.get(position).getNamaPembeli());
                keDetailTransaksi.putExtra("namaPemilik", daftarTransaksi.get(position).getNamaPemilik());
                keDetailTransaksi.putExtra("namaKamar", daftarTransaksi.get(position).getNamaKamar());
                keDetailTransaksi.putExtra("harga", daftarTransaksi.get(position).getTotalHarga());
                keDetailTransaksi.putExtra("status", daftarTransaksi.get(position).getStatusTransaksi());
                startActivity(keDetailTransaksi);
            }
        };
    }

    private void loadDataTransaksi(String namaPembeli, String alamatPembeli){
        collectionReference.whereEqualTo("namaPembeli", namaPembeli).whereEqualTo("alamatPembeli", alamatPembeli).orderBy("namaKost", Query.Direction.ASCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    for (QueryDocumentSnapshot TransaksiUserList : queryDocumentSnapshots){
                        ListTransaksiKost ListTransaksiUser = TransaksiUserList.toObject(ListTransaksiKost.class);
                        daftarTransaksi.add(ListTransaksiUser);
                    }

                    //Adapter Transaksi User
                    setOnClickListener();
                    ListTransaksiUserAdapter = new ListTransaksiKostUserAdapter(ListTransaksiKostUser.this, daftarTransaksi, listener);
                    recyclerView.setAdapter(ListTransaksiUserAdapter);
                    ListTransaksiUserAdapter.notifyDataSetChanged();
                }else {
                    Toast.makeText(ListTransaksiKostUser.this, "Data Transaksi Tidak Ada", Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ListTransaksiKostUser.this, "Data Transaksi Tidak ada", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    @Override
    protected void onStop(){
        super.onStop();
    }

    @Override
    protected void onStart(){
        super.onStart();
    }
}