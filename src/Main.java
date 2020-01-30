import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.lang.reflect.Type;
import java.util.*;
import static java.util.stream.Collectors.toMap;

/**
 * Η κλάση αυτή φροντίζει για την υλοποίηση του υποσυστήματος για την επεξεργασία του ερωτήματος του χρήστη και
 * τον υπολογισμό των ποσοτήτων TF IDF και Cosine Similarity.
 * @author Matskidis Ioannis
 * @author Moutafidis Dimitrios
 */
public class Main {
    public static HashSet<TFD> documentsHashSet = new HashSet<>();//HashSet που περιέχει εγγραφές τύπου (t,d,f).
    private static  HashSet<String> words=new HashSet<>();//HashSet που περιέχει εγγραφές τύπου (t).
    private static HashSet<String> links=new HashSet<>();//HashSet που περέχει όλα τα λινκ που κάναμε crawling.

    /**
     * H μέθοδος αυτή δημιουργεί ένα αντικείμενο queryProcessor με σκοπό να μπορεί να
     * κληθεί για να υλοποιήσει κάποιο κομμάτι του συστήματος.
     * @param args Τα ορίσματα τα οποία θα δώσει ο χρήστης όταν τρέξει το πρόγραμμα.
     */
    public static void main(String[] args) {
        QueryProcessor queryProcessor = new QueryProcessor(args[0]);
        getDocuments(queryProcessor);
    }

    /**
     * Η μέθοδος αυτή φροντίζει για τη λήψη των δεδομένων απο τη βάση (κάνοντας τους απαραίτητους
     * ελέγχους) και στη συνέχεια καλεί μέσω του αντικειμένου queryProcessor τις κατάλληλες
     * μεθόδους από την κλάση Query Processor προκειμένου να υπολογίσει τον τελικό πίνακα
     * με τις βαθμολογίες του ερωτήματος του χρήστη(Cosine Similarity).
     * @param queryProcessor Αντικείμενο για την προσπέλαση των μεθόδων της κλάσης Query Processor.
     */
    public static void getDocuments(QueryProcessor queryProcessor) {

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://woodymats.digital:3001/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CallsInterface callsInterface = retrofit.create(CallsInterface.class);
        Call<JsonArray> call = callsInterface.getDocuments();
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                int code = response.code();
                if (code == 200) {
                    Type type = new TypeToken<HashSet<TFD>>(){}.getType();
                    documentsHashSet = new Gson().fromJson(response.body(), type);
                    createSets();
                    double[] results = queryProcessor.CalculateResults(documentsHashSet,words);
                    links=queryProcessor.docSet;
                    HashMap<String,Double> finalCosSim=new HashMap<>();
                    int i=0;
                    for (String s:links){
                            finalCosSim.put(s,results[i]);
                            i++;
                        }
                    Map<String, Double> sorted = finalCosSim
                            .entrySet()
                            .stream()
                            .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                            .collect(
                                    toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                            LinkedHashMap::new));
                    for (Map.Entry<String,Double>entry:sorted.entrySet()){
                        if (entry.getValue().equals(0.0)){
                            sorted.remove(entry.getKey(),entry.getValue());
                        }
                    }

                    ArrayList<Result> list = new ArrayList<>();
                    for (String url : sorted.keySet()) {
                        Result result = new Result();
                        result.setUrl(url);
                        result.setRank(sorted.get(url));
                        list.add(result);
                    }
                    System.out.println(new Gson().toJson(list, ArrayList.class));
                    System.exit(0);
                } else {
                    System.out.println("Error with code: " + code);
                }

            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable throwable) {
            }
        });

    }

    /**
     * Μέθοδος η οποία εισάγει σε ένα HashSet όλες τις λέξεις που περιέχει η βάση
     * για τον υπολογισμό του πίνακα Cosine Similarity.
     */
    public static void createSets(){
        for (TFD tfd:documentsHashSet){
            words.add(tfd.getTextTerm());
        }
    }

}
