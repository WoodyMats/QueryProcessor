public class CosineSimilarityCalc {
    double  calc(double [] tfIdf,double [] termTfIdf){
        double dotProduct = 0.0;
        double magnitude1 = 0.0;
        double magnitude2 = 0.0;
        double cosineSimilarity;

        for (double value : tfIdf) {
            for (double v : termTfIdf) {
                dotProduct += value * v;  //a.b
                magnitude1 += Math.pow(value, 2);  //(a^2)
                magnitude2 += Math.pow(v, 2); //(b^2)
            }
        }
        magnitude1 = Math.sqrt(magnitude1);//sqrt(a^2)
        magnitude2 = Math.sqrt(magnitude2);//sqrt(b^2)
        if (magnitude1 != 0.0 && magnitude2 != 0.0)
        {
            cosineSimilarity = dotProduct / (magnitude1 * magnitude2);
        }
        else
        {
            return 0.0;
        }
        return cosineSimilarity;
    }
}

