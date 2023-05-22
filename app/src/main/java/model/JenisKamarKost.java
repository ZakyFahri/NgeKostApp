package model;

import com.google.firebase.Timestamp;
import java.sql.Time;
import java.util.List;

import account.ngekostuser;

public class JenisKamarKost {
    private String Namakost, NamaKamar, Harga, LamaSewa, KamarTersedia, LuasKamar, FasilitasKamar, GambarKamar, namaPemilik, hargaKonversi;
    private Timestamp timeadded;

    public JenisKamarKost(){

    }


    public JenisKamarKost(String Namakost, String namaKamar, String harga, String lamaSewa, String kamarTersedia, String luasKamar, String fasilitasKamar, String gambarKamar, Timestamp timeadded, String namaPemilik, String hargaKonversi) {
        this.Namakost = Namakost;
        this.NamaKamar = namaKamar;
        this.Harga = harga;
        this.LamaSewa = lamaSewa;
        this.KamarTersedia = kamarTersedia;
        this.LuasKamar = luasKamar;
        this.FasilitasKamar = fasilitasKamar;
        this.GambarKamar = gambarKamar;
        this.timeadded = timeadded;
        this.namaPemilik = namaPemilik;
        this.hargaKonversi = hargaKonversi;
    }

    public String getHargaKonversi() {
        return hargaKonversi;
    }

    public void setHargaKonversi(String hargaKonversi) {
        this.hargaKonversi = hargaKonversi;
    }

    public String getNamaPemilik() {
        return namaPemilik;
    }

    public void setNamaPemilik(String namaPemilik) {
        this.namaPemilik = namaPemilik;
    }

    public String getNamaKamar() {
        return NamaKamar;
    }

    public String getNamakost() {
        return Namakost;
    }

    public void setNamakost(String namakost) {
        Namakost = namakost;
    }

    public void setNamaKamar(String namaKamar) {
        NamaKamar = namaKamar;
    }

    public String getHarga() {
        return Harga;
    }

    public void setHarga(String harga) {
        Harga = harga;
    }

    public String getLamaSewa() {
        return LamaSewa;
    }

    public void setLamaSewa(String lamaSewa) {
        LamaSewa = lamaSewa;
    }

    public String getKamarTersedia() {
        return KamarTersedia;
    }

    public void setKamarTersedia(String kamarTersedia) {
        KamarTersedia = kamarTersedia;
    }

    public String getLuasKamar() {
        return LuasKamar;
    }

    public void setLuasKamar(String luasKamar) {
        LuasKamar = luasKamar;
    }

    public String getFasilitasKamar() {
        return FasilitasKamar;
    }

    public void setFasilitasKamar(String fasilitasKamar) {
        FasilitasKamar = fasilitasKamar;
    }

    public String getGambarKamar() {
        return GambarKamar;
    }

    public void setGambarKamar(String gambarKamar) {
        GambarKamar = gambarKamar;
    }

    public Timestamp getTimeadded() {
        return timeadded;
    }

    public void setTimeadded(Timestamp timeadded) {
        this.timeadded = timeadded;
    }
}
