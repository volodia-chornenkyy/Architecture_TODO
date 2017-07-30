package vchornenkyy.com.architecturetodo.arch_comp;

import android.arch.lifecycle.LifecycleActivity;
import android.arch.lifecycle.LifecycleRegistry;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;

import vchornenkyy.com.architecturetodo.R;
import vchornenkyy.com.core.IntentWrapper;
import vchornenkyy.com.core.Utils;

public class LifecycleLoginActivity extends LifecycleActivity {
//public class LifecycleLoginActivity extends AppCompatActivity implements LifecycleRegistryOwner {

    private LifecycleFirebaseAuthHelper firebaseAuthHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuthHelper = new LifecycleFirebaseAuthHelper(this);

        findViewById(R.id.login_with_google).setOnClickListener(view -> {
            if (new Utils(LifecycleLoginActivity.this).isNetworkAvailable()) {
                firebaseAuthHelper.signIn();
            } else {
                Toast.makeText(LifecycleLoginActivity.this, "Oops! no internet connection!", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.logout).setOnClickListener(view ->
                firebaseAuthHelper.signOut(Status::getStatus));

        getLifecycle().addObserver(firebaseAuthHelper);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        firebaseAuthHelper.onActivityResult(requestCode, resultCode, new IntentWrapper(data),
                user -> Toast.makeText(LifecycleLoginActivity.this, "Login successful:" + user.getEmail(), Toast.LENGTH_LONG).show());
    }



    LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);

    @Override
    public LifecycleRegistry getLifecycle() {
        return lifecycleRegistry;
    }
}
