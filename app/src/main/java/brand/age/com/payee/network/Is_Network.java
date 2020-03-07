package brand.age.com.payee.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.security.spec.ECField;
import java.util.Objects;


public class Is_Network {


    public boolean isWifiConnected(Context contextValue) {
        ConnectivityManager connMgr = (ConnectivityManager)
                contextValue.getSystemService(Context.CONNECTIVITY_SERVICE); // 1
        assert connMgr != null;
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo(); // 2
        return networkInfo != null && networkInfo.isConnected(); // 3
    }

    public boolean isNetworkConnected(Context contextValue) {
        ConnectivityManager connMgr = (ConnectivityManager)
                contextValue.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connMgr != null;
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && (ConnectivityManager.TYPE_WIFI == networkInfo.getType()) && networkInfo.isConnected();
    }

    public boolean isOnline(Context contextValue) {
        try{
            ConnectivityManager cm = (ConnectivityManager) contextValue.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = Objects.requireNonNull(cm).getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                return true;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean haveNetworkConnection(Context contextValue) {
        Context context = contextValue;
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
}
