package com.example.bohemeow;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.ArrayList;

import androidx.core.app.ActivityCompat;

public class PermissionManager {

    private Activity mActivity = null;
    private ArrayList<PermissionListener> alPermissionListener = new ArrayList<PermissionListener>();

    public interface PermissionListener {
        void granted();
        void denied();
    }

    public PermissionManager( Activity activity ) {
        this.mActivity = activity;
    }

    public void request( String[] permissions, PermissionListener listener ) {

        // 권한 리스너 추가
        alPermissionListener.add(listener);

        if (Build.VERSION.SDK_INT >= 23) {
            // 권한 요청 ( 요청 결과는 activity 의 onRequestPermissionsResult 함수로 넘어온다. )
            // 따라서 이 클래스를 맴버변수로 선언하고 onRequestPermissionsResult 함수에서
            // 이 클래스의 setResponse 함수를 호출해줘야 한다.
            // (requestCode = alPermissionListener 의 Index, 결과가 오면 alPermissionListener.get(requestCode) 에게 결과 전달함)
            ActivityCompat.requestPermissions(mActivity, permissions, alPermissionListener.size()-1);
        }
        else {
            // SDK 23 미만은 설치하기 전에 권한요청 함 ( 따라서 이미 권한 있음 )
            listener.granted();
        }
    }

    public void setResponse(int requestCode, int[] grantResults) {
        int cntGrant = 0;
        if ( grantResults.length > 0 ) {
            for( int i=0; i<grantResults.length; i++ ) {
                if( grantResults[i] == PackageManager.PERMISSION_GRANTED ) {
                    cntGrant++;
                }
            }

            if( grantResults.length == cntGrant ) {
                // 권한 승인 허가
                alPermissionListener.get(requestCode).granted();
            }
            else {
                // 권한 승인 거부
                alPermissionListener.get(requestCode).denied();
            }
        } else {
            // 권한 승인 거부
            alPermissionListener.get(requestCode).denied();
        }
    }
}
