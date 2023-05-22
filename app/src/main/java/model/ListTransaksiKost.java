package model;

import com.google.firebase.Timestamp;
import java.sql.Time;
import java.util.List;

public class ListTransaksiKost {
    private String NamaPembeli, AlamatPembeli, JenisKelaminPembeli, NamaKost, NamaKamar, LamaSewa, Gambarkost, NamaPemilik, TotalHarga, StatusTransaksi, waktuDitambahkan;
    public ListTransaksiKost(){

    }

    public ListTransaksiKost(String namaPembeli, String alamatPembeli, String jenisKelaminPembeli, String namaKost, String namaKamar, String namaPemilik, String totalHarga, String statusTransaksi, String waktuDitambahkan, String LamaSewa, String Gambarkost) {
        this.NamaPembeli = namaPembeli;
        this.AlamatPembeli = alamatPembeli;
        this.JenisKelaminPembeli = jenisKelaminPembeli;
        this.NamaKost = namaKost;
        this.NamaKamar = namaKamar;
        this.NamaPemilik = namaPemilik;
        this.TotalHarga = totalHarga;
        this.StatusTransaksi = statusTransaksi;
        this.waktuDitambahkan = waktuDitambahkan;
        this.LamaSewa = LamaSewa;
        this.Gambarkost = Gambarkost;
    }

    public String getGambarkost() {
        return Gambarkost;
    }

    public void setGambarkost(String gambarkost) {
        Gambarkost = gambarkost;
    }

    public String getNamaPembeli() {
        return NamaPembeli;
    }

    public void setNamaPembeli(String namaPembeli) {
        NamaPembeli = namaPembeli;
    }

    public String getLamaSewa() {
        return LamaSewa;
    }

    public void setLamaSewa(String lamaSewa) {
        LamaSewa = lamaSewa;
    }

    public String getAlamatPembeli() {
        return AlamatPembeli;
    }

    public void setAlamatPembeli(String alamatPembeli) {
        AlamatPembeli = alamatPembeli;
    }

    public String getJenisKelaminPembeli() {
        return JenisKelaminPembeli;
    }

    public void setJenisKelaminPembeli(String jenisKelaminPembeli) {
        JenisKelaminPembeli = jenisKelaminPembeli;
    }

    public String getNamaKost() {
        return NamaKost;
    }

    public void setNamaKost(String namaKost) {
        NamaKost = namaKost;
    }

    public String getNamaKamar() {
        return NamaKamar;
    }

    public void setNamaKamar(String namaKamar) {
        NamaKamar = namaKamar;
    }

    public String getNamaPemilik() {
        return NamaPemilik;
    }

    public void setNamaPemilik(String namaPemilik) {
        NamaPemilik = namaPemilik;
    }

    public String getTotalHarga() {
        return TotalHarga;
    }

    public void setTotalHarga(String totalHarga) {
        TotalHarga = totalHarga;
    }

    public String getStatusTransaksi() {
        return StatusTransaksi;
    }

    public void setStatusTransaksi(String statusTransaksi) {
        StatusTransaksi = statusTransaksi;
    }

    public String getWaktuDitambahkan() {
        return waktuDitambahkan;
    }

    public void setWaktuDitambahkan(String waktuDitambahkan) {
        this.waktuDitambahkan = waktuDitambahkan;
    }
}
