package com.softdesign.devintensive.data.managers;

import android.content.SharedPreferences;
import android.net.Uri;

import com.softdesign.devintensive.util.ConstantManager;
import com.softdesign.devintensive.util.DevIntensiveApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by danil on 29.06.2016.
 */
public class PreferenceManager {
    private SharedPreferences mSharedPreferences;
    private static final String[] USER_FIELDS = {ConstantManager.USER_PHONE_KEY, ConstantManager.USER_MAIL_KEY,ConstantManager.USER_VK_KEY,ConstantManager.USER_GIT_KEY,ConstantManager.USER_SELF_KEY};

    public PreferenceManager(){
        this.mSharedPreferences = DevIntensiveApplication.getSharedPreferences();
    }

    public void saveUserProfileData(List<String> userFields){
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        for (int i=0;i< USER_FIELDS.length;i++){
            editor.putString(USER_FIELDS[i],userFields.get(i));
        }
        editor.apply();
    }

    public List<String> loadUserProfileData(){
        List<String> userFields = new ArrayList<>();
        userFields.add(mSharedPreferences.getString(ConstantManager.USER_PHONE_KEY,"Null"));
        userFields.add(mSharedPreferences.getString(ConstantManager.USER_MAIL_KEY,"Null"));
        userFields.add(mSharedPreferences.getString(ConstantManager.USER_VK_KEY,"Null"));
        userFields.add(mSharedPreferences.getString(ConstantManager.USER_GIT_KEY,"Null"));
        userFields.add(mSharedPreferences.getString(ConstantManager.USER_SELF_KEY,"Null"));

        return userFields;
    }

    public void SaverProfilePhoto(Uri uri){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(ConstantManager.USER_PHOTO_KEY,uri.toString());
        editor.apply();
    }

    public Uri LoadUserPhoto(){
       return Uri.parse( mSharedPreferences.getString(ConstantManager.USER_PHOTO_KEY,"android.resource://com.softdesign.devintensive/drawable/profile"));
    }
}
