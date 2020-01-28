import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.HashSet;


public class Main {
    public static HashSet<TFD> documentsHashSet = new HashSet<>();

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        long startTime = System.nanoTime();
        // QueryProcessor queryProcessor = new QueryProcessor(args[0]);
        // queryProcessor.getDataDb();

        getDocuments();

     /*   do {
            if (!args[0].equals("")) {
                  double[] results = queryProcessor.CalculateResults();
                System.out.println("Final cosine similarity ");
                //System.out.println(Arrays.toString(results));}
                long endTime = System.nanoTime();
                long totalTime = endTime - startTime;
                System.out.println("Total execution time: " + totalTime / 1000000000 + "sec.");
            }
        }while (!args[0].equals("")) ;
    }

*/
    }

    public static void getDocuments() {

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
                    printDocumentsHashSet();
                } else {
                    System.out.println("Error with code: " + code);
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable throwable) {
            }
        });
    }

    public static void printDocumentsHashSet() {
        for (TFD h : documentsHashSet) {
            System.out.println(h.getTextTerm());
        }
    }

}
