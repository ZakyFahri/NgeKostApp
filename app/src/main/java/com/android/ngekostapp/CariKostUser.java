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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import model.ListKost;
import account.ngekostuser;
import ui.ListKostAdapter;
import ui.ListKostPemilikAdapter;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CariKostUser extends AppCompatActivity {
    //Search
    SearchView cariKost;

    BottomSheetDialog bottomSheetDialog;

    Button filter;

    private String namaKost;

    //Firebase
    private FirebaseAuth firebaseAuth;
    ImageButton tombolKembali;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private ArrayList<ListKost> daftarKost;
    private RecyclerView recyclerView;
    private ListKostAdapter listKostAdapter;
    private CollectionReference collectionReference = db.collection("Data Kost");

    //Click Listener Recycler View
    private ListKostAdapter.RecylerViewClickListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_cari_kost_user);

        tombolKembali = findViewById(R.id.tombolkembali);
        cariKost = findViewById(R.id.searchData);
        filter = findViewById(R.id.buttonFilter);

        bottomSheetDialog = new BottomSheetDialog(this);

        //Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        //Widgets
        recyclerView =findViewById(R.id.kostList);
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

        cariKost.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                cariData(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.equals("")){
                    this.onQueryTextSubmit("");
                    daftarKost.clear();
                    loadDataKost();
                }
                cariDataContinue(s);
                return true;
            }
        });

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AturViewFilter();
                bottomSheetDialog.show();
            }
        });

//        bottomSheetDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);


    }

    //Filter
    private void AturViewFilter(){
        View view = getLayoutInflater().inflate(R.layout.filter, null, false);
        bottomSheetDialog.setContentView(view);

        RadioGroup filterdata = bottomSheetDialog.findViewById(R.id.filterdata);

        filterdata.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                daftarKost.clear();
                RadioButton AZ = bottomSheetDialog.findViewById(i);
//                RadioButton ZA = bottomSheetDialog.findViewById(R.id.radioZA);
//                RadioButton RadLaki = bottomSheetDialog.findViewById(R.id.radioLakiLaki);
//                RadioButton  RadPerempuan = bottomSheetDialog.findViewById(R.id.radioPerempuan);
//                RadioButton RadCampur = bottomSheetDialog.findViewById(R.id.radioCampur);
//                RadioButton RadRating = bottomSheetDialog.findViewById(R.id.radioRating);

                if (AZ.getText().toString().contains("Laki-Laki")){
                    db.collection("Data Kost").whereEqualTo("jeniskelaminkost", "Laki-Laki").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (DocumentSnapshot doc: task.getResult()){
                                ListKost daftarlistkost = doc.toObject(ListKost.class);
                                daftarKost.add(daftarlistkost);
                            }

                            //Recycler View
                            //Buat Adapter Untuk Recycler View
                            setOnClickListener();
                            listKostAdapter = new ListKostAdapter(CariKostUser.this, daftarKost, listener);
                            recyclerView.getRecycledViewPool().clear();
                            recyclerView.setAdapter(listKostAdapter);
                            listKostAdapter.notifyDataSetChanged();
                            bottomSheetDialog.dismiss();
                        }
                    });
                }else if (AZ.getText().toString().contains("Perempuan")){
                    db.collection("Data Kost").whereEqualTo("jeniskelaminkost", "Perempuan").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (DocumentSnapshot doc: task.getResult()){
                                ListKost daftarlistkost = doc.toObject(ListKost.class);
                                daftarKost.add(daftarlistkost);
                            }

                            //Recycler View
                            //Buat Adapter Untuk Recycler View
                            setOnClickListener();
                            listKostAdapter = new ListKostAdapter(CariKostUser.this, daftarKost, listener);
                            recyclerView.getRecycledViewPool().clear();
                            recyclerView.setAdapter(listKostAdapter);
                            listKostAdapter.notifyDataSetChanged();
                            bottomSheetDialog.dismiss();
                        }
                    });
                }else if (AZ.getText().toString().contains("Campur")) {
                    db.collection("Data Kost").whereEqualTo("jeniskelaminkost", "Campur").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (DocumentSnapshot doc : task.getResult()) {
                                ListKost daftarlistkost = doc.toObject(ListKost.class);
                                daftarKost.add(daftarlistkost);
                            }

                            //Recycler View
                            //Buat Adapter Untuk Recycler View
                            setOnClickListener();
                            listKostAdapter = new ListKostAdapter(CariKostUser.this, daftarKost, listener);
                            recyclerView.getRecycledViewPool().clear();
                            recyclerView.setAdapter(listKostAdapter);
                            listKostAdapter.notifyDataSetChanged();
                            bottomSheetDialog.dismiss();
                        }
                    });
                }else if (AZ.getText().toString().contains("A-Z")){
                    db.collection("Data Kost").orderBy("namakost", Query.Direction.ASCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (!queryDocumentSnapshots.isEmpty()){
                                for (QueryDocumentSnapshot datakostASC : queryDocumentSnapshots){
                                    ListKost daftarlistkost = datakostASC.toObject(ListKost.class);
                                    daftarKost.add(daftarlistkost);
                                }

                                //Recycler View
                                //Buat Adapter Untuk Recycler View
                                setOnClickListener();
                                listKostAdapter = new ListKostAdapter(CariKostUser.this, daftarKost, listener);
                                recyclerView.getRecycledViewPool().clear();
                                recyclerView.setAdapter(listKostAdapter);
                                listKostAdapter.notifyDataSetChanged();
                                bottomSheetDialog.dismiss();
                            }
                        }
                    });
                }else if (AZ.getText().toString().contains("Z-A")){
                    db.collection("Data Kost").orderBy("namakost", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (!queryDocumentSnapshots.isEmpty()){
                                for (QueryDocumentSnapshot datakostASC : queryDocumentSnapshots){
                                    ListKost daftarlistkost = datakostASC.toObject(ListKost.class);
                                    daftarKost.add(daftarlistkost);
                                }

                                //Recycler View
                                //Buat Adapter Untuk Recycler View
                                setOnClickListener();
                                listKostAdapter = new ListKostAdapter(CariKostUser.this, daftarKost, listener);
                                recyclerView.getRecycledViewPool().clear();
                                recyclerView.setAdapter(listKostAdapter);
                                listKostAdapter.notifyDataSetChanged();
                                bottomSheetDialog.dismiss();
                            }
                        }
                    });
                }else if (AZ.getText().toString().contains("Rating Tertinggi")){
                    db.collection("Data Kost").orderBy("Skor Kost", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (!queryDocumentSnapshots.isEmpty()){
                                for (QueryDocumentSnapshot datakostASC : queryDocumentSnapshots){
                                    ListKost daftarlistkost = datakostASC.toObject(ListKost.class);
                                    daftarKost.add(daftarlistkost);
                                }

                                //Recycler View
                                //Buat Adapter Untuk Recycler View
                                setOnClickListener();
                                listKostAdapter = new ListKostAdapter(CariKostUser.this, daftarKost, listener);
                                recyclerView.getRecycledViewPool().clear();
                                recyclerView.setAdapter(listKostAdapter);
                                listKostAdapter.notifyDataSetChanged();
                                bottomSheetDialog.dismiss();
                            }
                        }
                    });
                }
            }
        });
    }

    //PENCARIAN
    private void cariData(String s){
        db.collection("Data Kost").whereEqualTo("search", s).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    daftarKost.clear();
                    for (DocumentSnapshot doc: task.getResult()){
                        ListKost daftarlistkost = doc.toObject(ListKost.class);
                        daftarKost.add(daftarlistkost);
                    }

                    //Recycler View
                    //Buat Adapter Untuk Recycler View
                    setOnClickListener();
                    listKostAdapter = new ListKostAdapter(CariKostUser.this, daftarKost, listener);
                    recyclerView.setAdapter(listKostAdapter);
                    listKostAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void cariDataContinue(String s){
        db.collection("Data Kost").whereGreaterThanOrEqualTo("search", s).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    daftarKost.clear();
                    for (DocumentSnapshot doc: task.getResult()){
                        ListKost daftarlistkost = doc.toObject(ListKost.class);
                        daftarKost.add(daftarlistkost);
                    }

                    //Recycler View
                    //Buat Adapter Untuk Recycler View
                    setOnClickListener();
                    listKostAdapter = new ListKostAdapter(CariKostUser.this, daftarKost, listener);
                    recyclerView.setAdapter(listKostAdapter);
                    listKostAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void setOnClickListener(){
        listener = new ListKostAdapter.RecylerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent kedetailkostuser = new Intent(getApplicationContext(), DetailKostUser.class);
                kedetailkostuser.putExtra("namakost", daftarKost.get(position).getNamakost());
                kedetailkostuser.putExtra("daripeta", "Tidak");
                startActivity(kedetailkostuser);
            }
        };
    }

    private void loadDataKost(){
        db.collection("Data Kost").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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
                    listKostAdapter = new ListKostAdapter(CariKostUser.this, daftarKost, listener);
                    recyclerView.setAdapter(listKostAdapter);
                    listKostAdapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(CariKostUser.this, "OK", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Keasalahan Apapun
                Toast.makeText(CariKostUser.this, "Ups! Ada yang Salah!" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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