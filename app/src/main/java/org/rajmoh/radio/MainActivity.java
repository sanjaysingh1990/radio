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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.rajmoh.radio.adapters.RecyclerCategoryAdapter;
import org.rajmoh.radio.adapters.RecyclerChannelsAdapter;
import org.rajmoh.radio.callbacks.CategorySelectedCallback;
import org.rajmoh.radio.callbacks.ChannelSelectedCallBack;
import org.rajmoh.radio.callbacks.SnackBarEvent;
import org.rajmoh.radio.databinding.ActivityMainBinding;
import org.rajmoh.radio.helpers.LogHelper;
import org.rajmoh.radio.helpers.StationFetcher;
import org.rajmoh.radio.helpers.StorageHelper;
import org.rajmoh.radio.helpers.TransistorKeys;
import org.rajmoh.radio.pojo.CategoryModel;
import org.rajmoh.radio.pojo.ChannelInfo;
import org.rajmoh.radio.utils.Util;

import java.io.File;
import java.util.ArrayList;


/**
 * MainActivity class
 */
public final class MainActivity extends AppCompatActivity implements TransistorKeys, CategorySelectedCallback, ChannelSelectedCallBack {

    /* Define log tag */
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    ActivityMainBinding binding;
    /* Main class variables */
    private boolean mTwoPane;
    private File mCollectionFolder;
    private View mContainer;
    private BroadcastReceiver mCollectionChangedReceiver;
    private LinearLayoutManager mLayoutManager;
    private RecyclerCategoryAdapter mAdapter;
    private ArrayList<CategoryModel> mCategoryList;
    private File mFolder;
    private File mCategoryFolder;
    private RecyclerChannelsAdapter mChannelsAdapter;
    private ArrayList<ChannelInfo> mChannelList;
    private String mDirectoryName;
    private AdView adView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get collection folder
        StorageHelper storageHelper = new StorageHelper(this);
        mCollectionFolder = storageHelper.getCollectionDirectory();
        if (mCollectionFolder == null) {
            Toast.makeText(this, getString(R.string.toastalert_no_external_storage), Toast.LENGTH_LONG).show();
            finish();
        }

        // set layout
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setRadioActivity(this);
        init();

        // initialize broadcast receivers
        initializeBroadcastReceivers();
    }

    private void init() {
        // get collection folder
        StorageHelper storageHelper = new StorageHelper(this);
        mFolder = storageHelper.getCollectionDirectory();

        //default for hindi
        mCategoryFolder = new File(mFolder.getAbsolutePath() + "/Hindi");
        if (!mCategoryFolder.exists())
            mCategoryFolder.mkdirs();

        //disable swipe
        binding.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        binding.mainContent.textCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.drawer.openDrawer(GravityCompat.START);

            }
        });
        binding.mainContent.textChannelList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.drawer.openDrawer(GravityCompat.END);

            }
        });

        binding.mainContent.textCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.drawer.openDrawer(GravityCompat.START);

            }
        });

        //open drawer on left side click
        binding.rightDrawerContent.textChannelList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.drawer.closeDrawer(GravityCompat.END);

            }
        });
        binding.leftDrawerContent.textCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.drawer.closeDrawer(GravityCompat.START);

            }
        });


        mCategoryList = new ArrayList<>();
        mChannelList = new ArrayList<>();
        mLayoutManager = new LinearLayoutManager(this);
        //left side recycler view
        binding.leftDrawerContent.recyclerviewLeftdrawer.setLayoutManager(mLayoutManager);
        //right side recycler view

        binding.rightDrawerContent.recyclerviewRightdrawer.setLayoutManager(new LinearLayoutManager(this));
        //recyclerview adapter
        mAdapter = new RecyclerCategoryAdapter(this, mCategoryList, this);
        mChannelsAdapter = new RecyclerChannelsAdapter(this, mChannelList, this);
        //set adpater for recyclerview
        binding.leftDrawerContent.recyclerviewLeftdrawer.setAdapter(mAdapter);
        binding.rightDrawerContent.recyclerviewRightdrawer.setAdapter(mChannelsAdapter);
        loadFromServer();
        loadChannels("hindi");

        //load banner ad
        adView = (AdView)findViewById(R.id.adView);
//load banner ads
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("abc")
                .build();
        adView.loadAd(adRequest);


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
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_actionbar, menu);
        return super.onCreateOptionsMenu(menu);
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

    @Override
    public void categorySelected(String cateName) {
        loadChannels(cateName);
        binding.drawer.closeDrawer(GravityCompat.START);
        binding.drawer.openDrawer(GravityCompat.END);
        binding.rightDrawerContent.progressbar.setVisibility(View.VISIBLE);
        mDirectoryName = cateName;
        StorageHelper storageHelper = new StorageHelper(this);
        mFolder = storageHelper.getCollectionDirectory();
        mCategoryFolder = new File(mFolder.getAbsolutePath() + "/" + cateName);
        if (!mCategoryFolder.exists())
            mCategoryFolder.mkdirs();
        EventBus.getDefault().post(new String(cateName));// hide search progress if running
        Intent intent = new Intent(this, PlayerService.class);
        intent.setAction(ACTION_STOP);
        startService(intent);


    }

    private void loadFromServer() {
        if (Util.getInstance().isOnline(this)) {
            //  activityFavoriteBinding.progressbar.setVisibility(View.VISIBLE);

            //get device id
           /* String android_id = Util.getInstance().getDeviceId(FavoriteActivity.this);
            String favoritebranch="UserFavorites";
            if(getApplicationContext().getPackageName().compareToIgnoreCase("com.rajmoh.mysteriousworld")==0)
            {
                favoritebranch="UserFavoritesMysetriousWorld";
            }*/
            Util.getInstance().getDatabaseReference().child("cate").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                        CategoryModel item = noteDataSnapshot.getValue(CategoryModel.class);
                        // Log.e("data",item.getCate_name());
                        mCategoryList.add(item);
                        //save to favorite to database
                     /*   Realm myRealm = Util.getInstance().getRelam(FavoriteActivity.this);
                        myRealm.beginTransaction();
                        // Create an object
                        Item itemsave = myRealm.createObject(Item.class);
                        itemsave.setSubtitle(item.getSubtitle());
                        itemsave.setTitle(item.getTitle());
                        itemsave.setUrl(item.getUrl());
                        itemsave.setVideoId(item.getVideoId());
                        itemsave.setCreateAt(item.getCreateAt());
                        myRealm.commitTransaction();*/


                    }
                    mAdapter.notifyDataSetChanged();
                    if (mCategoryList.size() == 0) {
                        Util.getInstance().showSnackBar(binding.drawer, getResources().getString(R.string.no_category_available), "", false, null);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //Log.e("error", databaseError.getMessage());
                    Util.getInstance().showSnackBar(binding.drawer, databaseError.getMessage(), getResources().getString(R.string.retry), true, new SnackBarEvent() {
                        @Override
                        public void retry() {
                            loadFromServer();
                        }
                    });

                }
            });
        } else {
            Util.getInstance().showSnackBar(binding.drawer, getResources().getString(R.string.no_internet_connecton), getResources().getString(R.string.retry), true, new SnackBarEvent() {
                @Override
                public void retry() {
                    loadFromServer();
                }
            });

        }
    }


    private void loadChannels(String categoryName) {
        if (Util.getInstance().isOnline(this)) {
            mChannelList.clear();
            mChannelsAdapter.notifyDataSetChanged();
            //  activityFavoriteBinding.progressbar.setVisibility(View.VISIBLE);

            //get device id
           /* String android_id = Util.getInstance().getDeviceId(FavoriteActivity.this);
            String favoritebranch="UserFavorites";
            if(getApplicationContext().getPackageName().compareToIgnoreCase("com.rajmoh.mysteriousworld")==0)
            {
                favoritebranch="UserFavoritesMysetriousWorld";
            }*/
            // Log.e("cn",categoryName);
            Util.getInstance().getDatabaseReference().child(categoryName.toLowerCase()).child("channel").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    hideProgress();

                    for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                        ChannelInfo item = noteDataSnapshot.getValue(ChannelInfo.class);
                        //  Log.e("data",item.getChannel_name());
                        mChannelList.add(item);
                        //save to favorite to database
                     /*   Realm myRealm = Util.getInstance().getRelam(FavoriteActivity.this);
                        myRealm.beginTransaction();
                        // Create an object
                        Item itemsave = myRealm.createObject(Item.class);
                        itemsave.setSubtitle(item.getSubtitle());
                        itemsave.setTitle(item.getTitle());
                        itemsave.setUrl(item.getUrl());
                        itemsave.setVideoId(item.getVideoId());
                        itemsave.setCreateAt(item.getCreateAt());
                        myRealm.commitTransaction();*/


                    }
                    mChannelsAdapter.notifyDataSetChanged();
                    if (mChannelList.size() == 0) {
                        Util.getInstance().showSnackBar(binding.drawer, getResources().getString(R.string.no_category_available), "", false, null);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //Log.e("error", databaseError.getMessage());
                    Util.getInstance().showSnackBar(binding.drawer, databaseError.getMessage(), getResources().getString(R.string.retry), true, new SnackBarEvent() {
                        @Override
                        public void retry() {
                            loadFromServer();
                        }
                    });
                    hideProgress();

                }
            });
        } else {
            Util.getInstance().showSnackBar(binding.drawer, getResources().getString(R.string.no_internet_connecton), getResources().getString(R.string.retry), true, new SnackBarEvent() {
                @Override
                public void retry() {
                    loadFromServer();
                }
            });
            hideProgress();

        }
    }


    @Override
    public void channelSelected(String channelName, String channelUrl) {
        StationFetcher stationFetcher = new StationFetcher(this, mCategoryFolder, Uri.parse(channelUrl), channelName);
        stationFetcher.execute();
        binding.drawer.closeDrawer(GravityCompat.END);


    }

    private void showProgress() {
        binding.rightDrawerContent.progressbar.setVisibility(View.VISIBLE);
        binding.rightDrawerContent.textLoading.setVisibility(View.VISIBLE);

    }

    private void hideProgress() {
        binding.rightDrawerContent.progressbar.setVisibility(View.GONE);
        binding.rightDrawerContent.textLoading.setVisibility(View.GONE);

    }
}