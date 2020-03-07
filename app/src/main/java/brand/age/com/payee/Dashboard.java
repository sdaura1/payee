package brand.age.com.payee;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import brand.age.com.payee.network.APIClient;
import brand.age.com.payee.network.ApiService;
import brand.age.com.payee.network.SharedPreferenceManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Dashboard extends AppCompatActivity {

    private static final String TAG = "Dashboard";
    Button buy_airtime, send_sms, top_up;
    TextView wallet_balance, user_phone;
    SharedPreferenceManager sharedPreferenceManager;
    ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        sharedPreferenceManager = SharedPreferenceManager.getInstance(this);
        user_phone = findViewById(R.id.user_name_display);
        wallet_balance = findViewById(R.id.wallet_balance);
        buy_airtime = findViewById(R.id.buy_airtime);
        send_sms = findViewById(R.id.send_sms);
        top_up = findViewById(R.id.top_up);

        check_balance();
        user_phone.setText(sharedPreferenceManager.get_phone());

        buy_airtime.setOnClickListener(view -> dialog());

        send_sms.setOnClickListener(view -> sms_dialog());
    }

    private void dialog(){
        final Dialog d = new Dialog(this);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.airtime_dialog);
        d.setCancelable(true);
        Objects.requireNonNull(d.getWindow())
                .setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.white)));
        d.show();

        EditText number = d.findViewById(R.id.airtime_number);
        EditText amount = d.findViewById(R.id.airtime_amount);
        Button button_done = d.findViewById(R.id.send_airtime_btn);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(d.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        button_done.setOnClickListener(view1 -> {

            String n = number.getText().toString();
            String a = amount.getText().toString();

            String nn = n.substring(1);

            if (!a.isEmpty()){
                send_airtime("+234" + nn, a);
            }

            d.dismiss();
            d.getWindow().setAttributes(lp);
        });
    }

    private void sms_dialog(){
        final Dialog d = new Dialog(this);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.sms_dialog);
        d.setCancelable(true);
        Objects.requireNonNull(d.getWindow())
                .setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.white)));
        d.show();

        EditText sender_id = d.findViewById(R.id.sender_id);
        EditText number = d.findViewById(R.id.sms_number);
        EditText message = d.findViewById(R.id.sms_message);
        Button button_done = d.findViewById(R.id.send_airtime_btn);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(d.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        button_done.setOnClickListener(view1 -> {

            String n = number.getText().toString();
            String m = message.getText().toString();
            String s = sender_id.getText().toString();

            String nn = n.substring(1);

            if (!m.isEmpty()){
                send_sms("+234" + nn, s, m, Integer.valueOf(sharedPreferenceManager.get_user_id()));
            }

            d.dismiss();
            d.getWindow().setAttributes(lp);
        });
    }


    private void check_balance() {
        String url_ = "https://payee.indexial.tech/api/user_wallet.php?userid="+ sharedPreferenceManager.get_user_id();
        JSONObject request = new JSONObject();
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(
                Request.Method.POST, url_, request, response -> {
            try {
                if (response.getInt("status") == 0) {
                    wallet_balance.setText("₦" + response.getString("balance"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
                }, error -> {

        }
        );
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsArrayRequest);
    }

    public void send_airtime(final String phone, final String amount){

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait .... ");
        progressDialog.show();

        apiService = APIClient.getCacheEnabledRetrofit(this).create(ApiService.class);

        Call<JsonObject> login_call = apiService.send_airtime(phone, amount,
                Integer.valueOf(sharedPreferenceManager.get_user_id()));
        login_call.enqueue(new Callback<JsonObject>() {

            private String status;

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()){
                    if (progressDialog.isShowing()){
                        progressDialog.hide();
                    }

                    Log.d(TAG, "onResponse: " + response.body().toString());

                    JsonArray jsonArray = response.body().getAsJsonArray("responses").getAsJsonArray();

                    for (int i = 0; i < jsonArray.size(); i++){
                        status = jsonArray.get(i).getAsJsonObject().get("status").toString();
                    }

                    Toast.makeText(Dashboard.this, status, Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(getIntent());
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

    public void send_sms(final String phone, final String senderid, String message, int userid){

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait .... ");
        progressDialog.show();

        apiService = APIClient.getCacheEnabledRetrofit(this).create(ApiService.class);

        Call<JsonObject> login_call = apiService.send_sms(phone, senderid, message, userid);
        login_call.enqueue(new Callback<JsonObject>() {

            private String status;

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()){
                    if (progressDialog.isShowing()){
                        progressDialog.hide();
                    }

                    Log.d(TAG, "onResponse: " + response.body().toString());

                    JsonArray jsonArray = response.body().getAsJsonArray("responses").getAsJsonArray();

                    for (int i = 0; i < jsonArray.size(); i++){
                        status = jsonArray.get(i).getAsJsonObject().get("status").toString();
                    }

                    Toast.makeText(Dashboard.this, status, Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(getIntent());
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
