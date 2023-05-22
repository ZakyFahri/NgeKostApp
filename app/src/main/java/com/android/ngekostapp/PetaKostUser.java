package com.android.ngekostapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
//import org.osmdroid.views.MapView;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.text.CollationElementIterator;
import java.util.ArrayList;
import java.util.Map;

import model.ListKost;

public class PetaKostUser extends AppCompatActivity {
    //Map
    MapController mapController;
     org.osmdroid.views.MapView mapApp;

     ImageButton kelokasiku;

    //Latitude & Longitude
    Double latitude = 0.0;
    Double longitude = 0.0;

    Double latitudePengguna = 0.0;
    Double longitudePengguna = 0.0;

    Double latitudemap;
    Double longitudemap;

    String daridetail = "Salah";

    //Latitude & Longitude String dari detail kost
    String latitudeKost;
    String longitudeKost;

    boolean sudahdiklik = false;

    //Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    //List Kost
    private ArrayList<ListKost>daftarKost;

    //Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private CollectionReference DataKost = db.collection("Data Kost");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_peta_kost_user);

        kelokasiku = findViewById(R.id.LokasiGet);
        kelokasiku.bringToFront();

        AccessPermission();

        //                 --------SET MAPS--------
        mapApp = (MapView) findViewById(R.id.petakost);

        mapApp.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        Configuration.getInstance().setUserAgentValue(getPackageName());
        mapApp.setBuiltInZoomControls(true);
        mapApp.setMultiTouchControls(true);
        mapController = (MapController) mapApp.getController();
        mapController.setZoom(17);

        //Zoom Sekitaran UNM
        GeoPoint pointcenter = new GeoPoint( -5.186175951262937, 119.42952583088918);
        mapController.setCenter(pointcenter);

        //Bundle Ambil Data Map dari Detail Kost
        Bundle extra = getIntent().getExtras();
        if(extra != null){
            latitudeKost = extra.getString("latitudekost");
            longitudeKost = extra.getString("longitudekost");
            daridetail = extra.getString("dariDetailKost");
        }

        //Ambil Lokasi User
        final MyLocationNewOverlay myLocationNewOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), mapApp);
        myLocationNewOverlay.enableMyLocation();
        mapApp.getOverlays().add(myLocationNewOverlay);

        if (daridetail.equals("Benar")){
            latitudemap = Double.parseDouble(latitudeKost);
            longitudemap = Double.parseDouble(longitudeKost);
            GeoPoint pointcenterKost = new GeoPoint(latitudemap, longitudemap);
            mapController.setCenter(pointcenterKost);
            mapController.setZoom(21);
        }else if (daridetail.equals("Salah")){
            Toast.makeText(PetaKostUser.this, "Mengambil Lokasi, Mohon Tunggu...", Toast.LENGTH_SHORT).show();
            final LoadingAlert loadingAlert = new LoadingAlert(PetaKostUser.this);

            loadingAlert.StartAlertDialog();
            //        Untuk Mengatur Start Peta Ke Lokasi Kita
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadingAlert.closeAlertDialog();
                }
            },5000);
            myLocationNewOverlay.runOnFirstFix(new Runnable() {
                @Override
                public void run() {
                    if (myLocationNewOverlay.getMyLocation()!=null){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mapApp.getController().animateTo(myLocationNewOverlay.getMyLocation());
                                latitudePengguna=myLocationNewOverlay.getMyLocation().getLatitude();
                                longitudePengguna = myLocationNewOverlay.getMyLocation().getLongitude();
                            }
                        });
                    }
                }
            });
        }


        // --------Tambah Manual Marker Map----------
//        //Buat Sebuah Marker
//        GeoPoint markerkost = new GeoPoint(-5.184795131661884, 119.43270680999775);
//        Marker mymarkerpoint = new Marker(mapApp);
//        mymarkerpoint.setPosition(markerkost);
//        mymarkerpoint.setTitle("Pondok Cora 1");
//        mymarkerpoint.setIcon(this.getDrawable(R.drawable.pin));
//        mymarkerpoint.setAnchor(Marker.ANCHOR_LEFT,Marker.ANCHOR_RIGHT);
//        mapApp.getOverlays().add(mymarkerpoint);
//
//        //Buat Sebuah Marker
//        GeoPoint markerkost2 = new GeoPoint(-5.184867653418628, 119.43140134956634);
//        Marker mymarkerpoint2 = new Marker(mapApp);
//        mymarkerpoint2.setPosition(markerkost2);
//        mymarkerpoint2.setTitle("Kost Putri Alamanda Tamalate");
//        mymarkerpoint2.setIcon(this.getDrawable(R.drawable.pin));
//        mymarkerpoint2.setAnchor(Marker.ANCHOR_LEFT,Marker.ANCHOR_RIGHT);
//        mapApp.getOverlays().add(mymarkerpoint2);
        // --------END Tambah Manual Marker Map----------

        MapEventsReceiver mapEventsReceiver = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                return false;
            }
            @Override
            public boolean longPressHelper(GeoPoint p) {
                Toast.makeText(PetaKostUser.this, p.getLatitude()+"-"+p.getLongitude(), Toast.LENGTH_SHORT).show();
                return false;
            }
        };

        MapEventsOverlay eventsOverlay = new MapEventsOverlay(getApplicationContext(), mapEventsReceiver);
        mapApp.getOverlays().add(eventsOverlay);

        //
        //                 --------END MAPS--------

        // List Kost
        daftarKost = new ArrayList<>();
        loadDataKostMap();

        kelokasiku.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapApp.getController().animateTo(myLocationNewOverlay.getMyLocation());
                latitudePengguna=myLocationNewOverlay.getMyLocation().getLatitude();
                longitudePengguna = myLocationNewOverlay.getMyLocation().getLongitude();
            }
        });

    }

    void AccessPermission(){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.ICE_CREAM_SANDWICH && checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
        }
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.ICE_CREAM_SANDWICH && checkSelfPermission(android.Manifest.permission.INTERNET)!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.INTERNET}, 1002);
        }
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.ICE_CREAM_SANDWICH && checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE)!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, 1003);
        }
//        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.ICE_CREAM_SANDWICH && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
//            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1004);
//        }if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.ICE_CREAM_SANDWICH && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1005);
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1001:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Akses Lokasi Diterima", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, "Akses Lokasi Ditolak", Toast.LENGTH_SHORT).show();
                }
                break;
            case 1002:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Akses Internet Diterima", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, "Akses Internet Ditolak", Toast.LENGTH_SHORT).show();
                }
                break;
            case 1003:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Akses Network State Diterima", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, "Akses Network State Ditolak", Toast.LENGTH_SHORT).show();
                }
                break;
//            case 1004:
//                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                    Toast.makeText(this, "Akses Penyimpanan Eksternal Diterima", Toast.LENGTH_SHORT).show();
//                }else {
//                    Toast.makeText(this, "Akses Penyimpanan Eksternal Ditolak", Toast.LENGTH_SHORT).show();
//                }
//                break;
//            case 1005:
//                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                    Toast.makeText(this, "Akses Penyimpanan Eksternal Diterima", Toast.LENGTH_SHORT).show();
//                }else {
//                    Toast.makeText(this, "Akses Penyimpanan Eksternal Ditolak", Toast.LENGTH_SHORT).show();
//                }
//                break;
        }
    }

    private void loadDataKostMap(){
        DataKost.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()){
                    for (QueryDocumentSnapshot listkost : queryDocumentSnapshots){
                        ListKost daftarlistkost = listkost.toObject(ListKost.class);
                        String latitude = daftarlistkost.getLatitude();
                        String longitude = daftarlistkost.getLongitude();
                        String gambar = daftarlistkost.getGambarkost();
                        String pemilik = daftarlistkost.getPemilik();
                        String notelp = daftarlistkost.getNomortelpon();
                        String namakost = daftarlistkost.getNamakost();
                        String alamat = daftarlistkost.getAlamat();

                        Double latitudeKost = Double.parseDouble(latitude);
                        Double longitudeKost = Double.parseDouble(longitude);

                        GeoPoint markerKost3 = new GeoPoint(latitudeKost, longitudeKost);
                        Marker mymarkerpoint3 = new Marker(mapApp);
                        mymarkerpoint3.setPosition(markerKost3);
//                        mymarkerpoint3.setTitle(daftarlistkost.getNamakost());
                        mymarkerpoint3.setIcon(PetaKostUser.this.getDrawable(R.drawable.location_ireng));
//                        mymarkerpoint3.setAnchor(Marker.ANCHOR_LEFT,Marker.ANCHOR_RIGHT);
                        mapApp.getOverlays().add(mymarkerpoint3);

                        mymarkerpoint3.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker marker, MapView mapView) {
                                mymarkerpoint3.setIcon(PetaKostUser.this.getDrawable(R.drawable.location_merah));
                                showDialog(gambar, pemilik, notelp, namakost, alamat, latitudeKost, longitudeKost);
                                sudahdiklik = true;
                                return false;
                            }
                        });
                    }
                }
            }
        });
    }

    private void showDialog(String gambar, String pemilik, String NoTelp, String namaKost, String Alamat, Double latitudemap, Double longitudemap){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottommaplayout);

        Uri gambarKost;
        gambarKost = Uri.parse(gambar);
        ImageView gambarkost = dialog.findViewById(R.id.gambarkost);
        Picasso.get().load(gambarKost).into(gambarkost);

        TextView namakost = dialog.findViewById(R.id.namakost);
        TextView namapemilik = dialog.findViewById(R.id.namapemilikkost);
        TextView notelp = dialog.findViewById(R.id.notelppemilikkost);
        TextView alamat = dialog.findViewById(R.id.alamatkost);
        Button keDetailKost = dialog.findViewById(R.id.buttonKeDash);
        Button keRute = dialog.findViewById(R.id.buttonKeRute);

        namakost.setText(namaKost);
        namapemilik.setText(pemilik);
        notelp.setText(NoTelp);
        alamat.setText(Alamat);

        keDetailKost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent keDetailKostUser = new Intent(PetaKostUser.this, DetailKostUser.class);
                keDetailKostUser.putExtra("namakost", namaKost);
                keDetailKostUser.putExtra("daripeta", "Yes");
                startActivity(keDetailKostUser);
            }
        });

        keRute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent keRuteKost = new Intent(PetaKostUser.this, RuteKostUser.class);
                keRuteKost.putExtra("latitudeLokasiPengguna", latitudePengguna);
                keRuteKost.putExtra("longitudeLokasiPengguna", longitudePengguna);
                keRuteKost.putExtra("latitudeKost", latitudemap);
                keRuteKost.putExtra("longitudeKost", longitudemap);
                startActivity(keRuteKost);
            }
        });

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
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