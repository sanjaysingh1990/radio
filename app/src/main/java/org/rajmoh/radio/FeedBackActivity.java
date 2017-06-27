package org.rajmoh.radio;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import org.rajmoh.radio.databinding.ActivityFeedBackBinding;
import org.rajmoh.radio.pojo.FeedbackModel;
import org.rajmoh.radio.utils.Util;

import java.util.regex.Pattern;

public class FeedBackActivity extends AppCompatActivity {

    ActivityFeedBackBinding activityFeedBackBinding;
    private static final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(

            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityFeedBackBinding = DataBindingUtil.setContentView(this, R.layout.activity_feed_back);
        activityFeedBackBinding.setFeedbackActivity(this);
        activityFeedBackBinding.setClickEvent(new ClickEvent());
       init();
    }

    private void init()
    {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }
    public class ClickEvent {
        /**
         * ********************** BACK BUTTON ***************************
         */
        public void submit() {

         if(validate())
         {
             activityFeedBackBinding.progressBar2.setVisibility(View.VISIBLE);
              //get current utc time

             //get device id
             String android_id=Util.getInstance().getDeviceId(FeedBackActivity.this);

             FeedbackModel feedbackModel=new FeedbackModel();
             feedbackModel.setDeviceId(android_id);
             feedbackModel.setTime(Util.getInstance().getCurrentTime());
             feedbackModel.setEmail(activityFeedBackBinding.edittextEmail.getText().toString());
             feedbackModel.setMessage(activityFeedBackBinding.editTextMessage.getText().toString());
           //  Log.e("Android","Android ID : "+android_id);
             String feedbackbranch="feedback";
             if(getApplicationContext().getPackageName().compareToIgnoreCase("com.rajmoh.mysteriousworld")==0)
             {
                  feedbackbranch="feedbackmysetriousworld";
             }
             mDatabase.child(feedbackbranch).child(String.valueOf(System.currentTimeMillis())).setValue(feedbackModel).addOnCompleteListener(FeedBackActivity.this, new OnCompleteListener<Void>() {
                 @Override
                 public void onComplete(@NonNull Task<Void> task) {
                     activityFeedBackBinding.progressBar2.setVisibility(View.GONE);
                     Util.getInstance().showToast(FeedBackActivity.this,getResources().getString(R.string.feedback_response));
                     finish();
                 }
             }).addOnFailureListener(FeedBackActivity.this, new OnFailureListener() {
                 @Override
                 public void onFailure(@NonNull Exception e) {
                     activityFeedBackBinding.progressBar2.setVisibility(View.GONE);
                    // Log.e("error",e.getMessage()+"");
                 }
             }).addOnSuccessListener(FeedBackActivity.this, new OnSuccessListener<Void>() {
                 @Override
                 public void onSuccess(Void aVoid) {
                     finish();
                 }
             });

         }

        }

        public void back() {
              finish();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    initializeBannerAds();
    }

    private boolean validate()
{
    if (TextUtils.isEmpty(activityFeedBackBinding.edittextEmail.getText())) {
        activityFeedBackBinding.edittextEmail.requestFocus();
        activityFeedBackBinding.edittextEmail.setError("empty!");
        return false;
    } else if (TextUtils.isEmpty(activityFeedBackBinding.editTextMessage.getText())) {
        activityFeedBackBinding.editTextMessage.requestFocus();
        activityFeedBackBinding.editTextMessage.setError("empty!");
        return false;
    }
    else if(!isEmailValid(activityFeedBackBinding.edittextEmail.getText().toString()))
    {
        activityFeedBackBinding.edittextEmail.requestFocus();
        activityFeedBackBinding.edittextEmail.setError("invalid email!");
        return false;
    }
    else if(!Util.getInstance().isOnline(this))
    {
        Util.getInstance().showSnackBar(activityFeedBackBinding.constraintlayout, getResources().getString(R.string.no_internet_connecton), "",false,null);
        return false;
    }

    return true;
}

private boolean isEmailValid(String email)
{
   return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
}
private void initializeBannerAds()
{
    //for banner add
    AdRequest adRequest = new AdRequest.Builder()
            .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
            .addTestDevice("C04B1BFFB0774708339BC273F8A43708")
            .build();
    activityFeedBackBinding.adView.loadAd(adRequest);
    activityFeedBackBinding.adView.setAdListener(new AdListener() {
        @Override
        public void onAdFailedToLoad(int i) {
            Log.e("adderror",i+"");
        }

        @Override
        public void onAdOpened() {
            Log.e("add","opened");
        }

        @Override
        public void onAdLoaded() {
            Log.e("add","loaded");
        }
    });
}

}