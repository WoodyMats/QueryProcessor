/**
 * Κλάση η οποία υλοποιεί τα αποτελέσματα τα οποία θα επιστραφούν στη σελίδα.
 * @author Matskidis Ioannis
 * @author Moutafidis Dimitrios
 */
public class Result {

    public Result () {}

    private String url;
    private double rank;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public double getRank() {
        return rank;
    }

    public void setRank(double rank) {
        this.rank = rank;
    }

}
