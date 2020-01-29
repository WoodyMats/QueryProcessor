import java.util.HashSet;


class TfIdfCalc {


     double tfCalculator(String str, String id, HashSet<TFD> links)
     {
        double freq=0;
        double maxl=0;
        for (TFD tfd:links) {
            if (tfd.getDocumentId().equals(id)){
                maxl++;
        if (tfd.getTextTerm().equals(str)){
          freq+=tfd.getTermFrequency();
        }}}
        if (maxl==0 && freq==0){
            return 0.0;
        }
        return (freq/maxl);
    }


    double idfCalculator( String str, HashSet<TFD> links) {
    double count=0;
        for (TFD tfd:links){
            if (tfd.getTextTerm().equals(str)){
                count++;
            }
        }
        if (count==0){
            return 0.0;
        }
            return  Math.log(links.size()/count);

    }


}
