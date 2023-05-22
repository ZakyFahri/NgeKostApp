package model;

import com.google.firebase.Timestamp;
import java.sql.Time;
import java.util.List;

import account.ngekostuser;

public class ListBookmark {
    private String namaKost, gambarKost, pemilikBookmark, jeniskost, rangehargakost, alamatkost, namaPemilikKost;

    public ListBookmark(){

    }

    public ListBookmark(String namaKost, String gambarKost, String pemilikBookmark, String jeniskost, String rangehargakost, String alamatkost, String namaPemilikKost) {
        this.namaKost = namaKost;
        this.gambarKost = gambarKost;
        this.pemilikBookmark = pemilikBookmark;
        this.jeniskost = jeniskost;
        this.rangehargakost = rangehargakost;
        this.alamatkost = alamatkost;
        this.namaPemilikKost = namaPemilikKost;
    }

    public String getNamaKost() {
        return namaKost;
    }

    public void setNamaKost(String namaKost) {
        this.namaKost = namaKost;
    }

    public String getGambarKost() {
        return gambarKost;
    }

    public void setGambarKost(String gambarKost) {
        this.gambarKost = gambarKost;
    }

    public String getPemilikBookmark() {
        return pemilikBookmark;
    }

    public void setPemilikBookmark(String pemilikBookmark) {
        this.pemilikBookmark = pemilikBookmark;
    }

    public String getJeniskost() {
        return jeniskost;
    }

    public void setJeniskost(String jeniskost) {
        this.jeniskost = jeniskost;
    }

    public String getRangehargakost() {
        return rangehargakost;
    }

    public void setRangehargakost(String rangehargakost) {
        this.rangehargakost = rangehargakost;
    }

    public String getAlamatkost() {
        return alamatkost;
    }

    public void setAlamatkost(String alamatkost) {
        this.alamatkost = alamatkost;
    }

    public String getNamaPemilikKost() {
        return namaPemilikKost;
    }

    public void setNamaPemilikKost(String namaPemilikKost) {
        this.namaPemilikKost = namaPemilikKost;
    }
}
