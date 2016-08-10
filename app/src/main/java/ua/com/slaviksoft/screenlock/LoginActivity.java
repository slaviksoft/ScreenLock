package ua.com.slaviksoft.screenlock;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    final String TAG = "DEBUG:" + getClass().getSimpleName();

    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewInfo;
    private TextView textViewAdmin;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;

    private static final int ADMIN_INTENT = 1;
    private DevicePolicyManager mDevicePolicyManager;
    private ComponentName mComponentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        textViewInfo = (TextView) findViewById(R.id.textViewInfo);
        textViewAdmin = (TextView) findViewById(R.id.textViewAdmin);

        Firebase.setAndroidContext(this);
        mAuth = FirebaseAuth.getInstance();

        mDevicePolicyManager = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        mComponentName = new ComponentName(this, Admin.class);

        updateAdminState();

    }

    public void onSignUp(View view) {
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();
        signUp(email, password);
    }

    public void onSignIn(View view) {
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();
        signIn(email, password);
    }

    private void signIn(String email, final String password){

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                        if (task.isSuccessful()) {
                            updateToken();
                        }else{
                            textViewInfo.setText("Authentication failed. " + task.getException());
                        }


                    }
                });

    }

    private void signUp(final String email, final String password){

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                        if (task.isSuccessful()) {
                            signIn(email, password);
                            updateToken();
                            //textViewInfo.setText("Authentication succes. Login now.");
                        }else{
                            textViewInfo.setText("Authentication failed. " + task.getException());
                        }
                    }
                });
    }

    private void updateToken(){

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();
        String userId = mAuth.getCurrentUser().getUid();
        String token = FirebaseInstanceId.getInstance().getToken();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("token", token);
        ref.child("users").child(userId).setValue(map);// updateChildren(map);

        textViewInfo.setText("Application registered on server.");

    }


    private void updateAdminState(){

        if (mDevicePolicyManager.isAdminActive(mComponentName))
            textViewAdmin.setText("Admin active");
        else
            textViewAdmin.setText("Admin not active");
    }


    public void onSetAdminClick(View view) {
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mComponentName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Administrator description");
        startActivityForResult(intent, ADMIN_INTENT);
    }

    public void onUnsetAdminClick(View view) {
        mDevicePolicyManager.removeActiveAdmin(mComponentName);
        updateAdminState();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADMIN_INTENT) {
            updateAdminState();
        }
    }
}
