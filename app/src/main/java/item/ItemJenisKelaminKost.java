package item;

public class ItemJenisKelaminKost {
    private String jenisKelaminText;
    private int imageKelaminSpinner;

    public ItemJenisKelaminKost(String jenisKelaminText, int imageKelaminSpinner) {
        this.jenisKelaminText = jenisKelaminText;
        this.imageKelaminSpinner = imageKelaminSpinner;
    }

    public String getJenisKelaminText() {
        return jenisKelaminText;
    }

    public int getImageKelaminSpinner() {
        return imageKelaminSpinner;
    }
}
