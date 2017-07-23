package vchornenkyy.com.architecturetodo.mvp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import vchornenkyy.com.architecturetodo.FirebaseAuthHelper;
import vchornenkyy.com.architecturetodo.IntentWrapper;
import vchornenkyy.com.architecturetodo.R;
import vchornenkyy.com.architecturetodo.Utils;

public class MvpLoginActivity extends AppCompatActivity implements LoginPresenter.View {

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
        Toast.makeText(MvpLoginActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
