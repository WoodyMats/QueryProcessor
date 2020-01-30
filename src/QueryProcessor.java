import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashSet;

public class QueryProcessor {
    double [][] tfIdf;//tf idf weights array
    double [][] tf;
    double [] idf,cos,termTfIdf;
    String [] termToSearch;


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
        int i=0,j=0,k=0,n=0;
        docId=getDocNum();
        TfIdfCalc tfidfcalc=new TfIdfCalc();
System.out.println(documentsHashSet.size());
        tf=new double[words.size()][docNum];
        tfIdf=new double[termToSearch.length][docNum];
        idf=new double[words.size()];
        cos=new double[docNum];
        termTfIdf=new double[termToSearch.length];

        //Calculate tf,idf arrays
        for (String word: words){
            for (String id:docId) {
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
//        System.out.println("tf is "+tf.length+" "+tf[0].length+" idf is "+idf.length+" tfidf is "+tfIdf.length+" "+tfIdf[0].length+" term tf idf is "+termTfIdf.length);
//        System.out.println("TF is ");
//        System.out.println(Arrays.deepToString(tf));
//        System.out.println("IDF is ");
//        System.out.println(Arrays.toString(idf));
//        System.out.println("TFIDF is ");
//        System.out.println(Arrays.deepToString(tfIdf));
//        System.out.println("Term tfidf is ");
//        System.out.println(Arrays.toString(termTfIdf));
        return cos;
    }

    double findSumTermFreq(String term,HashSet<TFD> hash){
        int sum=0;
        for (TFD tfd:hash){
            if (tfd.getTextTerm().equals(term)){
                sum+=tfd.getTermFrequency();
            }
        }
        return sum;
    }


    double findNumOfDocWithTerm(String term,HashSet<TFD> hash){
        int count=0;
        for (TFD tfd:hash){
            if (tfd.getTextTerm().equals(term)){
                count++;
            }
        }
        return count;
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
