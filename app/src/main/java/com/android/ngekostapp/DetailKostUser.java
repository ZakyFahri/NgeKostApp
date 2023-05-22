package com.android.ngekostapp;

import androidx.annotation.NonNull;
import androidx.core.view.MenuHost;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.media.Rating;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import model.JenisKamarKost;
import model.ListBookmark;
import model.ListKost;
import account.ngekostuser;
import model.ListReviewKost;
import ui.ListJenisKamarPemilikAdapter;
import ui.ListKostPemilikAdapter;
import ui.ListReviewKostUserAdapter;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailKostUser extends AppCompatActivity {

    //Widget
    ImageView gambarkostdetail;
    TextView jeniskost, ratingkos, alamatkost, deskripsikost, notelponkost, namapemilikkost, namakostdetail, RatingKost;
    TextView infoReview;
    ImageButton buttonBookmark, tombolKembali, telponPemilik, buttonKeMap;

    //Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    //Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private CollectionReference collectionReference = db.collection("Data Kost");
    private CollectionReference dataKamar = db.collection("Data Kamar");
    private CollectionReference Pengguna = db.collection("Pengguna");

    //Untuk Bookmark
    private String userID;
    private String pemilikBookmark;
    private String JenisKost;
    private String RangeKost;
    private String gambarKost;
    private String AlamatKost;
    private String namaPemilikKost;
    private String daripeta = "Tidak";
    private double ratingkost;
    private double totalratingkost = 0;
    private double jumlahReviewer = 0;

    //Map
    private String latitude;
    private String longitude;

    //String untuk Data Kost
    private String namaKost;
    private String jenisKost;
    private String rangehargaKost;
    private String alamatKost;
    private String notelpKost;
    private String descKost;
    private String pemilikKost;
    private String userIDKost;
    private String GambarKostPath;

    private ArrayList<JenisKamarKost> daftarKamarKost;
    private ArrayList<ListReviewKost> daftarReviewer;
    private ListJenisKamarPemilikAdapter ListJenisKamarAdapter;
    private ListReviewKostUserAdapter listReviewKostUserAdapter;
    private RecyclerView recyclerView;
    private RecyclerView recyclerViewReview;

    private Uri fotokost;


    //Click Listener Recyler View
    private ListJenisKamarPemilikAdapter.RecylerViewClickListener listener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_detail_kost_user);

        gambarkostdetail = findViewById(R.id.gambarkostdetail);
        jeniskost = findViewById(R.id.jeniskostdetail);
        ratingkos = findViewById(R.id.ratingkostdetail);
        alamatkost = findViewById(R.id.alamatkostdetail);
        deskripsikost = findViewById(R.id.DeskripsiKostDetail);
        notelponkost = findViewById(R.id.notelppemilikkostdetail);
        namapemilikkost = findViewById(R.id.namapemilikkostDetail);
        namakostdetail = findViewById(R.id.NamaKostDetail);

        buttonKeMap = findViewById(R.id.pinmapdetailkost);
        telponPemilik = findViewById(R.id.pintelponkost);

        RatingKost = findViewById(R.id.ratingkostdetail);

        tombolKembali = findViewById(R.id.tombolkembali);

        infoReview=findViewById(R.id.ReviewInfoEmpty);
        infoReview.setVisibility(View.GONE);

        //Image Button
        buttonBookmark = findViewById(R.id.pinbookmarkkost);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = firebaseAuth.getInstance();

        //BUNDLE EXTRA INTENT
        Bundle extra = getIntent().getExtras();
        if(extra != null){
            namaKost = extra.getString("namakost");
            daripeta = extra.getString("daripeta");

            loadDetailKost(namaKost);
            loadJenisKamar(namaKost);
        }

        if (daripeta.equals("Yes")){
            buttonKeMap.setVisibility(View.GONE);
        }else if (daripeta.equals("Tidak")){
            buttonKeMap.setVisibility(View.VISIBLE);
        }

        //Bookmark
        user = firebaseAuth.getCurrentUser();
        assert  user !=null;
        final String userID = user.getUid();
        Pengguna.whereEqualTo("userID", userID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    QuerySnapshot documentquery = task.getResult();
                    DocumentSnapshot document = documentquery.getDocuments().get(0);
                    pemilikBookmark = (String) document.get("nama");
                    loadBookmark(namaKost, pemilikBookmark);
                }
            }
        });

        //Widgets Jenis Kamar
        recyclerView = findViewById(R.id.ListJenisKamar);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        //List Jenis Kamar
        daftarKamarKost = new ArrayList<>();

        //Widget Reviewer
        recyclerViewReview = findViewById(R.id.ListReviewKost);
        recyclerViewReview.setHasFixedSize(true);
        recyclerViewReview.setLayoutManager(new LinearLayoutManager(this));
        //List Reviewer
        daftarReviewer = new ArrayList<>();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if(user!=null){

                }else{

                }
            }
        };

        buttonBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("Data Bookmark").whereEqualTo("pemilikBookmark", pemilikBookmark).whereEqualTo("namaKost", namaKost).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        QuerySnapshot documentQuery = task.getResult();
                        if (!documentQuery.isEmpty()){
                            buttonBookmark.setImageDrawable(getBaseContext().getResources().getDrawable(R.drawable.addbookmark));
                            HapusBookmark(pemilikBookmark, namaKost);
                        }else {
                            buttonBookmark.setImageDrawable(getBaseContext().getResources().getDrawable(R.drawable.bookmarkadded));
                            tambahBookmark(namaKost, namaPemilikKost);
                        }
                    }
                });
            }
        });

        tombolKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        telponPemilik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent keWA = new Intent(Intent.ACTION_VIEW);
                keWA.setData(Uri.parse("https://wa.me/" + notelpKost));
                startActivity(keWA);
            }
        });

        buttonKeMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent kepetakost = new Intent(DetailKostUser.this, PetaKostUser.class);
                kepetakost.putExtra("latitudekost", latitude);
                kepetakost.putExtra("longitudekost", longitude);
                kepetakost.putExtra("dariDetailKost", "Benar");
                startActivity(kepetakost);
            }
        });
    }

    private void loadBookmark(String namaKost, String pemilikBookmark){
        db.collection("Data Bookmark").whereEqualTo("pemilikBookmark", pemilikBookmark).whereEqualTo("namaKost", namaKost).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                QuerySnapshot documentQuery = task.getResult();
                if (!documentQuery.isEmpty()) {
                    buttonBookmark.setImageDrawable(getBaseContext().getResources().getDrawable(R.drawable.bookmarkadded));
                }
            }
        });
    }

    private void setOnClickListener(){
        listener = new ListJenisKamarPemilikAdapter.RecylerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent keDetailKamar = new Intent(getApplicationContext(), DetailKamarUser.class);
                keDetailKamar.putExtra("namakost", daftarKamarKost.get(position).getNamakost());
                keDetailKamar.putExtra("namaKamar", daftarKamarKost.get(position).getNamaKamar());
                keDetailKamar.putExtra("detailkamar", 1);
                startActivity(keDetailKamar);
            }
        };
    }

    private void tambahBookmark( String namaKost, String namaPemilikKost){
        user = firebaseAuth.getCurrentUser();
        assert  user !=null;
        final String userID = user.getUid();

        Pengguna.whereEqualTo("userID", userID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    QuerySnapshot documentquery = task.getResult();
                    DocumentSnapshot document = documentquery.getDocuments().get(0);
                    pemilikBookmark = (String) document.get("nama");
                }
            }
        });

        collectionReference.whereEqualTo("namakost", namaKost).whereEqualTo("pemilik", namaPemilikKost).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    QuerySnapshot documentQuery = task.getResult();
                    DocumentSnapshot documentkost = documentQuery.getDocuments().get(0);
                    jenisKost = (String) documentkost.get("jeniskelaminkost");
                    RangeKost = (String) documentkost.get("rangeharga");
                    gambarKost = (String) documentkost.get("gambarkost");
                    alamatKost = (String) documentkost.get("alamat");


                    ListBookmark listBookmark = new ListBookmark();
                    listBookmark.setPemilikBookmark(pemilikBookmark);
                    listBookmark.setNamaKost(namaKost);
                    listBookmark.setNamaPemilikKost(namaPemilikKost);
                    listBookmark.setGambarKost(gambarKost);
                    listBookmark.setAlamatkost(alamatKost);
                    listBookmark.setJeniskost(jenisKost);
                    listBookmark.setRangehargakost(RangeKost);

                    //Invoking Collection Reference
                    db.collection("Data Bookmark").add(listBookmark).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            Toast.makeText(DetailKostUser.this, "Tambah Bookmark Berhasil!", Toast.LENGTH_LONG).show();
                        }
                    });

                }
            }
        });
    }

    private void HapusBookmark(String pemilikBookmark, String namaKost){
        db.collection("Data Bookmark").whereEqualTo("pemilikBookmark", pemilikBookmark).whereEqualTo("namaKost", namaKost).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    QuerySnapshot documentQuery = task.getResult();
                    DocumentSnapshot document = documentQuery.getDocuments().get(0);
                    String DocumentID = document.getId();
                    db.collection("Data Bookmark").document(DocumentID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(DetailKostUser.this, "Bookmark Telah Dihapus", Toast.LENGTH_LONG).show();
                        }
                    });
                    }
            }
        });
    }

    private void loadDetailKost(String namakost){
        collectionReference.whereEqualTo("namakost", namakost).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    QuerySnapshot documentquery = task.getResult();
                    DocumentSnapshot document = documentquery.getDocuments().get(0);
                    jeniskost.setText((String)document.get("jeniskelaminkost"));
                    namakostdetail.setText((String)document.get("namakost"));
                    deskripsikost.setText((String)document.get("deksripsikost"));
                    notelponkost.setText((String)document.get("nomortelpon"));
                    namapemilikkost.setText((String)document.get("pemilik"));
                    alamatkost.setText((String)document.get("alamat"));

                    notelpKost = (String) document.get("nomortelpon");

                    latitude = (String) document.get("latitude");
                    longitude = (String) document.get("longitude");

                    namaPemilikKost = (String) document.get("pemilik");

                    //Load Reviewer
                    loadReviewer(namakost, namaPemilikKost);
                    loadBintangReview(namakost, namaPemilikKost);


                    //Gambar Kost
                    GambarKostPath = (String)document.get("gambarkost");
                    fotokost = Uri.parse(GambarKostPath);
                    Picasso.get().load(fotokost).into(gambarkostdetail);
                }
            }
        });
    }

    private void loadJenisKamar(String namakost){
        dataKamar.whereEqualTo("namakost", namakost).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    for (QueryDocumentSnapshot JenisKamarKostList : queryDocumentSnapshots){
                        JenisKamarKost jeniskamarlistKost = JenisKamarKostList.toObject(JenisKamarKost.class);
                        daftarKamarKost.add(jeniskamarlistKost);
                    }

                    //Adapter Recycler View
                    setOnClickListener();
                    ListJenisKamarAdapter = new ListJenisKamarPemilikAdapter(DetailKostUser.this, daftarKamarKost, listener);
                    recyclerView.setAdapter(ListJenisKamarAdapter);
                    ListJenisKamarAdapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(DetailKostUser.this, "Data Kamar Tidak Ada", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DetailKostUser.this, "Ups! Ada yang salah!" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadReviewer(String namaKost, String namaPemilikKost){
        db.collection("Data Reviewer").whereEqualTo("namaKost", namaKost).whereEqualTo("namaPemilikKost", namaPemilikKost).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    for (QueryDocumentSnapshot ListReviewer : queryDocumentSnapshots){
                        ListReviewKost DaftarReviewer = ListReviewer.toObject(ListReviewKost.class);
                        daftarReviewer.add(DaftarReviewer);
                    }
                    //Adapter Recycler View
                    listReviewKostUserAdapter = new ListReviewKostUserAdapter(DetailKostUser.this, daftarReviewer);
                    recyclerViewReview.setAdapter(listReviewKostUserAdapter);
                    recyclerViewReview.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
                    listReviewKostUserAdapter.notifyDataSetChanged();
                }else {
                    infoReview.setVisibility(View.VISIBLE);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DetailKostUser.this, "Ambil Data Review Gagal: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadBintangReview(String namaKost, String namaPemilikKost){
        db.collection("Data Reviewer").whereEqualTo("namaKost", namaKost).whereEqualTo("namaPemilikKost", namaPemilikKost).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && !task.getResult().isEmpty()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        ratingkost = Double.parseDouble((String)document.get("bintangReviewer"));
                        totalratingkost = totalratingkost + ratingkost;
                        jumlahReviewer++;
                    }
                    double Rating = totalratingkost/jumlahReviewer;
                    String ratingString = Double.toString(Rating);
                    RatingKost.setText(ratingString);

                    Map<String, Object>UpdateRating =new HashMap<>();
                    UpdateRating.put("Skor Kost", ratingString);
                    db.collection("Data Kost").whereEqualTo("namakost", namaKost).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful() && !task.getResult().isEmpty()) {
                                DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                String DocumentID = documentSnapshot.getId();

                                db.collection("Data Kost").document(DocumentID).update(UpdateRating);
                            }
                        }
                    });
                }else if (task.getResult().isEmpty()){
                    RatingKost.setText("Belum Ada Rating");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DetailKostUser.this, "Ambil Data Review Gagal: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();
        user = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop(){
        super.onStop();

        if(firebaseAuth!=null){
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}