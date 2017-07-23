package vchornenkyy.com.architecturetodo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.api.Status;

// This class handles Google Firebase Authentication and also saves the user details to Firebase
public class MainActivity extends AppCompatActivity {

    private FirebaseAuthHelper firebaseAuthHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Firebase.setAndroidContext(this);

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
        firebaseAuthHelper.onActivityResult(requestCode, resultCode, data, user -> {

            // Save Data to SharedPreference
            MainActivity context = MainActivity.this;
            SharedPrefManager sharedPrefManager = new SharedPrefManager(context);
            sharedPrefManager.saveIsLoggedIn(context, true);

            sharedPrefManager.saveEmail(context, user.getEmail());
            sharedPrefManager.saveName(context, user.getFullName());
            sharedPrefManager.savePhoto(context, user.getPhoto());

//            sharedPrefManager.saveToken(context, user.);
            //sharedPrefManager.saveIsLoggedIn(mContext, true);

            Toast.makeText(MainActivity.this, "Login successful:" + user.getEmail(), Toast.LENGTH_LONG).show();
        });
    }

    //This method creates a new user on our own Firebase database
    //after a successful Authentication on Firebase
    //It also saves the user info to SharedPreference
    private void createUserInFirebaseHelper(String email, final String name, final String photo) {

        //Since Firebase does not allow "." in the key name, we'll have to encode and change the "." to ","
        // using the encodeEmail method in class Utils
        final String encodedEmail = Utils.encodeEmail(email.toLowerCase());

        //create an object of Firebase database and pass the the Firebase URL
        final Firebase userLocation = new Firebase(Constants.FIREBASE_URL_USERS).child(encodedEmail);

        //Add a Listerner to that above location
        userLocation.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {

                    // Insert into Firebase database
//                    User newUser = new User(name, photo, encodedEmail, timestampJoined);
//                    userLocation.setValue(newUser);

                    Toast.makeText(MainActivity.this, "Account created!", Toast.LENGTH_SHORT).show();

                    // After saving data to Firebase, goto next activity
//                    Intent intent = new Intent(MainActivity.this, NavDrawerActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(intent);
//                    finish();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

//                Log.e(TAG, /*getString(R.string.log_error_occurred) +*/ firebaseError.getMessage());
                //hideProgressDialog();
                if (firebaseError.getCode() == FirebaseError.EMAIL_TAKEN) {
                } else {
                    Toast.makeText(MainActivity.this, firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
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
