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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EditJenisKamarPemilik extends AppCompatActivity {
    private static final int GALLERY_CODE = 1;

    //Widget
    EditText namakamar, hargakamar, lamasewakamar, kamartersedia, luaskamar, fasilitas;
    ImageView imageKamar;
    Button choosephoto, EditKamar;

    //Ambil Data Kamar untuk simpan ke Class
    private String namaKamar;
    private String namaKost;
    private String GambarKamarPath;

    //Dialog
    Dialog popupberhasil;
    Dialog popupgagal;

    //Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    //FireStore
    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private CollectionReference DataKamarKost = database.collection("Data Kamar");
    private CollectionReference datakost = database.collection("Data Kost");

    //Gambar
    private Uri gambarkamarLama, gambarKamarBaru;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_edit_jenis_kamar_pemilik);

        namakamar = findViewById(R.id.NamaKamar);
        hargakamar = findViewById(R.id.HargaKamar);
        lamasewakamar = findViewById(R.id.LamaSewaKamar);
        kamartersedia = findViewById(R.id.JumlahKamarTersedia);
        luaskamar = findViewById(R.id.LuasKamar);
        fasilitas = findViewById(R.id.FasilitasKamar);

        imageKamar = findViewById(R.id.postImageView);

        popupgagal = new Dialog(this);
        popupberhasil = new Dialog(this);

        choosephoto = findViewById(R.id.buttonambilfoto);
        EditKamar = findViewById(R.id.EditKamar);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = firebaseAuth.getInstance();

        Bundle extra = getIntent().getExtras();
        if(extra != null){
            namaKamar = extra.getString("namakamar");
            namaKost = extra.getString("namakost");
            loadDataKamar(namaKamar);
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

        EditKamar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final LoadingAlert loadingAlert = new LoadingAlert(EditJenisKamarPemilik.this);

                loadingAlert.StartAlertDialog();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadingAlert.closeAlertDialog();
                    }
                },5000);
                updateKamar(namaKamar, namaKost);
            }
        });
    }
    //                    -------------------SECTION UBAH DATA KAMAR PADA COLLECTION LAIN-------------------

    private void editNamaKamarTransaksi(String namaKamar, String inputNamaEditKamar){
        database.collection("Data Transaksi").whereEqualTo("namaKamar", namaKamar).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && !task.getResult().isEmpty()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        Map<String, Object> gantiNamaKamarTransaksi = new HashMap<>();
                        gantiNamaKamarTransaksi.put("namaKamar", inputNamaEditKamar);
                        String DocumentID = document.getId();
                        database.collection("Data Transaksi").document(DocumentID).update(gantiNamaKamarTransaksi);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }


    //                    ------------------- END SECTION UBAH DATA KAMAR PADA COLLECTION LAIN-------------------


    protected void loadDataKamar(String namaKamar){
        DataKamarKost.whereEqualTo("namaKamar", namaKamar).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    QuerySnapshot documentquery = task.getResult();
                    DocumentSnapshot document = documentquery.getDocuments().get(0);

                    namakamar.setText(namaKamar);
                    hargakamar.setText((String)document.get("harga"));
                    lamasewakamar.setText((String)document.get("lamaSewa"));
                    kamartersedia.setText((String)document.get("kamarTersedia"));
                    luaskamar.setText((String)document.get("luasKamar"));
                    fasilitas.setText((String)document.get("fasilitasKamar"));

                    //Gambar Kamar
                    GambarKamarPath = (String) document.get("gambarKamar");
                    gambarkamarLama = Uri.parse(GambarKamarPath);
                    Picasso.get().load(gambarkamarLama).into(imageKamar);
                }
            }
        });
    }

    private void updateKamar(String namaKamar, String namaKost){
        final String inputNamaEditKamar = namakamar.getText().toString().trim();
        final String inputHargaEditKamar = hargakamar.getText().toString().trim();
        final String inputLamaSewaEditKamar = lamasewakamar.getText().toString().trim();
        final String inputKamarTersediaEditKamar = kamartersedia.getText().toString().trim();
        final String inputLuasEditKamar = luaskamar.getText().toString().trim();
        final String inputFasilitasEditKamar = fasilitas.getText().toString().trim();

        if(!TextUtils.isEmpty(inputNamaEditKamar) && !TextUtils.isEmpty(inputFasilitasEditKamar) && !TextUtils.isEmpty(inputHargaEditKamar) && !TextUtils.isEmpty(inputKamarTersediaEditKamar) && !TextUtils.isEmpty(inputFasilitasEditKamar) && gambarKamarBaru == null){
            imageKamar.setImageURI(Uri.parse(GambarKamarPath));

            Locale localID = new Locale("IND", "ID");
            NumberFormat numberFormat = NumberFormat.getNumberInstance(localID);
            double konversi = Double.parseDouble(hargakamar.getText().toString());

            String hargaKonversi = "Rp" + numberFormat.format(konversi);

            Map<String, Object> UpdateKamar = new HashMap<>();
            UpdateKamar.put("namaKamar", inputNamaEditKamar);
            UpdateKamar.put("harga", inputHargaEditKamar);
            UpdateKamar.put("hargaKonversi", hargaKonversi);
            UpdateKamar.put("lamaSewa", inputLamaSewaEditKamar);
            UpdateKamar.put("kamarTersedia", inputKamarTersediaEditKamar);
            UpdateKamar.put("luasKamar", inputLuasEditKamar);
            UpdateKamar.put("fasilitasKamar", inputFasilitasEditKamar);
            UpdateKamar.put("gambarKamar", GambarKamarPath);

            Picasso.get().load(Uri.parse(GambarKamarPath)).into(imageKamar);

            DataKamarKost.whereEqualTo("namaKamar", namaKamar).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful() && !task.getResult().isEmpty()){
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        String DocumentID = documentSnapshot.getId();

                        // EDIT NAMA KAMAR PEMBELI
                        editNamaKamarTransaksi(namaKamar, inputNamaEditKamar);

                        DataKamarKost.document(DocumentID).update(UpdateKamar).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                popupberhasil.setContentView(R.layout.popupeditkamarberhasil);
                                popupberhasil.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                popupberhasil.show();

                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        popupberhasil.dismiss();;
                                        Intent keDetailKamarPemilik = new Intent(EditJenisKamarPemilik.this, DetailKamarPemilik.class);
                                        keDetailKamarPemilik.putExtra("namaKamar", inputNamaEditKamar);
                                        keDetailKamarPemilik.putExtra("namakost", namaKost);
//                                startActivity(keDetailKamarPemilik);
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
                                },3000);
                            }
                        });
                    }
                }
            });
        } else if (!TextUtils.isEmpty(inputNamaEditKamar) && !TextUtils.isEmpty(inputFasilitasEditKamar) && !TextUtils.isEmpty(inputHargaEditKamar) && !TextUtils.isEmpty(inputKamarTersediaEditKamar) && !TextUtils.isEmpty(inputFasilitasEditKamar) && gambarKamarBaru != null) {
            final StorageReference filepath = storageReference.child("gambar_kamar_update").child("gambarKamarKost"+ namaKost + Timestamp.now().getNanoseconds());
            filepath.putFile(gambarKamarBaru).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            GambarKamarPath = uri.toString();

                            Locale localID = new Locale("IND", "ID");
                            NumberFormat numberFormat = NumberFormat.getNumberInstance(localID);
                            double konversi = Double.parseDouble(hargakamar.getText().toString());

                            String hargaKonversi = "Rp" + numberFormat.format(konversi);

                            Map<String, Object> UpdateKamar = new HashMap<>();
                            UpdateKamar.put("namaKamar", inputNamaEditKamar);
                            UpdateKamar.put("harga", inputHargaEditKamar);
                            UpdateKamar.put("hargaKonversi", hargaKonversi);
                            UpdateKamar.put("lamaSewa", inputLamaSewaEditKamar);
                            UpdateKamar.put("kamarTersedia", inputKamarTersediaEditKamar);
                            UpdateKamar.put("luasKamar", inputLuasEditKamar);
                            UpdateKamar.put("fasilitasKamar", inputFasilitasEditKamar);
                            UpdateKamar.put("gambarKamar", GambarKamarPath);

                            DataKamarKost.whereEqualTo("namaKamar", namaKamar).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful() && !task.getResult().isEmpty()){
                                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                        String DocumentID = documentSnapshot.getId();

                                        // EDIT NAMA KAMAR PEMBELI
                                        editNamaKamarTransaksi(namaKamar, inputNamaEditKamar);


                                        DataKamarKost.document(DocumentID).update(UpdateKamar).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                popupberhasil.setContentView(R.layout.popupeditkamarberhasil);
                                                popupberhasil.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                popupberhasil.show();

                                                final Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Intent keDetailKamarPemilik = new Intent(EditJenisKamarPemilik.this, DetailKamarPemilik.class);
                                                        keDetailKamarPemilik.putExtra("namaKamar", inputNamaEditKamar);
                                                        keDetailKamarPemilik.putExtra("namakost", namaKost);
//                                                startActivity(keDetailKamarPemilik);
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
                                                },3000);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK){
            if (data !=null){
                gambarKamarBaru = data.getData(); //Mendapatkan Path Aktual
                imageKamar.setImageURI(gambarKamarBaru); //Menampilkan Gambar
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