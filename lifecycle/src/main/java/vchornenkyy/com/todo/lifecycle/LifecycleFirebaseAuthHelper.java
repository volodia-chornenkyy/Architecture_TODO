package vchornenkyy.com.todo.lifecycle;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.support.v4.app.FragmentActivity;

import vchornenkyy.com.core.FirebaseAuthHelper;
import vchornenkyy.com.core.GoogleAuthHelper;

public class LifecycleFirebaseAuthHelper extends FirebaseAuthHelper implements LifecycleObserver {

    public LifecycleFirebaseAuthHelper(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public LifecycleFirebaseAuthHelper(GoogleAuthHelper googleAuthHelper) {
        super(googleAuthHelper);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    void start() {
        bind();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    void stop() {
        unbind();
    }
}
