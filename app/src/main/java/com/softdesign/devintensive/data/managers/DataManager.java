package com.softdesign.devintensive.data.managers;

/**
 * Created by danil on 29.06.2016.
 */
public class DataManager {
    private static DataManager INSTANCE = null;
    private PreferenceManager mPreferenceManager;



    public DataManager(){
        mPreferenceManager = new PreferenceManager();
    }

    public static DataManager getInstance(){
        if(INSTANCE==null){
            INSTANCE= new DataManager();
        }
        return INSTANCE;
    }

    public PreferenceManager getPreferenceManager(){
        return mPreferenceManager;
    }
}
