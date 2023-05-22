package model;

import com.google.firebase.Timestamp;
import java.sql.Time;
import java.util.List;

public class ListReviewKost {
    private String NamaReviewer, UlasanReviewer, FotoReviewer, BintangReviewer, namaPemilikKost, namaKost;

    public ListReviewKost(){

    }

    public ListReviewKost(String namaReviewer, String ulasanReviewer, String fotoReviewer, String bintangReviewer, String namaPemilikKost, String namaKost) {
        this.NamaReviewer = namaReviewer;
        this.UlasanReviewer = ulasanReviewer;
        this.FotoReviewer = fotoReviewer;
        this.BintangReviewer = bintangReviewer;
        this.namaPemilikKost = namaPemilikKost;
        this.namaKost = namaKost;
    }

    public String getNamaPemilikKost() {
        return namaPemilikKost;
    }

    public void setNamaPemilikKost(String namaPemilikKost) {
        this.namaPemilikKost = namaPemilikKost;
    }

    public String getNamaKost() {
        return namaKost;
    }

    public void setNamaKost(String namaKost) {
        this.namaKost = namaKost;
    }

    public String getNamaReviewer() {
        return NamaReviewer;
    }

    public void setNamaReviewer(String namaReviewer) {
        NamaReviewer = namaReviewer;
    }

    public String getUlasanReviewer() {
        return UlasanReviewer;
    }

    public void setUlasanReviewer(String ulasanReviewer) {
        UlasanReviewer = ulasanReviewer;
    }

    public String getFotoReviewer() {
        return FotoReviewer;
    }

    public void setFotoReviewer(String fotoReviewer) {
        FotoReviewer = fotoReviewer;
    }

    public String getBintangReviewer() {
        return BintangReviewer;
    }

    public void setBintangReviewer(String bintangReviewer) {
        BintangReviewer = bintangReviewer;
    }
}
