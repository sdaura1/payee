package brand.age.com.payee;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonObject;

import brand.age.com.payee.network.APIClient;
import brand.age.com.payee.network.ApiService;
import brand.age.com.payee.network.SharedPreferenceManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    EditText phone, pin;
    Button login_btn;
    TextView register_page;
    ApiService apiService;
    SharedPreferenceManager sharedPreferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferenceManager = SharedPreferenceManager.getInstance(this);
        register_page = findViewById(R.id.register_page);
        phone = findViewById(R.id.phone);
        pin = findViewById(R.id.user_pin);
        login_btn = findViewById(R.id.login_button);

        login_btn.setOnClickListener(view -> {
            String pin_string = pin.getText().toString();
            String phone_string = phone.getText().toString();

            if (pin_string.length() != 4 || phone_string.length() != 11){
                Log.d(TAG, "onCreate: Update");
            }else {
                login(phone_string, pin_string);
            }
        });

        register_page.setOnClickListener(view -> startActivity(new Intent(this, Register.class)));
    }

    public void login(final String phone, final String pin){

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait .... ");
        progressDialog.show();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("phone", phone);
        jsonObject.addProperty("pin", pin);

        apiService = APIClient.getCacheEnabledRetrofit(this).create(ApiService.class);

        Call<JsonObject> login_call = apiService.login(jsonObject);
        login_call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()){
                    if (progressDialog.isShowing()){
                        progressDialog.hide();
                    }

                    JsonObject object = response.body().getAsJsonObject();

                    String email = object.get("email").toString();
                    String fullname = object.get("fullname").toString();
                    String id = object.get("id").toString();

                    sharedPreferenceManager.save_phone(phone);
                    sharedPreferenceManager.save_pin(pin);
                    sharedPreferenceManager.save_user_email(email.substring(1, email.length() - 1));
                    sharedPreferenceManager.save_user_full_name(fullname.substring(1, fullname.length() - 1));
                    sharedPreferenceManager.save_user_id(id);

                    Log.d(TAG, "onResponse: " +
                            sharedPreferenceManager.get_user_email() + "\n" + sharedPreferenceManager.get_fullname() +
                            "\n" + sharedPreferenceManager.get_user_id());

                    startActivity(new Intent(getApplicationContext(), Dashboard.class));
                    finish();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (progressDialog.isShowing()){
                    progressDialog.hide();
                }
            }
        });
    }
}
