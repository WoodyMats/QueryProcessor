import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.HashSet;

public interface CallsInterface {

    @Headers("Content-Type: application/json")
    @POST("sendDocuments/{keepData}")
    Call<JsonObject> sendDocuments(@Body HashSet<TFD> documents, @Path(value = "keepData", encoded = true) boolean keepData);

    @Headers("Content-Type: application/json")
    @GET("getDocuments")
    Call<JsonArray> getDocuments();
}
