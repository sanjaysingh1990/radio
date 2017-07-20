package com.raj.moh.appversionchecker;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


/**
 * Created by NEERAJ on 7/1/2017.
 */

public class CheckLatestVersion {

    private String currentVersion, latestVersion;
    private Dialog dialog;
    private UpdateApplication updateApplication;
    private Context mContext;
    private String PACKAGE_NAME;
    private String PLAY_STORE_LINK = "https://play.google.com/store/apps/details?id=";

    public CheckLatestVersion(Context context)
    {
        mContext=context;
        PACKAGE_NAME = context.getApplicationContext().getPackageName();
        PLAY_STORE_LINK = PLAY_STORE_LINK + PACKAGE_NAME + "&hl=en";
//        Log.e("link",PLAY_STORE_LINK);
//        Log.e("package",PACKAGE_NAME);


    }
    public void getCurrentVersion(UpdateApplication updateApplication){
        this.updateApplication=updateApplication;
        PackageManager pm = mContext.getPackageManager();
        PackageInfo pInfo = null;

        try {
            pInfo =  pm.getPackageInfo(mContext.getPackageName(),0);

        } catch (PackageManager.NameNotFoundException e1) {
            // TODO Auto-generated catch block
            Log.e("error",e1.getMessage()+"");
        }
        currentVersion = pInfo.versionName;
        Log.e("cv",currentVersion+"");
        new GetLatestVersion().execute();

    }

    private class GetLatestVersion extends AsyncTask<String, String, JSONObject> {

        //private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
//It retrieves the latest version by scraping the content of current version from play store at runtime
                Document doc = Jsoup.connect(PLAY_STORE_LINK).get();
                // Log.e("doc",doc.toString()+"");
                latestVersion = doc.getElementsByAttributeValue("itemprop","softwareVersion").first().text();
                Log.e("lv",latestVersion+"");
            }catch (Exception e){
                e.printStackTrace();

            }

            return new JSONObject();
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if(latestVersion!=null) {

                if (!currentVersion.equalsIgnoreCase(latestVersion)){
                    if(updateApplication!=null)
                        updateApplication.newVersionFound(latestVersion);

                }
                else
                {
                    if(updateApplication!=null)
                        updateApplication.noUpdate();
                }
            }
            else
            {
                if(updateApplication!=null)
                    updateApplication.noUpdate();

            }

            super.onPostExecute(jsonObject);
        }
    }

    public void showForceUpdateDialog() {
        android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(mContext,
                R.style.AppCompatAlertDialogStyle);

        alertDialogBuilder.setTitle(mContext.getResources().getString(R.string.updateavailableTitle));
        alertDialogBuilder.setMessage(mContext.getResources().getString(R.string.updateMessage));
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(mContext.getResources().getString(R.string.update), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+PACKAGE_NAME)));

            }
        });
        alertDialogBuilder.show();
    }
}
