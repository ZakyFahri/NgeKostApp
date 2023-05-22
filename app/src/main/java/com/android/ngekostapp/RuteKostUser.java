package com.android.ngekostapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class RuteKostUser extends AppCompatActivity {
    WebView mapsRute;

    Double latitudePengguna, longitudePengguna, latitudemap, longitudemap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_rute_kost_user);

        mapsRute = (WebView) findViewById(R.id.WebRute);
        mapsRute.getSettings().setJavaScriptEnabled(true);

        WebSettings settings = mapsRute.getSettings();
        settings.setDomStorageEnabled(true);

        Bundle extra = getIntent().getExtras();
        if(extra != null) {
            latitudePengguna = extra.getDouble("latitudeLokasiPengguna");
            longitudePengguna = extra.getDouble("longitudeLokasiPengguna");
            latitudemap = extra.getDouble("latitudeKost");
            longitudemap = extra.getDouble("longitudeKost");
        }

        mapsRute.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (URLUtil.isNetworkUrl(url)){
                    return false;
                }

                if (appInstalledOrNot(url)){
                    Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent1);
                }
                return true;
            }
        });

        mapsRute.loadUrl("https://www.google.com/maps?"+"saddr="+latitudePengguna+","+longitudePengguna+"&daddr="+latitudemap+","+longitudemap);
        Toast.makeText(this, "Mempersiapkan Rute, Mohon Tunggu....", Toast.LENGTH_LONG).show();
    }

    boolean appInstalledOrNot(String url){
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(url, PackageManager.GET_ACTIVITIES);
            return true;
        }catch (PackageManager.NameNotFoundException e){

        }
        return false;
    }
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        finish();
//    }
}