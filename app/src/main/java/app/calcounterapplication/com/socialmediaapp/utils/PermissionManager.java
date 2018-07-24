package app.calcounterapplication.com.socialmediaapp.utils;

import android.Manifest;
import android.app.Activity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;



/**
 * Created by idoed on 24/07/2018.
 */

public class PermissionManager extends Activity {
    private Context context;
    private Activity activity;
    private final int STORAGE_PERMISSION_CODE=1,EXTERNAL_STORAGE=20,INTERNAL_STORAGE=3;
    private static int YES=1,NO=2, ANSWER=0;

    public PermissionManager(Context context,Activity activity) {
        this.context = context;
        this.activity=activity;
    }


    public int checkPermission(int PERMISSION_TYPE) {
        if(PERMISSION_TYPE==EXTERNAL_STORAGE) {

            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                ANSWER = YES;

            } else {
                requestPermission(activity,EXTERNAL_STORAGE);
            }
        }
//        else  if(PERMISSION_TYPE==INTERNAL_STORAGE) {
//
//            if (ContextCompat.checkSelfPermission(context, Manifest.permission.INTERN) == PackageManager.PERMISSION_GRANTED) {
//                ANSWER = YES;
//
//            } else {
//                requestPermission(activity,EXTERNAL_STORAGE);
//            }
//        }
        return ANSWER;
    }

    private void requestPermission(final Activity activity,int PERMISSION_TYPE) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE)) {

            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
        }else{
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
            ANSWER=YES;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==STORAGE_PERMISSION_CODE){
            if(grantResults.length>0 &&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                ANSWER=YES;

            }else{
                ANSWER=NO;
            }
        }
    }
}
