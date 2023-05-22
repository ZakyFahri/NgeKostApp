package com.android.ngekostapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.MenuHost;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Window;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import de.hdodenhof.circleimageview.CircleImageView;
import model.JenisKamarKost;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DetailKostPemilik extends AppCompatActivity {
    //Widget
    ImageView gambarkostdetail;
    TextView infoReview, jeniskost, ratingkos, alamatkost, deskripsikost, notelponkost, namapemilikkost, namakostdetail;
    ImageButton editDataKost, tombolKembali, hapuskost;
    CircleImageView TambahKamar;

    //Dialog
    Dialog popupberhasil, popupgagal;

    //Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    //Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private CollectionReference collectionReference = db.collection("Data Kost");
    private CollectionReference dataKamar = db.collection("Data Kamar");

    //String untuk Data Kost
    private String namaKost;
    private String namaKamar;
    private String namaPemilikKost;
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
        setContentView(R.layout.activity_detail_kost_pemilik);

        gambarkostdetail = findViewById(R.id.gambarkostdetail);
        jeniskost = findViewById(R.id.jeniskostdetail);
        ratingkos = findViewById(R.id.ratingkostdetail);
        alamatkost = findViewById(R.id.alamatkostdetail);
        deskripsikost = findViewById(R.id.DeskripsiKostDetail);
        notelponkost = findViewById(R.id.notelppemilikkostdetail);
        namapemilikkost = findViewById(R.id.namapemilikkostDetail);
        namakostdetail = findViewById(R.id.NamaKostDetail);
        TambahKamar = (CircleImageView) findViewById(R.id.TambahKamar);

        popupberhasil = new Dialog(this);
        popupgagal = new Dialog(this);

        hapuskost = findViewById(R.id.pinhapuskost);
        tombolKembali = (ImageButton) findViewById(R.id.tombolkembali);

        editDataKost = (ImageButton) findViewById(R.id.pineditkost);

        infoReview=findViewById(R.id.ReviewInfoEmpty);
        infoReview.setVisibility(View.GONE);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = firebaseAuth.getInstance();

        Bundle extra = getIntent().getExtras();
        if(extra != null){
            namaKost = extra.getString("namakost");
            loadDetailKost(namaKost);
            loadJenisKamar(namaKost);
        }
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

        TambahKamar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent keAddJenisKamar = new Intent(DetailKostPemilik.this, TambahJenisKamarPemilik.class);
                keAddJenisKamar.putExtra("namakost", namaKost);
                startActivity(keAddJenisKamar);
            }
        });

        editDataKost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent keEditKost = new Intent(DetailKostPemilik.this, EditDataKost.class);
                keEditKost.putExtra("namakost", namaKost);
                startActivity(keEditKost);
            }
        });

        tombolKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        hapuskost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWarningDialog();
            }
        });
    }

    private void showWarningDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailKostPemilik.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(DetailKostPemilik.this).inflate(
                R.layout.dialogcontainer,(ConstraintLayout)findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        ((TextView)view.findViewById(R.id.textTitle)).setText("Konfirmasi Hapus");
        ((TextView)view.findViewById(R.id.textMessage)).setText("Apakah Anda Yakin Menghapus Kost Ini?");
        ((Button)view.findViewById(R.id.buttonYes)).setText("Ya");
        ((Button)view.findViewById(R.id.buttonNo)).setText("Tidak");
        ((ImageView)view.findViewById(R.id.imageIcon)).setImageResource(R.drawable.warning);

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonNo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();

            }
        });

        view.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();

                final LoadingAlert loadingAlert = new LoadingAlert(DetailKostPemilik.this);

                loadingAlert.StartAlertDialog();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadingAlert.closeAlertDialog();;
                        HapusKost(namaKost);
                    }
                }, 5000);
            }
        });

        if(alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }


    //           ------------------SECTION HAPUS KOST dan Data Dari Colelction Lainnya------------------
    private void HapusKost(String namaKost){
        user = firebaseAuth.getCurrentUser();
        assert user!=null;
        final String userID = user.getUid();

        db.collection("Data Kost").whereEqualTo("userID", userID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                QuerySnapshot documentquery = task.getResult();
                DocumentSnapshot document = documentquery.getDocuments().get(0);
                namaPemilikKost = (String) document.get("pemilik");

                db.collection("Data Kost").whereEqualTo("namakost", namaKost).whereEqualTo("pemilik", namaPemilikKost).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        QuerySnapshot documentquery = task.getResult();
                        DocumentSnapshot document = documentquery.getDocuments().get(0);
                        String DocumentID = document.getId();

                        db.collection("Data Kost").document(DocumentID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                popupberhasil.setContentView(R.layout.popuphapuskostberhasil);
                                popupberhasil.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                popupberhasil.show();

                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        popupberhasil.dismiss();
                                        //Hapus Data Collection Lainnya
                                        HapusDataKamarKost(namaKost, namaPemilikKost);
                                        HapusDataBookmarkdariKost(namaKost, namaPemilikKost);
                                        HapusDataTransaksiDariKost(namaKost, namaPemilikKost);
                                        HapusDataReviewerDariKost(namaKost, namaPemilikKost);

                                        Intent balikkelistKost = new Intent(DetailKostPemilik.this, ListKostPemilik.class);
                                        finish();
                                    }
                                }, 3000);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                popupgagal.setContentView(R.layout.popuphapuskostgagal);
                                popupgagal.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                popupgagal.show();

                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        popupgagal.dismiss();
                                    }
                                },3000);
                            }
                        });
                    }
                });
            }
        });
    }



    private void HapusDataKamarKost(String namaKost, String namaPemilikKost){
        dataKamar.whereEqualTo("namakost", namaKost).whereEqualTo("namaPemilik", namaPemilikKost).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && !task.getResult().isEmpty()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        String DocumentID = document.getId();
                        db.collection("Data Kamar").document(DocumentID).delete();
                    }
                }
            }
        });
    }

    private void HapusDataBookmarkdariKost(String namaKost, String namaPemilikKost){
        db.collection("Data Bookmark").whereEqualTo("namaKost", namaKost).whereEqualTo("namaPemilikKost", namaPemilikKost).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && !task.getResult().isEmpty()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        String DocumentID = document.getId();
                        db.collection("Data Bookmark").document(DocumentID).delete();
                    }
                }
            }
        });

    }

    private void HapusDataTransaksiDariKost(String namaKost, String namaPemilikKost){
        db.collection("Data Transaksi").whereEqualTo("namaKost", namaKost).whereEqualTo("namaPemilik", namaPemilikKost).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                    for (QueryDocumentSnapshot document : task.getResult()){
                        String DocumentID = document.getId();
                        db.collection("Data Transaksi").document(DocumentID).delete();
                    }
                }
            }
        });
    }

    private void HapusDataReviewerDariKost(String namaKost, String namaPemilikKost){
        db.collection("Data Reviewer").whereEqualTo("namaKost", namaKost).whereEqualTo("namaPemilikKost", namaPemilikKost).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                    for (QueryDocumentSnapshot document : task.getResult()){
                        String DocumentID = document.getId();
                        db.collection("Data Reviewer").document(DocumentID).delete();
                    }
                }
            }
        });
    }

    //           ------------------ END SECTION HAPUS KOST dan Data Dari Colelction Lainnya------------------


    private void setOnClickListener(){
        listener = new ListJenisKamarPemilikAdapter.RecylerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent keDetailKamar = new Intent(getApplicationContext(), DetailKamarPemilik.class);
                keDetailKamar.putExtra("namakost", daftarKamarKost.get(position).getNamakost());
                keDetailKamar.putExtra("namaKamar", daftarKamarKost.get(position).getNamaKamar());
                startActivity(keDetailKamar);
            }
        };
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

                    namaPemilikKost = (String) document.get("pemilik");

                    loadReviewer(namaKost, namaPemilikKost);

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
                    ListJenisKamarAdapter = new ListJenisKamarPemilikAdapter(DetailKostPemilik.this, daftarKamarKost, listener);
                    recyclerView.setAdapter(ListJenisKamarAdapter);
                    ListJenisKamarAdapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(DetailKostPemilik.this, "Data Kamar Tidak Ada", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DetailKostPemilik.this, "Ups! Ada yang salah!" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadReviewer(String namaKost, String namaPemilikKost) {
        db.collection("Data Reviewer").whereEqualTo("namaKost", namaKost).whereEqualTo("namaPemilikKost", namaPemilikKost).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    for (QueryDocumentSnapshot ListReviewer : queryDocumentSnapshots) {
                        ListReviewKost DaftarReviewer = ListReviewer.toObject(ListReviewKost.class);
                        daftarReviewer.add(DaftarReviewer);
                    }
                    //Adapter Recycler View
                    listReviewKostUserAdapter = new ListReviewKostUserAdapter(DetailKostPemilik.this, daftarReviewer);
                    recyclerViewReview.setAdapter(listReviewKostUserAdapter);
                    recyclerViewReview.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
                    listReviewKostUserAdapter.notifyDataSetChanged();
                } else {
                    infoReview.setVisibility(View.VISIBLE);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DetailKostPemilik.this, "Ambil Data Review Gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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