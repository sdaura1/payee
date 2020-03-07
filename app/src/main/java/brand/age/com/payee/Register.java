package brand.age.com.payee;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonObject;

import brand.age.com.payee.network.APIClient;
import brand.age.com.payee.network.ApiService;
import brand.age.com.payee.network.SharedPreferenceManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Register extends AppCompatActivity {

    private static final String TAG = "Register";
    SharedPreferenceManager sharedPreferenceManager;
    EditText phone, pin, email, fullname;
    Button register_btn;
    ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        sharedPreferenceManager = SharedPreferenceManager.getInstance(this);
        phone = findViewById(R.id.register_phone);
        pin = findViewById(R.id.register_pin);
        email = findViewById(R.id.user_email);
        fullname = findViewById(R.id.fullname);
        register_btn = findViewById(R.id.register_button);

        register_btn.setOnClickListener(view -> {
            String email_string = email.getText().toString();
            String phone_string = phone.getText().toString();
            String fullname_string = fullname.getText().toString();
            String pin_string = pin.getText().toString();

            if (email_string.isEmpty() || phone_string.isEmpty() ||
                    fullname_string.isEmpty() || pin_string.isEmpty() ||
                    phone_string.length() != 11 || pin_string.length() != 4){
                Toast.makeText(this, "Please check your entries", Toast.LENGTH_SHORT).show();
            }else {
                register(phone_string, pin_string, fullname_string, email_string);
            }
        });
    }

    public void register(final String phone, final String pin, String fullname, String email){

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait .... ");
        progressDialog.show();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("email", email);
        jsonObject.addProperty("phone", phone);
        jsonObject.addProperty("pin", pin);
        jsonObject.addProperty("fullname", fullname);

        apiService = APIClient.getCacheEnabledRetrofit(this).create(ApiService.class);

        Call<JsonObject> login_call = apiService.register(jsonObject);
        login_call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()){
                    if (progressDialog.isShowing()){
                        progressDialog.hide();
                    }

                    sharedPreferenceManager.save_phone(phone);
                    sharedPreferenceManager.save_pin(pin);
                    sharedPreferenceManager.save_user_email(email.substring(1, email.length() - 1));
                    sharedPreferenceManager.save_user_full_name(fullname.substring(1, fullname.length() - 1));

                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
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
