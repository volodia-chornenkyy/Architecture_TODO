package vchornenkyy.com.architecturetodo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;

// This class handles Google Firebase Authentication and also saves the user details to Firebase
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private GoogleAuthHelper googleAuthHelper;

    private String idToken;
    private String name, email;
    private String photo;
    private Uri photoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Firebase.setAndroidContext(this);

        findViewById(R.id.login_with_google).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (new Utils(MainActivity.this).isNetworkAvailable()) {
                    googleAuthHelper.signIn();
                } else {
                    Toast.makeText(MainActivity.this, "Oops! no internet connection!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleAuthHelper.signOut(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        status.getStatus();
                    }
                });
            }
        });

        googleAuthHelper = new GoogleAuthHelper(this);

        mAuth = com.google.firebase.auth.FirebaseAuth.getInstance();

        //this is where we start the Auth state Listener to listen for whether the user is signed in or not
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // Get signedIn user
                FirebaseUser user = firebaseAuth.getCurrentUser();

                //if user is signed in, we call a helper method to save the user details to Firebase
                if (user != null) {
                    // User is signed in
                    createUserInFirebaseHelper("email", "name", "photo");
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        googleAuthHelper.onActivityResult(requestCode, resultCode, data, new Action<GoogleSignInAccount>() {
            @Override
            public void call(GoogleSignInAccount account) {
                idToken = account.getIdToken();

                name = account.getDisplayName();
                email = account.getEmail();
                photoUri = account.getPhotoUrl();
                photo = photoUri.toString();

                // Save Data to SharedPreference
//                sharedPrefManager = new SharedPrefManager(mContext);
//                sharedPrefManager.saveIsLoggedIn(mContext, true);
//
//                sharedPrefManager.saveEmail(mContext, email);
//                sharedPrefManager.saveName(mContext, name);
//                sharedPrefManager.savePhoto(mContext, photo);
//
//                sharedPrefManager.saveToken(mContext, idToken);
                //sharedPrefManager.saveIsLoggedIn(mContext, true);

                AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
                firebaseAuthWithGoogle(credential);
            }
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
                    /* Set raw version of date to the ServerValue.TIMESTAMP value and save into dateCreatedMap */
                    HashMap<String, Object> timestampJoined = new HashMap<>();
                    timestampJoined.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

                    // Insert into Firebase database
                    User newUser = new User(name, photo, encodedEmail, timestampJoined);
                    userLocation.setValue(newUser);

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

                Log.e(TAG, /*getString(R.string.log_error_occurred) +*/ firebaseError.getMessage());
                //hideProgressDialog();
                if (firebaseError.getCode() == FirebaseError.EMAIL_TAKEN) {
                } else {
                    Toast.makeText(MainActivity.this, firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //After a successful sign into Google, this method now authenticates the user with Firebase
    private void firebaseAuthWithGoogle(AuthCredential credential) {
//        showProgressDialog();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential" + task.getException().getMessage());
                            task.getException().printStackTrace();
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            createUserInFirebaseHelper("email", "name", "photo");
                            Toast.makeText(MainActivity.this, "Login successful",
                                    Toast.LENGTH_SHORT).show();
                        }
//                        hideProgressDialog();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().signOut();
        }
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
