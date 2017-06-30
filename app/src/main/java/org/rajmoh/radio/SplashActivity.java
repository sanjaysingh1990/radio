package org.rajmoh.radio;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import org.rajmoh.radio.utils.Util;

import crickit.debut.com.library.CheckLatestVersion;
import crickit.debut.com.library.ShowDialog;
import crickit.debut.com.library.UpdateApplication;


public class SplashActivity extends AppCompatActivity {
    private String PACKAGE_NAME;
    private String PLAY_STORE_LINK = "https://play.google.com/store/apps/details?id=";
    private boolean mVersionChecked = false;
    private int mCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash);
        //GET PACKAGE NAME
        PACKAGE_NAME = getApplicationContext().getPackageName();
        PLAY_STORE_LINK = PLAY_STORE_LINK + PACKAGE_NAME + "&hl=en";
        if (Util.getInstance().isOnline(this)) {
            CheckLatestVersion checkLatestVersion = new CheckLatestVersion(this);
            final ShowDialog showDialog = new ShowDialog(this);
            checkLatestVersion.getCurrentVersion(PLAY_STORE_LINK, new UpdateApplication() {
                @Override
                public void newVersionFound(String latesversion) {
                    showDialog.showForceUpdateDialog(PACKAGE_NAME);
                }

                @Override
                public void noUpdate(String message) {
                    mVersionChecked = true;


                }
            });
            startHandler();
        } else {
            moveToDashboard(); //to dashboard
        }
    }

    void moveToDashboard() {
        Intent mainActivity = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(mainActivity);
        finish();
    }

    void startHandler() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                mCount++;
                if (mCount > 2 && mVersionChecked) {
                    moveToDashboard(); //to dashboard
                } else {
                    handler.postDelayed(this, 1000);
                }
            }
        }, 1000);
    }
}
