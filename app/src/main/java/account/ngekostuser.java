package account;

import android.app.Application;

public class ngekostuser extends Application{
    private String userID, nama, alamat, jeniskelamin, email, fotoprofil, nomortelp;
//            private String password;

    private static ngekostuser instance;

    public static ngekostuser getInstance(){
        if(instance == null){
            instance = new ngekostuser();
        }
        return instance;
    }

    public String getNomortelp() {
        return nomortelp;
    }

    public void setNomortelp(String nomortelp) {
        this.nomortelp = nomortelp;
    }

    public String getFotoprofil() {
        return fotoprofil;
    }

    public void setFotoprofil(String fotoprofil) {
        this.fotoprofil = fotoprofil;
    }

    public ngekostuser(){

    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getJeniskelamin() {
        return jeniskelamin;
    }

    public void setJeniskelamin(String jeniskelamin) {
        this.jeniskelamin = jeniskelamin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
}
