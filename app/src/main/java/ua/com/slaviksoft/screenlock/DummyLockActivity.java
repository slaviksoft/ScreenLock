package ua.com.slaviksoft.screenlock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Slavik on 28.07.2016.
 */
public class DummyLockActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startService(new Intent(this, LockService.class));
        finish();

    }
}
