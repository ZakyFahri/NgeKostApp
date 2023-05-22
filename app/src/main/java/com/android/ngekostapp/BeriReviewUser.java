package com.android.ngekostapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.IDNA;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Bundle;
import android.widget.Spinner;

import item.AdapterJenisKelamin;
import item.ItemJenisKelaminKost;

import com.google.android.gms.common.internal.IAccountAccessor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import account.ngekostuser;
import kotlin.collections.UCollectionsKt;
import model.ListReviewKost;

public class BeriReviewUser extends AppCompatActivity {
    //Widget
    EditText InputReview;
    ImageView gambarKost;
    ImageButton bintang1, bintang2, bintang3, bintang4, bintang5;
    TextView InfoBintang, NamaKost;
    Button SimpanReview;
    Boolean sudahdiklik;

    //UserID dan Nama Kost dan Pemilik Kost
    private String userID;
    private String namaPemilik;
    private String namaKost;
    private String namaKamar;
    private String harga;

    //Dialog
    Dialog popupberhasil;
    Dialog popupgagal;

    //Gambar Path
    private String GambarKostPath;

    //Bintang
    private String SkorReview;

    //Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    //FireStore
    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private CollectionReference pemilik = database.collection("Pemilik");
    private CollectionReference datakost = database.collection("Data Kost");
    private CollectionReference dataReview = database.collection("Data Reviewer");

    //Uri Gambar Kost
    private Uri fotoKost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_beri_review_user);

        gambarKost = findViewById(R.id.postImageKost);
        InputReview = findViewById(R.id.inputReview);
        bintang1 = (ImageButton) findViewById(R.id.bintang1);
        bintang2 = (ImageButton) findViewById(R.id.bintang2);
        bintang3 = (ImageButton) findViewById(R.id.bintang3);
        bintang4 = (ImageButton) findViewById(R.id.bintang4);
        bintang5 = (ImageButton) findViewById(R.id.bintang5);
        InfoBintang = findViewById(R.id.reviewInfo);
        SimpanReview = findViewById(R.id.buttonBeriReview);
        NamaKost = findViewById(R.id.NamaKostDetail);

        popupberhasil = new Dialog(this);
        popupgagal = new Dialog(this);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = firebaseAuth.getInstance();

        //Bundle Intent
        Bundle extra = getIntent().getExtras();
        if(extra != null){
            namaKost = extra.getString("namakost");
            namaPemilik = extra.getString("Pemilik");
            namaKamar = extra.getString("namaKamar");
            harga = extra.getString("harga");
            loadDataKost(namaKost, namaPemilik);
        }

        //Gambar Bintang
        bintang1.setBackgroundResource(R.drawable.star_empty);
        bintang2.setBackgroundResource(R.drawable.star_empty);
        bintang3.setBackgroundResource(R.drawable.star_empty);
        bintang4.setBackgroundResource(R.drawable.star_empty);
        bintang5.setBackgroundResource(R.drawable.star_empty);
        sudahdiklik = false;

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if(user!=null){

                }else{

                }
            }
        };

        //             -------------------- SECTION GIVE STAR ---------------------------------------------------
        bintang1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sudahdiklik == true){
                    bintang2.setBackgroundResource(R.drawable.star_empty);
                    bintang3.setBackgroundResource(R.drawable.star_empty);
                    bintang4.setBackgroundResource(R.drawable.star_empty);
                    bintang5.setBackgroundResource(R.drawable.star_empty);
                }

                SkorReview = "1";
                bintang1.setBackgroundResource(R.drawable.star);
                InfoBintang.setText(SkorReview+"/5");

                sudahdiklik = true;
            }
        });

        bintang2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sudahdiklik == true){
                    bintang3.setBackgroundResource(R.drawable.star_empty);
                    bintang4.setBackgroundResource(R.drawable.star_empty);
                    bintang5.setBackgroundResource(R.drawable.star_empty);
                }

                SkorReview = "2";
                bintang1.setBackgroundResource(R.drawable.star);
                bintang2.setBackgroundResource(R.drawable.star);
                InfoBintang.setText(SkorReview+"/5");

                sudahdiklik = true;
            }
        });

        bintang3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sudahdiklik == true){
                    bintang4.setBackgroundResource(R.drawable.star_empty);
                    bintang5.setBackgroundResource(R.drawable.star_empty);
                }

                SkorReview = "3";
                bintang1.setBackgroundResource(R.drawable.star);
                bintang2.setBackgroundResource(R.drawable.star);
                bintang3.setBackgroundResource(R.drawable.star);
                InfoBintang.setText(SkorReview+"/5");

                sudahdiklik = true;
            }
        });

        bintang4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sudahdiklik == true){
                    bintang5.setBackgroundResource(R.drawable.star_empty);
                }

                SkorReview = "4";
                bintang1.setBackgroundResource(R.drawable.star);
                bintang2.setBackgroundResource(R.drawable.star);
                bintang3.setBackgroundResource(R.drawable.star);
                bintang4.setBackgroundResource(R.drawable.star);
                InfoBintang.setText(SkorReview+"/5");

                sudahdiklik = true;
            }
        });

        bintang5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SkorReview = "5";
                bintang1.setBackgroundResource(R.drawable.star);
                bintang2.setBackgroundResource(R.drawable.star);
                bintang3.setBackgroundResource(R.drawable.star);
                bintang4.setBackgroundResource(R.drawable.star);
                bintang5.setBackgroundResource(R.drawable.star);
                InfoBintang.setText(SkorReview+"/5");

                sudahdiklik = true;
            }
        });

        //             -------------------- END SECTION GIVE STAR ---------------------------------------------------

        SimpanReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final LoadingAlert loadingAlert = new LoadingAlert(BeriReviewUser.this);

                loadingAlert.StartAlertDialog();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadingAlert.closeAlertDialog();
                        SimpanDataReview(SkorReview);
                    }
                }, 5000);
            }
        });

    }

    private void loadDataKost(String namaKost, String namaPemilik){
        datakost.whereEqualTo("namakost", namaKost).whereEqualTo("pemilik", namaPemilik).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot documentquery = task.getResult();
                    DocumentSnapshot document = documentquery.getDocuments().get(0);
                    NamaKost.setText((String) document.get("namakost"));

                    //Gambar Kost
                    GambarKostPath = (String) document.get("gambarkost");
                    fotoKost = Uri.parse(GambarKostPath);
                    Picasso.get().load(fotoKost).into(gambarKost);
                }

            }
        });
    }

    private void SimpanDataReview(String SkorReview){
        final String ulasanReview = InputReview.getText().toString().trim();

        user = firebaseAuth.getCurrentUser();
        assert user != null;
        userID = user.getUid();

        database.collection("Pengguna").whereEqualTo("userID", userID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot documentQuery = task.getResult();
                    DocumentSnapshot document = documentQuery.getDocuments().get(0);
                    final String gambarReviewer = (String) document.get("fotoprofil");
                    final String namaReviewer = (String) document.get("nama");

                    if (!TextUtils.isEmpty(ulasanReview)){
                        //Objek Review
                        ListReviewKost tambahDataReview = new ListReviewKost();
                        tambahDataReview.setNamaReviewer(namaReviewer);
                        tambahDataReview.setFotoReviewer(gambarReviewer);
                        tambahDataReview.setBintangReviewer(SkorReview);
                        tambahDataReview.setUlasanReviewer(ulasanReview);
                        tambahDataReview.setNamaKost(namaKost);
                        tambahDataReview.setNamaPemilikKost(namaPemilik);

                        dataReview.add(tambahDataReview).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                popupberhasil.setContentView(R.layout.popuptambahreviewberhasil);
                                popupberhasil.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                popupberhasil.show();

                                final Handler handler=new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        popupberhasil.dismiss();

                                        Intent keDetailTransaksiUser = new Intent(BeriReviewUser.this,DetailTransaksiUser.class);
                                        keDetailTransaksiUser.putExtra("namakost", namaKost);
                                        keDetailTransaksiUser.putExtra("namaPembeli", namaReviewer);
                                        keDetailTransaksiUser.putExtra("namaPemilik", namaPemilik);
                                        keDetailTransaksiUser.putExtra("namaKamar", namaKamar);
                                        keDetailTransaksiUser.putExtra("harga", harga);
//                                startActivity(keDetailTransaksiUser);
                                        finish();

                                    }
                                }, 3000);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                popupgagal.setContentView(R.layout.popupreviewgagal);
                                popupgagal.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                popupgagal.show();

                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        popupgagal.dismiss();
                                    }
                                }, 3000);
                            }
                        });
                    }else {
                        Toast.makeText(BeriReviewUser.this, "Mohon Isi Ulasan Anda!", Toast.LENGTH_LONG).show();
                    }
                }
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