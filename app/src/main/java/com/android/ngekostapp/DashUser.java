package com.android.ngekostapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import account.ngekostuser;
import ui.ListJenisKamarPemilikAdapter;

public class DashUser extends AppCompatActivity implements View.OnClickListener {
    private CardView cariKost, PetaKost, InfoApp, Logout;
    private TextView namapengguna;
    private ImageView fotoprofil;
    private ImageButton editprofil;
    private ImageButton cartUser;
    private ImageButton kebookmark;

    //Nama dan UserID dan Gambar Profil
    private String userID;
    private String nama;
    private String fotoprofilpath;
    private String alamatUser;

    //Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    //FireStore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private CollectionReference collectionReference = db.collection("Pengguna");

    Uri fotoprofiluri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_dash_user);

        cariKost = findViewById(R.id.cardViewKost);
        PetaKost = findViewById(R.id.cardviewMap);
        InfoApp = findViewById(R.id.cardviewInfo);
        Logout = findViewById(R.id.cardviewLogout);
        fotoprofil = findViewById(R.id.photouser);
        editprofil = (ImageButton) findViewById(R.id.editprofileDashUser);
        cartUser = (ImageButton) findViewById(R.id.CartUser);
        kebookmark = (ImageButton) findViewById(R.id.BookmarkDashUser);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = firebaseAuth.getInstance();

        namapengguna = findViewById(R.id.namaUser);

        cariKost.setOnClickListener((View.OnClickListener) this);
        PetaKost.setOnClickListener((View.OnClickListener) this);
        InfoApp.setOnClickListener((View.OnClickListener) this);
        Logout.setOnClickListener((View.OnClickListener) this);

        if (ngekostuser.getInstance()!=null){
            nama = ngekostuser.getInstance().getNama();
            userID = ngekostuser.getInstance().getUserID();
            alamatUser = ngekostuser.getInstance().getAlamat();
            fotoprofilpath = ngekostuser.getInstance().getFotoprofil();
            fotoprofiluri = Uri.parse(fotoprofilpath);
            Picasso.get().load(fotoprofiluri).into(fotoprofil);
            namapengguna.setText(nama);
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

        editprofil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent keeditprofil = new Intent(DashUser.this, EditProfiUser.class);
                startActivity(keeditprofil);
            }
        });

        cartUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent keTransaksiUser = new Intent(DashUser.this, ListTransaksiKostUser.class);
                keTransaksiUser.putExtra("namaUser", nama);
                keTransaksiUser.putExtra("alamatUser", alamatUser);
                startActivity(keTransaksiUser);
            }
        });

        kebookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent keBookmarkUser = new Intent(DashUser.this, ListBookmarkUser.class);
                keBookmarkUser.putExtra("namaUser", nama);
                startActivity(keBookmarkUser);
            }
        });
    }

    @Override
    public void onBackPressed(){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()) {
            case R.id.cardViewKost:
                i = new Intent(this, CariKostUser.class);
                startActivity(i);
                break;
            case R.id.cardviewMap:
                i = new Intent(this, PetaKostUser.class);
                startActivity(i);
                break;
            case R.id.cardviewInfo:
                i = new Intent(this, TentangAplikasiUser.class);
                startActivity(i);
                break;
//            case R.id.editprofileDashUser:
//                i = new Intent(this, EditProfiUser.class);
//                startActivity(i);
//                break;
            case R.id.cardviewLogout:
                if (user != null && firebaseAuth != null) {
                    showWarningDialog();
//                    firebaseAuth.signOut();
//                    startActivity(new Intent(DashUser.this, MainActivity.class));
                }
                break;
        }
    }

    //Dialog Alert
    private void showWarningDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(DashUser.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(DashUser.this).inflate(
                R.layout.dialogcontainer,(ConstraintLayout)findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        ((TextView)view.findViewById(R.id.textTitle)).setText("Konfirmasi Logout");
        ((TextView)view.findViewById(R.id.textMessage)).setText("Apakah Anda yakin ?");
        ((Button)view.findViewById(R.id.buttonYes)).setText("Logout");
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

                firebaseAuth.signOut();
                startActivity(new Intent(DashUser.this, MainActivity.class));
            }
        });

        if(alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
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
        user =firebaseAuth.getCurrentUser();
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