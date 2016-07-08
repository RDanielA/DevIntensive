package com.softdesign.devintensive.data.ui.activities;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.util.ConstantManager;
import com.softdesign.devintensive.util.RoundedTransformation;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.security.Permission;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.jar.Manifest;

public class MainActivity extends BaseActivity implements View.OnClickListener{

    private int mCurrentEditMode=0;

    private DataManager mDataManager;
    private final static String TAG = ConstantManager.TAG_PREFIX+"MainActivity";
    private CoordinatorLayout mCoordinatorLayout;
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private FloatingActionButton mFab;
    private RelativeLayout mProfilePlaceholder;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private AppBarLayout.LayoutParams mAppBarParams = null;
    private AppBarLayout mAppBarLayout;
    private File mPhotoFile=null;
    private Uri mSelectedImage=null;
    private ImageView mProfileImage;
    private ImageView mDrawerAvatar;
    private View mDrawerView;

    private EditText mUserPhone,mUserEmail,mUserVk,mUserGit,mUserSelf;
    private List<View> mUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG,"onCreate");

        mDataManager = DataManager.getInstance();
        mCoordinatorLayout = (CoordinatorLayout)findViewById(R.id.main_coordinator_container);
        mToolbar=(Toolbar) findViewById(R.id.toolbar);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.navigation_drawer);
        mProfilePlaceholder = (RelativeLayout)findViewById(R.id.profile_placeholder);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar);
        mAppBarLayout = (AppBarLayout)findViewById(R.id.appbar_layout);
        mProfileImage = (ImageView)findViewById(R.id.user_photo_img);
//        mDrawerAvatar = (ImageView)mDrawerView.findViewById(R.id.drawer_avatar);

        mFab = (FloatingActionButton)findViewById(R.id.fab);
        mUserPhone = (EditText)findViewById(R.id.phone_et);
        mUserEmail = (EditText)findViewById(R.id.email_et);
        mUserVk = (EditText)findViewById(R.id.vk_et);
        mUserGit = (EditText)findViewById(R.id.git_et);
        mUserSelf = (EditText)findViewById(R.id.self_et);

        mUserInfo = new ArrayList<>();

        mUserInfo.add(mUserPhone);
        mUserInfo.add(mUserEmail);
        mUserInfo.add(mUserVk);
        mUserInfo.add(mUserGit);
        mUserInfo.add(mUserSelf);


        setupToolbar();
        setupDrawer();
        loadUserInfoValue();

        Picasso.with(this)
                .load(mDataManager.getPreferenceManager().LoadUserPhoto())
                .placeholder(R.drawable.profile)
                .into(mProfileImage);

//        Picasso.with(this)
//                .load(mDataManager.getPreferenceManager().LoadUserPhoto())
//                .transform(new RoundedTransformation(50,4))
//                .into(mDrawerAvatar);


        mFab.setOnClickListener(this);
        mProfilePlaceholder.setOnClickListener(this);

        if(savedInstanceState ==null){

        }else{
            mCurrentEditMode=savedInstanceState.getInt(ConstantManager.EDIT_MODE_KEY,0);
            changeEditMode(mCurrentEditMode);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            mDrawerLayout.openDrawer(GravityCompat.START );

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG,"onRestart");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG,"onStart");
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"onPause");
        loadUserInfoValue();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG,"onStop");
        saveUserInfoValue();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
        saveUserInfoValue();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab:
                showSnackbar("clickFAb");
                if(mCurrentEditMode==0){
                    changeEditMode(1);
                    mCurrentEditMode=1;
                }else{
                    changeEditMode(0);
                    mCurrentEditMode=0;
                }
                break;
            case R.id.profile_placeholder:
                //// TODO: сделать выбор откуда загружать фото
                showDialog(ConstantManager.LOAD_PROFILE_PHOTO);
                break;
        }
    }

    private void runWithDelay(){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hideProgress();
            }
        },5000);
    }

    private void showSnackbar(String message){
        Snackbar.make(mCoordinatorLayout,message,Snackbar.LENGTH_LONG).show();
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();

        mAppBarParams = (AppBarLayout.LayoutParams)mCollapsingToolbarLayout.getLayoutParams();
        if(actionBar !=null){
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupDrawer(){
        NavigationView navigationView = (NavigationView)findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                showSnackbar(item.getTitle().toString());
                item.setChecked(true);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                return false;
            }
        });

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putInt(ConstantManager.EDIT_MODE_KEY,mCurrentEditMode );
    }

    private void changeEditMode(int mode){
        if(mode==1){
            mFab.setImageResource(R.drawable.ic_check_black_24dp);
            for(View userValue : mUserInfo){
                userValue.setEnabled(true);
                userValue.setFocusable(true);
                userValue.setFocusableInTouchMode(true);

                showProfilePlaceholder();
                lockToolbar();
                mCollapsingToolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);
            }
        }else{
            mFab.setImageResource(R.drawable.ic_create_black_24dp);
            for(View userValue : mUserInfo){
                userValue.setEnabled(false);
                userValue.setFocusable(false);
                userValue.setFocusableInTouchMode(true);

                hideProfilePlaceholder();
                unlockToolbar();
                mCollapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.grey_background));

                saveUserInfoValue();
            }
        }
    }

    private void loadUserInfoValue(){
        List<String> userData = mDataManager.getPreferenceManager().loadUserProfileData();
        for(int i=0;i<userData.size();i++){
            ((EditText)mUserInfo.get(i)).setText(userData.get(i));
        }
    }

    private void saveUserInfoValue(){
        List<String> userData = new ArrayList<>();
        for(View userFieldView : mUserInfo){
            userData.add(((EditText)userFieldView).getText().toString());
        }
        mDataManager.getPreferenceManager().saveUserProfileData(userData);
    }

    /**
     * Получение результат из другой Activity (фото из камеры или галереи
     * @param requestCode
     * @param resultCode
     * @param data
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case ConstantManager.REQUEST_GALLERY_PICTURE:
                if(resultCode==RESULT_OK && data!=null){
                    mSelectedImage=data.getData();

                    insertProfileImage(mSelectedImage);
                }
                break;
            case ConstantManager.REQUEST_CAMERA_PICTURE:
                if(resultCode==RESULT_OK && mPhotoFile !=null){
                    mSelectedImage = Uri.fromFile(mPhotoFile);

                    insertProfileImage(mSelectedImage);
                }
        }
    }



    private void loadPhotoFromGallery(){
        Intent takeGalleryIntent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        takeGalleryIntent.setType("image/*");
        startActivityForResult(Intent.createChooser(takeGalleryIntent,getString(R.string.user_profile_chose_message)),ConstantManager.REQUEST_GALLERY_PICTURE);
    }

    private void loadPhotoFromCamera(){

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED
        && ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){

            Intent takeCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                mPhotoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(mPhotoFile!=null){
                takeCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
                startActivityForResult(takeCaptureIntent,ConstantManager.REQUEST_CAMERA_PICTURE);
            }
        }else{
            ActivityCompat.requestPermissions(this,new String[]{
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            },ConstantManager.CAMERE_REQUEST_PERMISSION_CODE);

            Snackbar.make(mCoordinatorLayout,"Для корректной работы необходимо дать требуемые разрешения",Snackbar.LENGTH_LONG)
                    .setAction("Разрешить", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openApplicationSetting();
                        }
                    }).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == ConstantManager.CAMERE_REQUEST_PERMISSION_CODE && grantResults.length==2){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){

            }
            if(grantResults[1] == PackageManager.PERMISSION_GRANTED){

            }
        }
    }

    private void hideProfilePlaceholder(){
        mProfilePlaceholder.setVisibility(View.GONE);
    }

    private void showProfilePlaceholder(){
        mProfilePlaceholder.setVisibility(View.VISIBLE);
    }

    private void lockToolbar(){
        mAppBarLayout.setExpanded(true,true);
        mAppBarParams.setScrollFlags(0);
        mCollapsingToolbarLayout.setLayoutParams(mAppBarParams);
    }

    private void unlockToolbar(){
        mAppBarParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
        mCollapsingToolbarLayout.setLayoutParams(mAppBarParams);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id){
            case ConstantManager.LOAD_PROFILE_PHOTO:
                String[] selectItems = {getString(R.string.user_profile_dialog_gallery),getString(R.string.user_profile_dialog_camera),getString(R.string.user_profile_dialog_cancel)};

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.user_profile_dialog_title));
                builder.setItems(selectItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                // TODO: загрузить из галлереи
                                loadPhotoFromGallery();
                                break;
                            case 1:
                                //TODO: загрузить из камеры
                                loadPhotoFromCamera();
                                break;
                            case 2:
                                //TODO: отмена
                                dialog.cancel();
                                break;
                        }
                    }
                });
                return builder.create();
            default:
                return null;
        }
    }

    private File createImageFile() throws IOException{
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_"+timeStamp+"_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(imageFileName,".jpg",storageDir);

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE,"image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, image.getAbsolutePath());

        this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
        return image;
    }

    private void insertProfileImage(Uri selectedImage) {
        Picasso.with(this)
                .load(selectedImage)
                .into(mProfileImage);

//        Picasso.with(this)
//                .load(mDataManager.getPreferenceManager().LoadUserPhoto())
//                .transform(new RoundedTransformation(50,4))
//                .into(mDrawerAvatar);

        mDataManager.getPreferenceManager().SaverProfilePhoto(selectedImage);
    }

    public void openApplicationSetting(){
        Intent appSettingIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,Uri.parse("package:"+getPackageName()));

        startActivityForResult(appSettingIntent,ConstantManager.PERMISSON_REQUEST_SETTINGS_CODE);
    }
}
