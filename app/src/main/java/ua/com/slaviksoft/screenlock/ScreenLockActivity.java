package ua.com.slaviksoft.screenlock;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

public class ScreenLockActivity extends AppCompatActivity {

    final String TAG = "DEBUG:" + getClass().getSimpleName();
    private String action;
    private String password;

    private TextView textViewInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_lock);

        Log.d(TAG, "onCreate");

        Intent intent = getIntent();
        if (intent.hasExtra(AppFirebaseMessagingService.EXTRA_MESSAGE_ACTION)){
            action = intent.getStringExtra(AppFirebaseMessagingService.EXTRA_MESSAGE_ACTION);
            password = intent.getStringExtra(AppFirebaseMessagingService.EXTRA_MESSAGE_PASSWORD);
        }else
            finish();

        DevicePolicyManager mDevicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName mComponentName = new ComponentName(this, Admin.class);
        boolean isAdmin = mDevicePolicyManager.isAdminActive(mComponentName);

        if (action.equals("lock")) {
            if (isAdmin) {
                mDevicePolicyManager.resetPassword(password, 0);
                mDevicePolicyManager.lockNow();
                finish();
            }else{
                Log.d(TAG, "Not Registered as admin");
            }
        }else{
            mDevicePolicyManager.resetPassword(null, 0);
            finish();
        }

    }

    public void onExitClick(View view) {
        stopService(new Intent(this, LockService.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if((keyCode == KeyEvent.KEYCODE_HOME)){
            return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }
}
