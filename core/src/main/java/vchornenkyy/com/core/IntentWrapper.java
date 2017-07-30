package vchornenkyy.com.core;

import android.content.Intent;

public class IntentWrapper {

    private Intent intent;

    public IntentWrapper(Intent intent) {
        this.intent = intent;
    }

    public Intent getIntent() {
        return intent;
    }
}
