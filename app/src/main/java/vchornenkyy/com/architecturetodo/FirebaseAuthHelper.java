package vchornenkyy.com.architecturetodo;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;

public class FirebaseAuthHelper {

    private static final String TAG = FirebaseAuthHelper.class.getName();

    private GoogleAuthHelper googleAuthHelper;

    private String idToken;
    private String name, email;
    private String photo;
    private Uri photoUri;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public FirebaseAuthHelper(FragmentActivity fragmentActivity) {
        this(fragmentActivity, new GoogleAuthHelper(fragmentActivity));

        mAuth = FirebaseAuth.getInstance();
    }

    public FirebaseAuthHelper(FragmentActivity fragmentActivity, GoogleAuthHelper googleAuthHelper) {
        this.googleAuthHelper = googleAuthHelper;

        //this is where we start the Auth state Listener to listen for whether the user is signed in or not
        mAuthListener = firebaseAuth -> {
            // Get signedIn user
            FirebaseUser user = firebaseAuth.getCurrentUser();

            //if user is signed in, we call a helper method to save the user details to Firebase
            if (user != null) {
                // User is signed in
//                createUserInFirebaseHelper("email", "name", "photo");
                Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
            } else {
                // User is signed out
                Log.d(TAG, "onAuthStateChanged:signed_out");
            }
        };
    }

    public void bind() {
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().signOut();
        }
        mAuth.addAuthStateListener(mAuthListener);
    }

    public void unbind() {
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data, Action<User> successAction) {
        googleAuthHelper.onActivityResult(requestCode, resultCode, data, account -> {
            idToken = account.getIdToken();

            name = account.getDisplayName();
            email = account.getEmail();
            photoUri = account.getPhotoUrl();
            photo = photoUri.toString();

            /* Set raw version of date to the ServerValue.TIMESTAMP value and save into dateCreatedMap */
            HashMap<String, Object> timestampJoined = new HashMap<>();
            timestampJoined.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

            User newUser = new User(name, photo, Utils.encodeEmail(email.toLowerCase()), timestampJoined);

            AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(task -> {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential" + task.getException().getMessage());
                            task.getException().printStackTrace();
                        } else {
                            successAction.call(newUser);
                        }
//                        hideProgressDialog();
                    });


        });
    }

    public void signIn() {
        googleAuthHelper.signIn();
    }

    public void signOut(Action<Status> result) {
        googleAuthHelper.signOut(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                result.call(status);
            }
        });
    }
}
