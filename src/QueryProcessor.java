import java.util.HashSet;

public class QueryProcessor {
    double [][] tfIdf;//tf idf weights array
    double [][] tf;
    double [] idf,cos,termTfIdf;
    String [] termToSearch;
    HashSet<HashSet<TFD>> set=new HashSet<>();

    public HashSet<String> getDocNum() {
        return docNum;
    }

    HashSet<String> docNum;
    HashSet<String> docId=new HashSet<>();






    QueryProcessor(String search)  {
       termToSearch =search.split(" ");
    }


    double [] CalculateResults(HashSet<TFD> documentsHashSet, HashSet<String> words)  {
        int docNum=findDocNum(documentsHashSet);
        docId=getDocNum();
        TfIdfCalc tfidfcalc=new TfIdfCalc();
        int i=0,j=0,k=0,n=0;
        tf=new double[words.size()][docNum];
        tfIdf=new double[termToSearch.length][docNum];
        idf=new double[words.size()];
        cos=new double[docNum];
        termTfIdf=new double[termToSearch.length];

        //Calculate tf,idf arrays
        for (String word: words){
            for (String id:docId) {
                double Tf = tfidfcalc.tfCalculator(word,id, documentsHashSet);
                tf[i][j] = Tf;
                j++;
            }
            double Idf = tfidfcalc.idfCalculator(word,documentsHashSet);
            idf[i] = Idf;
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
        for (String str:termToSearch){
            for (String s:docId){
            double termTf=tfidfcalc.tfCalculator(str,s,temp);
            double termIdf=tfidfcalc.idfCalculator(str,temp);
            termTfIdf[n]=termTf*termIdf;
        } n++;}

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

public int findDocNum(HashSet<TFD> hash){
        docNum=new HashSet<>();
        for (TFD tfd:hash){
            docNum.add(tfd.getDocumentId());
        }
        return docNum.size();
    }


}
