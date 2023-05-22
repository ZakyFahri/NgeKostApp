package com.android.ngekostapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.Toast;
import android.os.Bundle;
import android.widget.Spinner;

import item.AdapterJenisKelamin;
import item.ItemJenisKelaminKost;

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
import com.google.firebase.firestore.QueryDocumentSnapshot;
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
import model.ListKost;

public class EditDataKost extends AppCompatActivity {
    private static final int GALLERY_CODE = 1;

    //Widget
    EditText namakost, latitude, longitude, rangeharga, jeniskost, alamatkost, notelp, deskripsikost;
    ImageView imageKost;
    Button choosephoto, editkost;
    Spinner jenisKelaminKost;
    ImageButton tombolKembali;

    //Dialog
    Dialog popupberhasil;
    Dialog popupgagal;

    //UserID, Nama Pemilik Kost dan Nama Kost
    private String userID;
    private String namaPemilik;
    private String NamaKost;
    private String GambarKostPath;
    private String GambarKostPathLama;

    //Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    //FireStore
    private StorageReference storageReference;
    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    private CollectionReference pemilik = database.collection("Pemilik");
    private CollectionReference datakost = database.collection("Data Kost");
    private CollectionReference dataKamar = database.collection("Data Kamar");

    //Gambar
    private Uri gambarkostlama, gambarkostbaru;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_edit_data_kost);

        namakost = findViewById(R.id.inputNamaKost);
        latitude = findViewById(R.id.inputLatitude);
        longitude = findViewById(R.id.inputLongitude);
        rangeharga = findViewById(R.id.inputHarga);
        alamatkost = findViewById(R.id.inputAlamatKost);
        notelp = findViewById(R.id.InputNomorTelepon);
        deskripsikost = findViewById(R.id.InputDeskripsi);
        imageKost = findViewById(R.id.postImageView);

        popupberhasil = new Dialog(this);
        popupgagal = new Dialog(this);

        tombolKembali = findViewById(R.id.tombolkembali);

        choosephoto = findViewById(R.id.buttonambilfotoeditkost);
        editkost = findViewById(R.id.buttonEditKost);


        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = firebaseAuth.getInstance();

        jenisKelaminKost = findViewById(R.id.JenisKelaminKost);

        ArrayList<ItemJenisKelaminKost> ListKelamin = new ArrayList<>();
        ListKelamin.add(new ItemJenisKelaminKost("Laki-Laki",R.drawable.man));
        ListKelamin.add(new ItemJenisKelaminKost("Perempuan",R.drawable.woman));
        ListKelamin.add(new ItemJenisKelaminKost("Campur",R.drawable.campur));

        //Adapater untuk Spinner
        AdapterJenisKelamin AdapterKelamin = new AdapterJenisKelamin(this, ListKelamin);
        if(jenisKelaminKost != null){
            jenisKelaminKost.setAdapter(AdapterKelamin);
        }

        Bundle extra = getIntent().getExtras();
        if (extra!=null){
            NamaKost = extra.getString("namakost");
            loadDataKost(NamaKost);
        }

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user!=null){

                }else{

                }
            }
        };

        choosephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Mendapat Gambar
                Intent galleryIntent =new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_CODE);
            }
        });

        jenisKelaminKost.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ItemJenisKelaminKost items = (ItemJenisKelaminKost) adapterView.getSelectedItem();
                String jeniskelaminkost = items.getJenisKelaminText();

                editkost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final LoadingAlert loadingAlert = new LoadingAlert(EditDataKost.this);

                        loadingAlert.StartAlertDialog();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                loadingAlert.closeAlertDialog();
                                updateKost(jeniskelaminkost, NamaKost);
                            }
                        }, 5000);
                    }
                });

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        tombolKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    //                    -------------------SECTION UBAH DATA KOST PADA COLLECTION LAIN-------------------
    private void editNamaKostKamar (String namaKostLama, String inputNamaEditKost){
        dataKamar.whereEqualTo("namakost", namaKostLama).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && !task.getResult().isEmpty()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        Map<String, Object> gantiNamaKost = new HashMap<>();
                        gantiNamaKost.put("namakost", inputNamaEditKost);
                        String DocumentID = document.getId();
                        dataKamar.document(DocumentID).update(gantiNamaKost);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void editNamaKostBookmark(String namaKostLama, String inputNamaEditKost){
        database.collection("Data Bookmark").whereEqualTo("namaKost", namaKostLama).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && !task.getResult().isEmpty()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        Map<String, Object> gantiNamaKostBookmark = new HashMap<>();
                        gantiNamaKostBookmark.put("namaKost", inputNamaEditKost);
                        String DocumentID = document.getId();
                        database.collection("Data Bookmark").document(DocumentID).update(gantiNamaKostBookmark);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void editNamaKostTransaksi(String namaKostLama, String inputNamaEditKost){
        database.collection("Data Transaksi").whereEqualTo("namaKost", namaKostLama).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && !task.getResult().isEmpty()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        Map<String, Object> gantiNamaKostTransaksi = new HashMap<>();
                        gantiNamaKostTransaksi.put("namaKost", inputNamaEditKost);
                        String DocumentID = document.getId();
                        database.collection("Data Transaksi").document(DocumentID).update(gantiNamaKostTransaksi);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    //                    ---------------------------UNTUK GAMBAR KOST---------------------------
    private void editGambarKostBookmark(String gambarKostPathLama, String gambarKostPath){
        database.collection("Data Bookmark").whereEqualTo("gambarKost", gambarKostPathLama).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && !task.getResult().isEmpty()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        Map<String, Object> gantiGambarKostBookmark = new HashMap<>();
                        gantiGambarKostBookmark.put("gambarKost", gambarKostPath);
                        String DocumentID = document.getId();
                        database.collection("Data Bookmark").document(DocumentID).update(gantiGambarKostBookmark);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void editGambarKostTransaksi(String gambarKostPathLama, String gambarKostPath){
        database.collection("Data Transaksi").whereEqualTo("gambarkost", gambarKostPathLama).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && !task.getResult().isEmpty()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        Map<String, Object> gantiGambarKostTransaksi = new HashMap<>();
                        gantiGambarKostTransaksi.put("gambarkost", gambarKostPath);
                        String DocumentID = document.getId();
                        database.collection("Data Transaksi").document(DocumentID).update(gantiGambarKostTransaksi);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }



    //                    ------------------- END SECTION UBAH DATA KOST PADA COLLECTION LAIN-------------------

    protected void loadDataKost(String NamaKost){
        datakost.whereEqualTo("namakost", NamaKost).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    QuerySnapshot documentquery = task.getResult();
                    DocumentSnapshot document = documentquery.getDocuments().get(0);
                    namakost.setText(NamaKost);
                    latitude.setText((String)document.get("latitude"));
                    longitude.setText((String)document.get("longitude"));
                    rangeharga.setText((String)document.get("rangeharga"));
                    deskripsikost.setText((String)document.get("deksripsikost"));
                    notelp.setText((String)document.get("nomortelpon"));
                    alamatkost.setText((String)document.get("alamat"));

                    //Gambar Kost
                    GambarKostPathLama = (String)document.get("gambarkost");
                    gambarkostlama = Uri.parse(GambarKostPathLama);
                    Picasso.get().load(gambarkostlama).into(imageKost);
                }
            }
        });
    }

    private void updateKost(String jeniskelaminkost, String NamaAwalKost){
        final String inputNamaEditKost = namakost.getText().toString().trim();
        final String inputAlamatKost = alamatkost.getText().toString().trim();
        final String inputLatitudeKost = latitude.getText().toString().trim();
        final String inputLongitudeKost = longitude.getText().toString().trim();
        final String inputRangeKost = rangeharga.getText().toString().trim();
        final String inputDeskripsiKost = deskripsikost.getText().toString().trim();
        final String nomortelponKost = notelp.getText().toString().trim();

        user = firebaseAuth.getCurrentUser();
        assert user !=null;
        final String userID = user.getUid();

        pemilik.whereEqualTo("userID", userID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    QuerySnapshot documentquery = task.getResult();
                    DocumentSnapshot document = documentquery.getDocuments().get(0);
                    namaPemilik = (String)document.get("nama");

                    if(!TextUtils.isEmpty(inputNamaEditKost) && !TextUtils.isEmpty(inputAlamatKost) && !TextUtils.isEmpty(inputLatitudeKost) && !TextUtils.isEmpty(inputLongitudeKost) && gambarkostbaru == null){
//                        final StorageReference filepath = storageReference.child("gambar_kost_Tanpa_Update").child("gambarkostPemilik" + namaPemilik + Timestamp.now().getNanoseconds());
//                        filepath.putFile(gambarkostlama).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                            @Override
//                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                                filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                    @Override
//                                    public void onSuccess(Uri uri) {
//                                        GambarKostPath = uri.toString();
//                                    }
//                                });
//                            }
//                        });

                        imageKost.setImageURI(Uri.parse(GambarKostPathLama));
                        Map<String, Object> UpdateData = new HashMap<>();
                        UpdateData.put("namakost", inputNamaEditKost);
                        UpdateData.put("search", namakost.getText().toString().trim().toLowerCase());
                        UpdateData.put("latitude", inputLatitudeKost);
                        UpdateData.put("longitude", inputLongitudeKost);
                        UpdateData.put("rangeharga", inputRangeKost);
                        UpdateData.put("deksripsikost", inputDeskripsiKost);
                        UpdateData.put("jeniskelaminkost", jeniskelaminkost);
                        UpdateData.put("nomortelpon",  nomortelponKost);
                        UpdateData.put("pemilik", namaPemilik);
                        UpdateData.put("gambarkost", GambarKostPathLama);

                        Picasso.get().load(Uri.parse(GambarKostPathLama)).into(imageKost);
                        String GambarKostBaru = GambarKostPathLama;

                        datakost.whereEqualTo("namakost", NamaAwalKost).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful() && !task.getResult().isEmpty()){
                                    DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                    String DocumentID = documentSnapshot.getId();

                                    //Edit Data Untuk Colelction Lainnya
                                    editNamaKostKamar(NamaAwalKost, inputNamaEditKost);
                                    editNamaKostBookmark(NamaAwalKost, inputNamaEditKost);
                                    editNamaKostTransaksi(NamaAwalKost, inputNamaEditKost);
                                    //Edit Gambar Collection Lain
                                    editGambarKostBookmark(GambarKostPathLama, GambarKostBaru);
                                    editGambarKostTransaksi(GambarKostPathLama, GambarKostBaru);


                                    datakost.document(DocumentID).update(UpdateData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            popupberhasil.setContentView(R.layout.popupeditkostberhasil);
                                            popupberhasil.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                            popupberhasil.show();

                                            final Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    popupberhasil.dismiss();
                                                    Intent kedetailkostpemilik = new Intent(EditDataKost.this, DetailKostPemilik.class);
                                                    kedetailkostpemilik.putExtra("namakost", inputNamaEditKost);
//                                                  startActivity(kedetailkostpemilik);
                                                    finish();
                                                }
                                            }, 3000);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            popupgagal.setContentView(R.layout.popupeditkostgagal);
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
                                }
                            }
                        });
                    }else if(!TextUtils.isEmpty(inputNamaEditKost) && !TextUtils.isEmpty(inputAlamatKost) && !TextUtils.isEmpty(inputLatitudeKost) && !TextUtils.isEmpty(inputLongitudeKost) && gambarkostbaru != null){
                        final StorageReference filepath = storageReference.child("gambar_kost_update").child("gambarkostPemilik" + namaPemilik + Timestamp.now().getNanoseconds());
                        filepath.putFile(gambarkostbaru).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        GambarKostPath = uri.toString();

                                        Map<String, Object> UpdateData = new HashMap<>();
                                        UpdateData.put("namakost", inputNamaEditKost);
                                        UpdateData.put("search", namakost.getText().toString().trim().toLowerCase());
                                        UpdateData.put("latitude", inputLatitudeKost);
                                        UpdateData.put("longitude", inputLongitudeKost);
                                        UpdateData.put("rangeharga", inputRangeKost);
                                        UpdateData.put("deksripsikost", inputDeskripsiKost);
                                        UpdateData.put("jeniskelaminkost", jeniskelaminkost);
                                        UpdateData.put("nomortelpon",  nomortelponKost);
                                        UpdateData.put("pemilik", namaPemilik);
                                        UpdateData.put("gambarkost", GambarKostPath);

                                        datakost.whereEqualTo("namakost", NamaAwalKost).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if(task.isSuccessful() && !task.getResult().isEmpty()) {
                                                    DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                                    String DocumentID = documentSnapshot.getId();

                                                    //Edit Data Untuk Colelction Lainnya
                                                    editNamaKostKamar(NamaAwalKost, inputNamaEditKost);
                                                    editNamaKostBookmark(NamaAwalKost, inputNamaEditKost);
                                                    editNamaKostTransaksi(NamaAwalKost, inputNamaEditKost);
                                                    //Edit Gambar Collection Lain
                                                    editGambarKostBookmark(GambarKostPathLama, GambarKostPath);
                                                    editGambarKostTransaksi(GambarKostPathLama, GambarKostPath);


                                                    datakost.document(DocumentID).update(UpdateData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            popupberhasil.setContentView(R.layout.popupeditkostberhasil);
                                                            popupberhasil.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                            popupberhasil.show();

                                                            final Handler handler = new Handler();
                                                            handler.postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    popupberhasil.dismiss();
                                                                    Intent kedetailkostpemilik = new Intent(EditDataKost.this, DetailKostPemilik.class);
                                                                    kedetailkostpemilik.putExtra("namakost", inputNamaEditKost);
//                                                                  startActivity(kedetailkostpemilik);
                                                                    finish();
                                                                }
                                                            }, 3000);
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            popupgagal.setContentView(R.layout.popupeditkostgagal);
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
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK){
            if (data !=null){
                gambarkostbaru = data.getData(); //Mendapatkan Path Aktual
                imageKost.setImageURI(gambarkostbaru); //Menampilkan Gambar
            }
        }
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