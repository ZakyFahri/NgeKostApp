package item;

public class ItemJenisLogin {
    private String jenisLoginText;
    private int imageSpinner;

    public ItemJenisLogin(String jenisLoginText, int imageSpinner) {
        this.jenisLoginText = jenisLoginText;
        this.imageSpinner = imageSpinner;
    }

    public String getJenisLoginText() {
        return jenisLoginText;
    }

    public int getImageSpinner() {
        return imageSpinner;
    }
}
