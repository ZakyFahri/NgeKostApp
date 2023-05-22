package model;

import com.google.firebase.Timestamp;
import java.sql.Time;
import java.util.List;

import account.ngekostuser;

public class ListKost {
    private String namakost, latitude, longitude, rangeharga, alamat, jeniskelaminkost, nomortelpon, deksripsikost, gambarkost, pemilik, userID, search;
    private Timestamp timeAdded;

//    private static ListKost instance;

    public ListKost(){

    }

//    public static ListKost getInstance(){
//        if(instance == null){
//            instance = new ListKost();
//        }
//        return instance;
//    }

    public ListKost(String namakost, String latitude, String longitude, String rangeharga, String alamat, String jeniskelaminkost, String nomortelpon, String deksripsikost, String gambarkost, String pemilik, String userID, Timestamp timeAdded, String search) {
        this.namakost = namakost;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rangeharga = rangeharga;
        this.alamat = alamat;
        this.jeniskelaminkost = jeniskelaminkost;
        this.nomortelpon = nomortelpon;
        this.deksripsikost = deksripsikost;
        this.gambarkost = gambarkost;
        this.pemilik = pemilik;
        this.userID = userID;
        this.timeAdded = timeAdded;
        this.search = search;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getNamakost() {
        return namakost;
    }

    public void setNamakost(String namakost) {
        this.namakost = namakost;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getRangeharga() {
        return rangeharga;
    }

    public void setRangeharga(String rangeharga) {
        this.rangeharga = rangeharga;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getJeniskelaminkost() {
        return jeniskelaminkost;
    }

    public void setJeniskelaminkost(String jeniskelaminkost) {
        this.jeniskelaminkost = jeniskelaminkost;
    }

    public String getNomortelpon() {
        return nomortelpon;
    }

    public void setNomortelpon(String nomortelpon) {
        this.nomortelpon = nomortelpon;
    }

    public String getDeksripsikost() {
        return deksripsikost;
    }

    public void setDeksripsikost(String deksripsikost) {
        this.deksripsikost = deksripsikost;
    }

    public String getGambarkost() {
        return gambarkost;
    }

    public void setGambarkost(String gambarkost) {
        this.gambarkost = gambarkost;
    }

    public Timestamp getTimeAdded() {
        return timeAdded;
    }

    public void setTimeAdded(Timestamp timeAdded) {
        this.timeAdded = timeAdded;
    }

    public String getPemilik() {
        return pemilik;
    }

    public void setPemilik(String pemilik) {
        this.pemilik = pemilik;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
