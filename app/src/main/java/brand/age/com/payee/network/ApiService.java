package brand.age.com.payee.network;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

  @POST("api/user_register.php")
  Call<JsonObject> register(@Body JsonObject jsonObject);

  @POST("api/user_login.php")
  Call<JsonObject> login(@Body JsonObject jsonObject);

  @POST("api/user_wallet.php?userid={id}")
  Call<JsonObject> wallet(@Path("id") int id);

  @POST("api/send_airtime.php")
  Call<JsonObject> send_airtime(@Query("phone") String phone,
                                @Query("amount") String amount,
                                @Query("userid") int userid);

  @POST("api/send_airtime.php")
  Call<JsonObject> send_sms(@Query("phone") String phone,
                            @Query("senderid") String senderid,
                            @Query("message") String message,
                            @Query("userid") int userid);

}
