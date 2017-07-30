package vchornenkyy.com.architecturetodo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;

import vchornenkyy.com.core.FirebaseAuthHelper;
import vchornenkyy.com.core.IntentWrapper;
import vchornenkyy.com.core.Utils;

// This class handles Google Firebase Authentication and also saves the user details to Firebase
public class MainActivity extends AppCompatActivity {

    private FirebaseAuthHelper firebaseAuthHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuthHelper = new FirebaseAuthHelper(this);

        findViewById(R.id.login_with_google).setOnClickListener(view -> {
            if (new Utils(MainActivity.this).isNetworkAvailable()) {
                firebaseAuthHelper.signIn();
            } else {
                Toast.makeText(MainActivity.this, "Oops! no internet connection!", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.logout).setOnClickListener(view ->
                firebaseAuthHelper.signOut(Status::getStatus));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        firebaseAuthHelper.onActivityResult(requestCode, resultCode, new IntentWrapper(data),
                user -> Toast.makeText(MainActivity.this, "Login successful:" + user.getEmail(), Toast.LENGTH_LONG).show());
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuthHelper.bind();
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuthHelper.unbind();
    }
}
