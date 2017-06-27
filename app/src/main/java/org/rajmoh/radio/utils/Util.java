package org.rajmoh.radio.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.rajmoh.radio.callbacks.SnackBarEvent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;


/**
 * Created by android on 9/5/17.
 */

public class Util {
    private static Util singleton = new Util();
    private Snackbar snackbar = null;
    private DatabaseReference mDatabase;
    private int mCount = 0;
    private Util() {

    }

    public synchronized static Util getInstance() {
        return singleton;
    }

    public int getRadomNumber() {
        Colors[] cards = Colors.values();
        int min = 0;
        int max = cards.length - 1;

        Random r = new Random();
        return r.nextInt(max - min + 1) + min;
    }

    public String coolFormat(double n, int iteration) {
        char[] c = new char[]{'K', 'M', 'B', 'T'};

        double d = ((long) n / 100) / 10.0;
        boolean isRound = (d * 10) % 10 == 0;//true if the decimal part is equal to 0 (then it's trimmed anyway)
        return (d < 1000 ? //this determines the class, i.e. 'k', 'm' etc
                ((d > 99.9 || isRound || (!isRound && d > 9.99) ? //this decides whether to trim the decimals
                        (int) d * 10 / 10 : d + "" // (int) d * 10 / 10 drops the decimal
                ) + "" + c[iteration])
                : coolFormat(d, iteration + 1));

    }

    public String Format(Integer number) {
        String[] suffix = new String[]{"k", "m", "b", "t"};
        int size = (number.intValue() != 0) ? (int) Math.log10(number) : 0;
        if (size >= 3) {
            while (size % 3 != 0) {
                size = size - 1;
            }
        }
        double notation = Math.pow(10, size);
        String result = (size >= 3) ? +(Math.round((number / notation) * 100) / 100.0d) + suffix[(size / 3) - 1] : +number + "";
        return result;
    }

    public String getDateTime(String startTime) {
        String data = "";
        /*
         ********************** DATE AND TIME FORMATTING EXCPETION ******************
         */
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date myDate = simpleDateFormat.parse(startTime);
            DateFormat date = new SimpleDateFormat("EEE dd");
            DateFormat date2 = new SimpleDateFormat("EEE dd hh:mm a");
            DateFormat time = new SimpleDateFormat("hh:mm a");
            data = String.valueOf(date2.format(myDate));
        } catch (Exception ex) {
            Log.e("error", ex.getMessage());
        }

        return data;
    }

    /*
    ************************* save values to shared preference ***********************
     */
    public void saveValueToSharedPreference(String key, String value,
                                            Context context) {
        if (context != null) {
            SharedPreferences prefs;
            prefs = context.getSharedPreferences(Constants.FILE_NAME, 0);
            SharedPreferences.Editor saveValue = prefs.edit();
            saveValue.putString(key, value);
            saveValue.commit();
        }
    }

    /**
     * @param key          The key from you want to get the value.
     * @param defaultValue Default value, if nothing is found on that key.
     * @param context
     * @description To get the value from a preference file on the specified key.
     */
    public String getValueFromSharedPreference(String key, String defaultValue, Context context) {
        //possible may fragment removed and api trying to get value
        if (context != null) {
            SharedPreferences prefs;
            prefs = context.getSharedPreferences(Constants.FILE_NAME, 0);
            return prefs.getString(key, defaultValue);
        } else {
            return "";
        }

    }

    /*
    ************************* save values to shared preference ***********************
     */
    public void saveValueToSharedPreference(String key, boolean value,
                                            Context context) {
        if (context != null) {
            SharedPreferences prefs;
            prefs = context.getSharedPreferences(Constants.FILE_NAME, 0);
            SharedPreferences.Editor saveValue = prefs.edit();
            saveValue.putBoolean(key, value);
            saveValue.commit();
        }
    }

    /**
     * @param key          The key from you want to get the value.
     * @param defaultValue Default value, if nothing is found on that key.
     * @param context
     * @description To get the value from a preference file on the specified key.
     */
    public boolean getValueFromSharedPreference(String key, boolean defaultValue, Context context) {
        //possible may fragment removed and api trying to get value
        if (context != null) {
            SharedPreferences prefs;
            prefs = context.getSharedPreferences(Constants.FILE_NAME, 0);
            return prefs.getBoolean(key, defaultValue);
        } else {
            return false;
        }

    }


    public void clearValues(Context context) {
        SharedPreferences prefs;
        prefs = context.getSharedPreferences(Constants.FILE_NAME, 0);
        SharedPreferences.Editor saveValue = prefs.edit();
        saveValue.clear().commit();
    }

    public void showToast(Context con, String message) {
        Toast.makeText(con, message, Toast.LENGTH_LONG).show();
    }

    public String getDeviceId(Context context) {
        //get device id
        String android_id = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        if (android_id != null) {
            return android_id;
        } else {
            return "allvines" + System.currentTimeMillis(); // unique device id is blank
        }
    }

    public String getCurrentTime() {
        Calendar datetime = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return simpleDateFormat.format(datetime.getTime());

    }

    public void showSnackBar(View view, String message, String retrytext, boolean ispermanent, final SnackBarEvent snackBarEvent) {
        snackbar = Snackbar
                .make(view, message, Snackbar.LENGTH_LONG)
                .setAction(retrytext, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        snackbar.dismiss();
                        if (snackBarEvent != null) {
                            snackBarEvent.retry();
                        }

                    }
                });

        // Changing message text color
        snackbar.setActionTextColor(Color.RED);

        // Changing action button text color
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        if (ispermanent) //disable autohide if true
            snackbar.setDuration(Snackbar.LENGTH_INDEFINITE);
        snackbar.show();

    }

    /**
     * @return True, if device is having a Internet connection available.
     */
    public boolean isOnline(Context context) {

        ConnectivityManager cm = null;
        if (context != null) {
            cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                return true;
            }
        }

        return false;
    }


    public DatabaseReference getDatabaseReference() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance().getReference();
        }

        return mDatabase;
    }

    public void increment() {
        mCount++;
    }

    public int getCount() {
        return mCount;
    }

}
