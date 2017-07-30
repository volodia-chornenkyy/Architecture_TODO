package vchornenkyy.com.architecturetodo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import vchornenkyy.com.core.FirebaseAuthHelper;
import vchornenkyy.com.core.IntentWrapper;
import vchornenkyy.com.core.Utils;

public class LoginActivity extends AppCompatActivity implements LoginPresenter.View {

    private LoginPresenter loginPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginPresenter = new LoginPresenter(this,
                new Utils(this),
                new FirebaseAuthHelper(this));

        findViewById(R.id.login_with_google).setOnClickListener(
                view -> loginPresenter.onGoogleSignInSelected());

        findViewById(R.id.logout).setOnClickListener(view ->
                loginPresenter.onGoogleSignOutSelected());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loginPresenter.onGoogleSignInStateChanged(requestCode, resultCode, new IntentWrapper(data));
    }

    @Override
    protected void onStart() {
        super.onStart();
        loginPresenter.onAttach();
    }

    @Override
    protected void onStop() {
        super.onStop();
        loginPresenter.onDetach();
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
