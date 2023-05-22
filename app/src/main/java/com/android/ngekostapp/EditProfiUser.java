package com.android.ngekostapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import account.ngekostuser;

public class EditProfiUser extends AppCompatActivity {
    //Widget
    private static final int GALLERY_CODE = 1;
    private EditText namaEdit, alamatEdit, nomortelpEdit;
    private ImageView fotoprofiledit;
    private Button choosephotos, editData;
    private ImageButton tombolKembali;
    private ProgressBar loadinggambar;

    //Dialog
    Dialog popupberhasil;
    Dialog popupgagal;

    //Mendapatkan UserID, Nama, Alamat, Path Profil dan Nomor Telpon
    private String userID;
    private String nama;
    private String alamat;
    private String notelp;
    private String fotoprofilpath;

    //Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    //Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private CollectionReference collectionReference = db.collection("Pengguna");

    private Uri fotoprofiluri;
    private Uri fotoprofilLama;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_edit_profi_user);

        namaEdit = findViewById(R.id.EditNama);
        alamatEdit = findViewById(R.id.EditAlamat);
        nomortelpEdit = findViewById(R.id.EditNomor);
        fotoprofiledit = findViewById(R.id.photouserEdit);
        choosephotos = findViewById(R.id.buttonambilfotoedit);
        editData = findViewById(R.id.buttoneditprofil);
//        loadinggambar = (ProgressBar) findViewById(R.id.loadgambar);

        tombolKembali = findViewById(R.id.tombolkembali);

        popupberhasil = new Dialog(this);
        popupgagal = new Dialog(this);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = firebaseAuth.getInstance();

        if (ngekostuser.getInstance()!=null){
            userID = ngekostuser.getInstance().getUserID();
            nama = ngekostuser.getInstance().getNama();
            alamat = ngekostuser.getInstance().getAlamat();
            notelp = ngekostuser.getInstance().getNomortelp();

            //Load Gambar
            fotoprofilpath = ngekostuser.getInstance().getFotoprofil();
            fotoprofilLama = Uri.parse(fotoprofilpath);
            Picasso.get().load(fotoprofilLama).into(fotoprofiledit);

            //Set Text pada Widget
            namaEdit.setText(nama);
            alamatEdit.setText(alamat);
            nomortelpEdit.setText(notelp);
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

        choosephotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Mendapat Gambar
                Intent galleryIntent =new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_CODE);
            }
        });

        editData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editProfil();
            }
        });

        tombolKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    //                    -------------------SECTION UBAH DATA PENGGUNA PADA COLLECTION LAIN-------------------
    private void editNamaPenggunaBookmark(String nama, String inputEditNama){
        db.collection("Data Bookmark").whereEqualTo("pemilikBookmark", nama).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && !task.getResult().isEmpty()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        Map<String, Object> gantiNamaPenggunaBookmark = new HashMap<>();
                        gantiNamaPenggunaBookmark.put("pemilikBookmark", inputEditNama);
                        String DocumentID = document.getId();
                        db.collection("Data Bookmark").document(DocumentID).update(gantiNamaPenggunaBookmark);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void editNamaPembeliTransaksi(String nama, String inputEditNama){
        db.collection("Data Transaksi").whereEqualTo("namaPembeli", nama).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && !task.getResult().isEmpty()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        Map<String, Object> gantiNamaPembeliTransaksi = new HashMap<>();
                        gantiNamaPembeliTransaksi.put("namaPembeli", inputEditNama);
                        String DocumentID = document.getId();
                        db.collection("Data Transaksi").document(DocumentID).update(gantiNamaPembeliTransaksi);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void editAlamatPembeliTransaksi(String alamat, String inputEditAlamat){
        db.collection("Data Transaksi").whereEqualTo("alamatPembeli", alamat).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && !task.getResult().isEmpty()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        Map<String, Object> gantiAlamatPembeliTransaksi = new HashMap<>();
                        gantiAlamatPembeliTransaksi.put("alamatPembeli", inputEditAlamat);
                        String DocumentID = document.getId();
                        db.collection("Data Transaksi").document(DocumentID).update(gantiAlamatPembeliTransaksi);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void editNamaReviewer(String nama, String inputEditNama){
        db.collection("Data Reviewer").whereEqualTo("namaReviewer", nama).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && !task.getResult().isEmpty()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        Map<String, Object> gantiNamaReviewer = new HashMap<>();
                        gantiNamaReviewer.put("namaReviewer", inputEditNama);
                        String DocumentID = document.getId();
                        db.collection("Data Reviewer").document(DocumentID).update(gantiNamaReviewer);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }



    //                    ------------------- END SECTION UBAH DATA PENGGUNA PADA COLLECTION LAIN-------------------


    private void editProfil(){
        final String inputEditNama = namaEdit.getText().toString().trim();
        final String inputEditAlamat = alamatEdit.getText().toString().trim();
        final String inputNomorTelpEdit = nomortelpEdit.getText().toString().trim();
        user = firebaseAuth.getCurrentUser();
        assert  user !=null;
        final String userID = user.getUid();

        if(!TextUtils.isEmpty(inputEditNama) && !TextUtils.isEmpty(inputEditAlamat) && !TextUtils.isEmpty(inputNomorTelpEdit) && fotoprofiluri!=null){
            //Simpan Gambar Profil
            final StorageReference filepath = storageReference.child("fotouser").child("fotouserupdate" + inputEditNama + Timestamp.now().getNanoseconds());
            filepath.putFile(fotoprofiluri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String fotoprofilpath = uri.toString();

                            ngekostuser.getInstance();
                            ngekostuser.getInstance().setNama(inputEditNama);
                            ngekostuser.getInstance().setNomortelp(inputNomorTelpEdit);
                            ngekostuser.getInstance().setAlamat(inputEditAlamat);
                            ngekostuser.getInstance().setFotoprofil(fotoprofilpath);

                            Map<String, Object> UpdateData = new HashMap<>();
                            UpdateData.put("nama", inputEditNama);
                            UpdateData.put("alamat", inputEditAlamat);
                            UpdateData.put("notelp", inputNomorTelpEdit);
                            UpdateData.put("fotoprofil", fotoprofilpath);

                            db.collection("Pengguna").whereEqualTo("userID", userID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful() && !task.getResult().isEmpty()){
                                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                        String documentID = documentSnapshot.getId();

                                        //Edit Data Nama Untuk Collection Lainnya
                                        editNamaPenggunaBookmark(nama, inputEditNama);
                                        editNamaPembeliTransaksi(nama, inputEditNama);
                                        editAlamatPembeliTransaksi(alamat, inputEditAlamat);
                                        editNamaReviewer(nama, inputEditNama);

                                        db.collection("Pengguna").document(documentID).update(UpdateData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                popupberhasil.setContentView(R.layout.popupeditprofilberhasil);
                                                popupberhasil.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                popupberhasil.show();

                                                final Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        popupberhasil.dismiss();
                                                        Intent kedash = new Intent(EditProfiUser.this, DashUser.class);
//                                                      startActivity(kedash);
                                                        finish();
                                                    }
                                                }, 2000);

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                popupgagal.setContentView(R.layout.popupeditprofilgagal);
                                                popupgagal.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                popupgagal.show();

                                                final Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        popupgagal.dismiss();
                                                    }
                                                }, 2000);
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    });
                }
            });
        }else if(!TextUtils.isEmpty(inputEditNama) && !TextUtils.isEmpty(inputEditAlamat) && !TextUtils.isEmpty(inputNomorTelpEdit) && fotoprofiluri == null){
            ngekostuser.getInstance();
            ngekostuser.getInstance().setNama(inputEditNama);
            ngekostuser.getInstance().setNomortelp(inputNomorTelpEdit);
            ngekostuser.getInstance().setAlamat(inputEditAlamat);
            fotoprofilpath = ngekostuser.getInstance().getFotoprofil();

            fotoprofiledit.setImageURI(Uri.parse(fotoprofilpath));
            ngekostuser.getInstance().setFotoprofil(fotoprofilpath);
            Picasso.get().load(Uri.parse(fotoprofilpath)).into(fotoprofiledit);

            Map<String, Object> UpdateData = new HashMap<>();
            UpdateData.put("nama", inputEditNama);
            UpdateData.put("alamat", inputEditAlamat);
            UpdateData.put("notelp", inputNomorTelpEdit);
            UpdateData.put("fotoprofil", fotoprofilpath);

            db.collection("Pengguna").whereEqualTo("userID", userID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful() && !task.getResult().isEmpty()){
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        String documentID = documentSnapshot.getId();

                        //Edit Data Nama Untuk Collection Lainnya
                        editNamaPenggunaBookmark(nama, inputEditNama);
                        editNamaPembeliTransaksi(nama, inputEditNama);
                        editAlamatPembeliTransaksi(alamat, inputEditAlamat);
                        editNamaReviewer(nama, inputEditNama);


                        db.collection("Pengguna").document(documentID).update(UpdateData).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                popupberhasil.setContentView(R.layout.popupeditprofilberhasil);
                                popupberhasil.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                popupberhasil.show();

                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        popupberhasil.dismiss();
                                        Intent kedash = new Intent(EditProfiUser.this, DashUser.class);
//                                      startActivity(kedash);
                                        finish();
                                    }
                                }, 2000);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                popupgagal.setContentView(R.layout.popupeditprofilgagal);
                                popupgagal.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                popupgagal.show();

                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        popupgagal.dismiss();
                                    }
                                }, 2000);
                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK){
            if (data !=null){
                fotoprofiluri = data.getData(); //Mendapatkan Path Aktual
                fotoprofiledit.setImageURI(fotoprofiluri); //Menampilkan Gambar
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