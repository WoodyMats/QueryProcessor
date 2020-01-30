/**
 * Κλάση η οποία υλοποιεί τις εγγραφές που υπάρχουν στη βάση.
 * @author Matskidis Ioannis
 * @author Moutafidis Dimitrios
 */

class TFD {

    String textTerm, documentId;

    int termFrequency;

    public String getTextTerm() {
        return textTerm;
    }

    public String getDocumentId() {
        return documentId;
    }

    public int getTermFrequency() {
        return termFrequency;
    }

    public void setTermFrequency(int termFrequency) {
        this.termFrequency = termFrequency;
    }

}
