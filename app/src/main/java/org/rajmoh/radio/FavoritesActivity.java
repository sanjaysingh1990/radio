/**
 * MainActivity.java
 * Implements the app's main activity
 * The main activity sets up the main view end inflates a menu bar menu
 * <p>
 * This file is part of
 * TRANSISTOR - Radio App for Android
 * <p>
 * Copyright (c) 2015-17 - Y20K.org
 * Licensed under the MIT-License
 * http://opensource.org/licenses/MIT
 */

package org.rajmoh.radio;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.greenrobot.eventbus.EventBus;
import org.rajmoh.radio.databinding.ActivityFavoritesBinding;
import org.rajmoh.radio.helpers.LogHelper;
import org.rajmoh.radio.helpers.StorageHelper;
import org.rajmoh.radio.helpers.TransistorKeys;
import org.rajmoh.radio.utils.Constants;

import java.io.File;


/**
 * MainActivity class
 */
public final class FavoritesActivity extends AppCompatActivity implements TransistorKeys {

    /* Define log tag */
    private static final String LOG_TAG = FavoritesActivity.class.getSimpleName();
    ActivityFavoritesBinding binding;
    /* Main class variables */
    private boolean mTwoPane;
    private File mCollectionFolder;
    private View mContainer;
    private BroadcastReceiver mCollectionChangedReceiver;
    private File mFolder;
    private File mCategoryFolder;
    private String mDirectoryName;
    private AdView adView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getResources().getString(R.string.favorites));
        // get collection folder
        StorageHelper storageHelper = new StorageHelper(this);
        mCollectionFolder = storageHelper.getCollectionDirectory();
        if (mCollectionFolder == null) {
            Toast.makeText(this, getString(R.string.toastalert_no_external_storage), Toast.LENGTH_LONG).show();
            finish();
        }

        // set layout
        binding = DataBindingUtil.setContentView(this, R.layout.activity_favorites);
        binding.setFavorateActivity(this);
        init();

        // initialize broadcast receivers
        initializeBroadcastReceivers();
    }

    private void init() {
        // get collection folder
        StorageHelper storageHelper = new StorageHelper(this);
        mFolder = storageHelper.getCollectionDirectory();

        //default for hindi
        mCategoryFolder = new File(mFolder.getAbsolutePath() + "/favorites");
        if (!mCategoryFolder.exists())
            mCategoryFolder.mkdirs();


        //load banner ad
        adView = (AdView) findViewById(R.id.adView);
//load banner ads
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("abc")
                .build();
        adView.loadAd(adRequest);

        EventBus.getDefault().post(new Boolean(true));// update is favorite


        //left drawer click event

        binding.imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // check if two pane mode can be used
        mTwoPane = detectTwoPane();

        // tablet mode: show player fragment in player container
        if (mTwoPane && mCollectionFolder.listFiles().length > 1) {
            // hide right pane
            mContainer.setVisibility(View.VISIBLE);
        } else if (mTwoPane) {
            // make room for action call
            mContainer.setVisibility(View.GONE);
        }

        saveAppState(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterBroadcastReceivers();
    }


    @Override
    public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {
        super.onMultiWindowModeChanged(isInMultiWindowMode);
        // check if two pane mode can be used
        mTwoPane = detectTwoPane();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // activity opened for second time set intent to new intent
        setIntent(intent);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // make sure that MainActivityFragment's onActivityResult() gets called
        super.onActivityResult(requestCode, resultCode, data);
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Fragment fragment = getFragmentManager().findFragmentById(R.id.fragment_main);
        // hand results over to fragment main
        fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    /* Checks if two-pane mode can be used */
    private boolean detectTwoPane() {
        mContainer = findViewById(R.id.player_container);

        // if player_container is present two-pane layout can be used
        if (mContainer != null) {
            LogHelper.v(LOG_TAG, "Large screen detected. Choosing two pane layout.");
            return true;
        } else {
            LogHelper.v(LOG_TAG, "Small screen detected. Choosing single pane layout.");
            return false;
        }
    }


    /* Saves app state to SharedPreferences */
    private void saveAppState(Context context) {
       // SharedPreferences settings = getSharedPreferences(Constants.FILE_NAME, Context.MODE_PRIVATE);
         SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        // editor.putInt(PREF_STATION_ID_SELECTED, mStationID);
        editor.putBoolean(PREF_TWO_PANE, mTwoPane);
        editor.apply();
        LogHelper.v(LOG_TAG, "Saving state. Two Pane = " + mTwoPane);
    }


    /* Unregisters broadcast receivers */
    private void unregisterBroadcastReceivers() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mCollectionChangedReceiver);
    }


    /* Initializes broadcast receivers for onCreate */
    private void initializeBroadcastReceivers() {
        // RECEIVER: station added, deleted, or changed
        mCollectionChangedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // show/hide player layout container
                if (mTwoPane && mCollectionFolder.listFiles().length == 1) {
                    // make room for action call - hide player container
                    mContainer.setVisibility(View.GONE);
                } else if (mTwoPane) {
                    // show player container
                    mContainer.setVisibility(View.VISIBLE);
                }
            }
        };
        IntentFilter collectionChangedIntentFilter = new IntentFilter(ACTION_COLLECTION_CHANGED);
        LocalBroadcastManager.getInstance(this).registerReceiver(mCollectionChangedReceiver, collectionChangedIntentFilter);
    }


}