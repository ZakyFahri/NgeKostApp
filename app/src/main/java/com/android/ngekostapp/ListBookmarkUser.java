package com.android.ngekostapp;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.Toast;

import model.ListBookmark;
import model.ListTransaksiKost;
import ui.ListBookmarkAdapter;
import ui.ListTransaksiKostUserAdapter;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;

public class ListBookmarkUser extends AppCompatActivity {
    private ImageButton buttonKembali;
    //Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;

    //Recyler
    private ArrayList<ListBookmark> daftarbookmark;
    private RecyclerView recyclerView;
    private ListBookmarkAdapter listBookmarkAdapter;
    private CollectionReference dataBookmark = db.collection("Data Bookmark");

    //String ambil data
    private String namaKost, namaUser, namaPemilik, alamatPembeli;

    //Click Listener Recycler View
    private ListBookmarkAdapter.RecylerViewClickListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_list_bookmark);

        buttonKembali = findViewById(R.id.tombolkembali);

        //Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        //Widgets
        recyclerView =findViewById(R.id.ListBookmark);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Bundle extra = getIntent().getExtras();
        if(extra != null){
            namaUser = extra.getString("namaUser");
        }

        //List Bookmark
        daftarbookmark = new ArrayList<>();
        loaddatabookmark(namaUser);

        buttonKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setOnClickListener(){
        listener = new ListBookmarkAdapter.RecylerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent kedetailkostuser = new Intent(getApplicationContext(), DetailKostUser.class);
                kedetailkostuser.putExtra("namakost", daftarbookmark.get(position).getNamaKost());
                kedetailkostuser.putExtra("daripeta", "Tidak");
                startActivity(kedetailkostuser);
            }
        };
    }

    private void loaddatabookmark(String namaUser){
        dataBookmark.whereEqualTo("pemilikBookmark", namaUser).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    for (QueryDocumentSnapshot Bookmarklist : queryDocumentSnapshots){
                        ListBookmark ListbookmarkUser = Bookmarklist.toObject(ListBookmark.class);
                        daftarbookmark.add(ListbookmarkUser);

                        //Adapter Bookmark
                        setOnClickListener();
                        listBookmarkAdapter = new ListBookmarkAdapter(ListBookmarkUser.this, daftarbookmark, listener);
                        recyclerView.setAdapter(listBookmarkAdapter);
                        listBookmarkAdapter.notifyDataSetChanged();
                    }
                }else{
                    Toast.makeText(ListBookmarkUser.this, "Data Bookmark Tidak Ada", Toast.LENGTH_LONG).show();
                }
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