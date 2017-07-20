/**
 * InfosheetActivity.java
 * Implements the app's infosheet activity
 * The infosheet activity sets up infosheet screens for "About" and "How to"
 * <p>
 * This file is part of
 * TRANSISTOR - Radio App for Android
 * <p>
 * Copyright (c) 2015-17 - Y20K.org
 * Licensed under the MIT-License
 * http://opensource.org/licenses/MIT
 */


package org.rajmoh.radio;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.rajmoh.radio.helpers.TransistorKeys;


/**
 * InfosheetActivity class
 */
public final class InfosheetActivity extends AppCompatActivity implements TransistorKeys {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get activity title from intent
        Intent intent = this.getIntent();

        // set activity title
        if (intent.hasExtra(EXTRA_INFOSHEET_TITLE)) {
            this.setTitle(intent.getStringExtra(EXTRA_INFOSHEET_TITLE));
        }

        // set activity view
        if (intent.hasExtra(EXTRA_INFOSHEET_CONTENT) && intent.getIntExtra(EXTRA_INFOSHEET_CONTENT, -1) == INFOSHEET_CONTENT_ABOUT) {
            setContentView(R.layout.fragment_infosheet_about);
            initializeBannerAds();
        } else if (intent.hasExtra(EXTRA_INFOSHEET_CONTENT) && intent.getIntExtra(EXTRA_INFOSHEET_CONTENT, -1) == INFOSHEET_CONTENT_HOWTO) {
            setContentView(R.layout.fragment_infosheet_howto);
        }

    }

    private void initializeBannerAds() {
        //for banner add
        AdRequest adRequest = new AdRequest.Builder()
             //   .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
               // .addTestDevice("C04B1BFFB0774708339BC273F8A43708")
                .build();
        AdView adView = (AdView) findViewById(R.id.adView);
        adView.loadAd(adRequest);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int i) {
                //"adderror", i + "");
            }

            @Override
            public void onAdOpened() {
                //"add", "opened");
            }

            @Override
            public void onAdLoaded() {
                //"add", "loaded");
            }
        });
    }

}
