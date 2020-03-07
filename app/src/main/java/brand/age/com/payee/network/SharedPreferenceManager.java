package brand.age.com.payee.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.ContactsContract;

import androidx.annotation.NonNull;

import java.util.Date;

public class SharedPreferenceManager {
    private SharedPreferences prefs;
    private static final String PREFS = "prefs";
    private static final String PHONE = "phone";
    private static final String PIN = "pin";
    private static final String USER_FULL_NAME = "user_full_name";
    private static final String USER_ID = "user_id";
    private static final String USER_EMAIL = "user_email";
    private static SharedPreferenceManager Instance =  null;

    private SharedPreferenceManager(@NonNull Context context){
        this.prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        Instance = this;
    }

    public static SharedPreferenceManager getInstance(@NonNull Context context){
        if (Instance == null){
            Instance = new SharedPreferenceManager(context);
        }
        return Instance;
    }

    public void save_phone(String user_number){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PHONE, user_number);
        editor.apply();
    }

    public void save_user_id(String user_id){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(USER_ID, user_id);
        editor.apply();
    }

    public void save_user_full_name(String user_full_name){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(USER_FULL_NAME, user_full_name);
        editor.apply();
    }

    public void save_user_email(String user_email){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(USER_EMAIL, user_email);
        editor.apply();
    }

    public void save_pin(String user_password){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PIN, user_password);
        editor.apply();
    }

    public String get_pin(){
        return prefs.getString(PIN, null);
    }

    public String get_phone(){
        return prefs.getString(PHONE, null);
    }

    public String get_user_email(){
        return prefs.getString(USER_EMAIL, null);
    }

    public String get_fullname(){
        return prefs.getString(USER_FULL_NAME, null);
    }

    public String get_user_id(){
        return prefs.getString(USER_ID, null);
    }

    public void clear() {
        prefs.edit().clear().apply();
    }
}
