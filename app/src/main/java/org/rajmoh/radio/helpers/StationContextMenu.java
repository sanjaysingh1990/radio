/**
 * StationContextMenu.java
 * Implements the StationContextMenu class
 * The StationContextMenu allows manipulation of station objects, eg. rename or delete
 * <p>
 * This file is part of
 * TRANSISTOR - Radio App for Android
 * <p>
 * Copyright (c) 2015-17 - Y20K.org
 * Licensed under the MIT-License
 * http://opensource.org/licenses/MIT
 */


package org.rajmoh.radio.helpers;

import android.app.Activity;
import android.app.DialogFragment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import org.rajmoh.radio.R;
import org.rajmoh.radio.core.Station;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * StationContextMenu class
 */
public final class StationContextMenu extends DialogFragment implements TransistorKeys {


    /* Main class variables */
    private View mView;
    private Activity mActivity;
    private Station mStation;
    private int mStationID;
    private String mCurrentFolder;

    /* Constructor (default) */
    public StationContextMenu() {
    }

    void copy(File src, File dest) throws IOException {

        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(src);
            os = new FileOutputStream(dest); // buffer size 1K
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = is.read(buf)) > 0) {
                os.write(buf, 0, bytesRead);
            }
        } finally {
            if (is != null)
                is.close();
            if (os != null)
                os.close();
        }

    }


    /* Initializer for main class variables */
    public void initialize(Activity activity, View view, Station station, int stationID, String currentFolder) {
        mActivity = activity;
        mView = view;
        mStation = station;
        mStationID = stationID;
        mCurrentFolder = currentFolder;
    }

    /* Displays context menu */
    public void show(final boolean isFavorite) {
        //"curpath", mCurrentFolder + "'");

        PopupMenu popup = new PopupMenu(mActivity, mView);
        if (isFavorite)
            popup.inflate(R.menu.menu_main_list_item);
        else
            popup.inflate(R.menu.menu_main_list_item2);

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {

                    // CASE ICON
                   /* case R.id.menu_icon:
                        // send local broadcast (needed by MainActivityFragment)
                        Intent iconIntent = new Intent();
                        iconIntent.setAction(ACTION_IMAGE_CHANGE_REQUESTED);
                        iconIntent.putExtra(EXTRA_STATION, mStation);
                        iconIntent.putExtra(EXTRA_STATION_ID, mStationID);
                        LocalBroadcastManager.getInstance(mActivity.getApplication()).sendBroadcast(iconIntent);
                        return true;

                    // CASE RENAME
                    case R.id.menu_rename:
                        // construct and run rename dialog
                        DialogRename dialogRename = new DialogRename(mActivity, mStation, mStationID);
                        dialogRename.show();
                        return true;*/

                    // CASE DELETE
                    case R.id.menu_delete:
                        // construct and run delete dialog
                        DialogDelete dialogDelete = new DialogDelete(mActivity, mStation, mStationID);
                        dialogDelete.show(isFavorite);
                        return true;

                    // CASE SHORTCUT
                    case R.id.menu_shortcut: {
                        // create shortcut
//                        ShortcutHelper shortcutHelper = new ShortcutHelper(mActivity);
                        ShortcutHelper shortcutHelper = new ShortcutHelper(mActivity.getApplication().getApplicationContext());
                        shortcutHelper.placeShortcut(mStation);
                        return true;
                    }
                    //case add to favorites
                    case R.id.menu_favorite:
                        // get collection folder
                        StorageHelper storageHelper = new StorageHelper(mActivity);
                        File mCollectionFolder = storageHelper.getCollectionDirectory();
                        File sourceFile = new File(mCollectionFolder.getAbsolutePath() + "/" + mCurrentFolder + "/" + mStation.getStationName() + ".m3u");

                        File destinationFile = new File(mCollectionFolder.getAbsolutePath() + "/favorites/" + mStation.getStationName() + ".m3u");

                        //"sourcefile", sourceFile.getAbsolutePath());
                        //"destinationfile", destinationFile.getAbsolutePath());
                        try {
                            copy(sourceFile, destinationFile);
                        } catch (IOException ex) {
                            //"ioexception", ex.getLocalizedMessage() + "");
                        }
                        return true;

                    // CASE DEFAULT
                    default:
                        return false;
                }
            }
        });
        popup.show();
    }
}