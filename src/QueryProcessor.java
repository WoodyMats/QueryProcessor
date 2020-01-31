import java.util.HashSet;


/**
 * Η κλάση αυτή επεξεργάζεται το ερώτημα του χρήστη και κάνοντας επιστρέφει τα σκορ
 * για το ερώτημα με βάση τις εγγραφές που υπάρχουν στη βάση και τις
 * οποίες κάναμε crawling.
 * @author Matskidis Ioannis
 * @author Moutafidis Dimitrios
 */
public class QueryProcessor {
    double [][] tfIdf;//TFIDF πίνακας όπου περιέχει τις αντίστοιχες εγγραφές TF*IDF.
    double [][] tf;//TF πίνακας.
    double [] idf,cos,termTfIdf;//Πίνακες με τις τιμές IDF,Cosine Similarity και TF*IDF για τους όρους του χρήστη αντίστοιχα.
    String [] termToSearch;//Πίνακας όπου θα περιέχει όλες τις μοναδικές λέξεις του ερωτήματος του χρήστη.
    HashSet<String> docSet;//HashSet περιέχει όλα τα λινκ.


    /**
     * Μέθοδος που επιστρέφει τα λινκ τα οποία υπάρχουν στη βάση.
     * @return HashSet με λινκ.
     */
    public HashSet<String> getDocNum() {
        return docSet;
    }

    /**
     * Constructor που κάνει πίνακα την είσοδο του χρήστη.
     * @param search Είσοδος χρήστη.
     */
    QueryProcessor(String search)  {

        termToSearch =search.toLowerCase().split(" ");
    }


    /**
     * Σε αυτή τη μέθοδο υπολογίζονται οι πίνακες TF,IDF,TF*IDF,TermTFIDF και
     * Cosine Similarity όπου o τελευταίος επιστρέφεται στο σημείο όπου θα κληθεί.
     * @param documentsHashSet HashSet με όλες τις εγραφές της βάσης.
     * @param words HashSet με όλες τις λέξεις που περιέχει η βάση.
     * @return Επιστρέφει πίνακα με Cosine Similarity.
     */
    double [] CalculateResults(HashSet<TFD> documentsHashSet, HashSet<String> words)  {
        int docNum=findDocNum(documentsHashSet);
        int i=0,j=0,k=0,n=0;
        docSet=getDocNum();
        TfIdfCalc tfidfcalc=new TfIdfCalc();
        tf=new double[words.size()][docNum];
        tfIdf=new double[termToSearch.length][docNum];
        idf=new double[words.size()];
        cos=new double[docNum];
        termTfIdf=new double[termToSearch.length];

        //Calculate tf,idf arrays
        for (String word: words){
            for (String id:docSet) {
                double Tf = tfidfcalc.tfCalculator(word,id, documentsHashSet);
                double Idf = tfidfcalc.idfCalculator(word,id,documentsHashSet);
                tf[i][j] = Tf;
                idf[i] = Idf;
                j++;
            }
            i++;
            j=0;
        }

        for (String s:termToSearch){
            int pos=findPosition(s, words);
            for (int l=0;l<docNum;l++){
                if (pos!=-1){
                double v1=tf[pos][l];
                double v2=idf[pos];
                tfIdf[k][l]=v1*v2;
            }else{
                    tfIdf[k][l]=0;
                }
        }
            k++;
        }

        HashSet<TFD> temp=calculateTermTFD();
        for (TFD tfd:temp){
            double termTf;
            double termIdf;
            double sumTermFreq=findSumTermFreq(tfd.getTextTerm(),documentsHashSet);
            if (sumTermFreq!=0){
                termTf=(double)tfd.getTermFrequency()/temp.size();
            }else{
                termTf=0.0;
            }
            double numOfDocWithTerm=findNumOfDocWithTerm(tfd.getTextTerm(),documentsHashSet);
            if (numOfDocWithTerm!=0){
            termIdf=1+Math.log(temp.size()/numOfDocWithTerm);
            }else{
               termIdf=0.0;
            }
            termTfIdf[n]=termTf*termIdf;
         n++;}

        CosineSimilarityCalc cosineSimilarityCalc=new CosineSimilarityCalc();
        double[] tempAr=new double[tfIdf.length];
        for (int g=0;g<tfIdf[0].length;g++){
            for (int u=0;u<tfIdf.length;u++){
                double c=tfIdf[u][g];
                tempAr[u]=c;
            }
            cos[g]=cosineSimilarityCalc.calc(tempAr,termTfIdf);
        }
        return cos;
    }

    /**
     * Βρίσκει και επιστρέφει το άθροισμα των συχνοτήτων εμφάνισης της λέξης term.
     * @param term Η λέξη η οποία θέλουμε να αναζητήσουμε τη συχνότητα.
     * @param hash Το σύνολο των δεδομένων της βάσης.
     * @return Τον αριθμό εμφάνισης της λέξης σε όλο το σύνολο των δεδομένων.
     */
    double findSumTermFreq(String term,HashSet<TFD> hash){
        int sum=0;
        for (TFD tfd:hash){
            if (tfd.getTextTerm().equals(term)){
                sum+=tfd.getTermFrequency();
            }
        }
        return sum;
    }


    /**
     * Βρίσκει και επιστρέφει σε πόσα έγγραφα(λινκ) βρέθηκε η λέξη term.
     * @param term Η λέξη η οποία θέλουμε να αναζητήσουμε τη συχνότητα.
     * @param hash Το σύνολο των δεδομένων της βάσης.
     * @return Τον αριθμό των εγγράφων στα οποία βρέθηκε η λέξη.
     */
    double findNumOfDocWithTerm(String term,HashSet<TFD> hash){
        int count=0;
        for (TFD tfd:hash){
            if (tfd.getTextTerm().equals(term)){
                count++;
            }
        }
        return count;
    }

    /**
     * Η μέθοδος αυτή βρίσκει για τη λέξη text σε ποιά θέση του HashSet
     * βρίσκεται και την επιστρέφει.Εάν δεν βρεθεί επιστρέφει -1.
     * @param text Η λέξη για την οποία θέλουμε να βρούμε τη θέση της.
     * @param set Το σύνολο των δεδομένων.
     * @return Τη θέση που βρέθηκε το στοιχείο αλλιώς -1.
     */
    int findPosition(String text,HashSet<String> set){
        int pos=0;
        if (set.contains(text)){
        for (String s:set){
            if (s.equals(text)){
                return pos;
            }else{
                if (pos<set.size()){
                pos++;}
            }
        }
        return pos;
        }
        return -1;
    }

    /**
     *Η μέθοδος υπολογίζει για τους όρους που κάνει ο χρήστης την αναζήτηση τα δεδομένα (t,d,f).
     * @return HashSet με τις εγγραφές απο την είσοδο του χρήστη.
     */
     HashSet<TFD> calculateTermTFD() {
        HashSet<TFD> termTfIdf=new HashSet<>();
        for (String x:termToSearch){
            boolean b=checkIfExists(x,termTfIdf);
            if (!b){
            TFD tfd = new TFD();
            tfd.documentId = x;
            tfd.termFrequency = 1;
            tfd.textTerm = x.toLowerCase();
            termTfIdf.add(tfd);
            }
        }
        return termTfIdf;
    }

    /**
     * Η μέθοδος ελέγχει εάν υπάρχει το στοιχείο x και επιστρέφει αντίστοιχα
     * το αποτέλεσμα (true or false) αυξάνοντας ταυτόχρονα τη συχνότητα εμφάνισης
     * εφόσον η λέξη υπάρχει ήδη.
     * @param x Η λέξη που θέλουμε να αναζητήσουμε εάν υπάρχει.
     * @param termTfIdf HashSet με το σύνολο των δεδομένων.
     * @return True εάν το στοιχείο βρέθηκε false σε αντίθετη περίπτωση.
     */
    private boolean checkIfExists(String x, HashSet<TFD> termTfIdf) {
        boolean bool=false;
        for (TFD tfd : termTfIdf) {
            if (tfd.getTextTerm().equals(x)){
                tfd.setTermFrequency(tfd.getTermFrequency()+1);
                bool=true;
            }
        }
        return bool;
}

    /**
     * Η μέθοδος αυτή συλλέγει όλα τα λινκ απο το σύνολο των δεδομένων και
     * επίσης επιστρέφει πόσα μοναδικά λινκ υπάρχουν στη βάση.
     * @param hash Το σύνολο των δεδομένων της βάσης.
     * @return Επιστρέφει το πλήθος των λινκ.
     */
    public int findDocNum(HashSet<TFD> hash){
        docSet=new HashSet<>();
        for (TFD tfd:hash){
            docSet.add(tfd.getDocumentId());
        }
        return docSet.size();
    }


}
