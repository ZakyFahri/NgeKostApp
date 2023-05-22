package com.android.ngekostapp;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Dialog;
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

public class DashPemilik extends AppCompatActivity implements View.OnClickListener{
    private CardView AddKost, ListKost, TransaksiKost, Logoutpemilik;
    private TextView namapemilik;
    private ImageView photopemilik;
    private ImageButton editprofil;

    //Nama, UserID dan Foto Profil
    private String userID;
    private String nama;
    private String fotoprofilpath;

    //Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser userpemilik;

    //FireStore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private CollectionReference collectionReference = db.collection("Pemilik");

    Uri fotoprofiluri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_dash_pemilik);

        AddKost = findViewById(R.id.cardViewAddKost);
        ListKost = findViewById(R.id.cardviewListKost);
        TransaksiKost = findViewById(R.id.cardviewTransaksiKost);
        Logoutpemilik = findViewById(R.id.cardviewLogoutPemilik);
        photopemilik = findViewById(R.id.photopemilik);
        editprofil = (ImageButton) findViewById(R.id.editprofileDashPemilik);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = firebaseAuth.getInstance();

        namapemilik = findViewById(R.id.namaPemilik);

        AddKost.setOnClickListener((View.OnClickListener) this);
        ListKost.setOnClickListener((View.OnClickListener) this);
        TransaksiKost.setOnClickListener((View.OnClickListener) this);
        Logoutpemilik.setOnClickListener((View.OnClickListener) this);

        if (ngekostuser.getInstance()!=null){
            nama = ngekostuser.getInstance().getNama();
            userID = ngekostuser.getInstance().getUserID();
            fotoprofilpath = ngekostuser.getInstance().getFotoprofil();

            fotoprofiluri = Uri.parse(fotoprofilpath);
            Picasso.get().load(fotoprofiluri).into(photopemilik);
            namapemilik.setText(nama);
        }

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                userpemilik = firebaseAuth.getCurrentUser();
                if (userpemilik!=null){

                }else{

                }
            }
        };

        editprofil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent keeditprofilpemilik = new Intent(DashPemilik.this, EditProfilPemilik.class);
                startActivity(keeditprofilpemilik);
            }
        });
    }

    private void showWarningDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(DashPemilik.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(DashPemilik.this).inflate(
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
                startActivity(new Intent(DashPemilik.this, MainActivity.class));
            }
        });

        if(alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
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
            case R.id.cardViewAddKost:
                i = new Intent(this, TambahKostPemilik.class);
                startActivity(i);
                break;
            case R.id.cardviewListKost:
                i = new Intent(this, ListKostPemilik.class);
                startActivity(i);
                break;
            case R.id.cardviewTransaksiKost:
                i = new Intent(this, ListTransaksiKostPemilik.class);
                i.putExtra("namaPemilik", nama);
                startActivity(i);
                break;
            case R.id.cardviewLogoutPemilik:
                if (userpemilik != null && firebaseAuth != null) {
                    showWarningDialog();
                }
                break;
        }
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
        userpemilik =firebaseAuth.getCurrentUser();
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