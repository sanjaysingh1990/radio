package org.rajmoh.radio;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.raj.moh.appversionchecker.CheckLatestVersion;
import com.raj.moh.appversionchecker.UpdateApplication;

import org.rajmoh.radio.utils.Util;



public class SplashActivity extends AppCompatActivity {
   private boolean mVersionChecked = false;
    private int mCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash);
        //GET PACKAGE NAME
         if (Util.getInstance().isOnline(this)) {
            final CheckLatestVersion checkLatestVersion = new CheckLatestVersion(this);
              checkLatestVersion.getCurrentVersion(new UpdateApplication() {
                @Override
                public void newVersionFound(String latesversion) {
                   checkLatestVersion.showForceUpdateDialog();
                }

                @Override
                public void noUpdate() {
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
