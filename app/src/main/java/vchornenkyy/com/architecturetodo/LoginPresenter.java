package vchornenkyy.com.architecturetodo;


import vchornenkyy.com.core.FirebaseAuthHelper;
import vchornenkyy.com.core.IntentWrapper;
import vchornenkyy.com.core.Utils;

public class LoginPresenter {

    public interface View {
        void showMessage(String message);
    }

    private View view;

    private Utils utils;
    private FirebaseAuthHelper firebaseAuthHelper;

    public LoginPresenter(View view, Utils utils, FirebaseAuthHelper firebaseAuthHelper) {
        this.view = view;
        this.utils = utils;
        this.firebaseAuthHelper = firebaseAuthHelper;
    }

    public void onAttach(){
        firebaseAuthHelper.bind();
    }

    public void onDetach(){
        firebaseAuthHelper.unbind();
    }

    public void onGoogleSignInSelected() {
        if (utils.isNetworkAvailable()) {
            firebaseAuthHelper.signIn();
        } else {
            view.showMessage("Oops! no internet connection!");
        }
    }

    public void onGoogleSignOutSelected() {
        firebaseAuthHelper.signOut(data -> view.showMessage("Signed out"));
    }

    public void onGoogleSignInStateChanged(int requestCode, int resultCode, IntentWrapper intentWrapper) {
        firebaseAuthHelper.onActivityResult(requestCode, resultCode, intentWrapper,
                user -> view.showMessage("Login successful:" + user.getEmail()));
    }
}
