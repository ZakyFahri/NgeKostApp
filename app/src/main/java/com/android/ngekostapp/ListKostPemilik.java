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
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import model.ListKost;
import account.ngekostuser;
import ui.ListKostPemilikAdapter;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;


public class ListKostPemilik extends AppCompatActivity {
    ImageButton tombolKembali;
    SearchView cari;
    //Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private ArrayList<ListKost> daftarKost;
    private RecyclerView recyclerView;
    private ListKostPemilikAdapter listKostAdapter;
    private CollectionReference collectionReference = db.collection("Data Kost");

    //Click Listener Recycler View
    private ListKostPemilikAdapter.RecylerViewClickListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_list_kost_pemilik);

        tombolKembali = findViewById(R.id.tombolkembali);

        //Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        //Widgets
        recyclerView =findViewById(R.id.kostListPemilik);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //List Kost
        daftarKost =new ArrayList<>();

        loadDataKost();

        tombolKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setOnClickListener(){
        listener = new ListKostPemilikAdapter.RecylerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent kedetailkostpemilik = new Intent(getApplicationContext(), DetailKostPemilik.class);
                kedetailkostpemilik.putExtra("namakost", daftarKost.get(position).getNamakost());
                startActivity(kedetailkostpemilik);
            }
        };
    }

    private void loadDataKost(){
        collectionReference.whereEqualTo("userID", ngekostuser.getInstance().getUserID()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    for(QueryDocumentSnapshot listkost : queryDocumentSnapshots){
                        ListKost daftarlistkost = listkost.toObject(ListKost.class);
                        daftarKost.add(daftarlistkost);
                    }

                    //Recycler View
                    //Buat Adapter Untuk Recycler View
                    setOnClickListener();
                    listKostAdapter = new ListKostPemilikAdapter(ListKostPemilik.this, daftarKost, listener);
                    recyclerView.setAdapter(listKostAdapter);
                    listKostAdapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(ListKostPemilik.this, "Data Kost Tidak Ada", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Keasalahan Apapun
                Toast.makeText(ListKostPemilik.this, "Ups! Ada yang Salah!" + e.getMessage(), Toast.LENGTH_SHORT).show();
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