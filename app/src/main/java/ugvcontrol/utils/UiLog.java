package ugvcontrol.utils;

import android.app.Activity;
import android.widget.EditText;

public class UiLog {
    private static Activity _activity;
    private static EditText _editText;

    public static void init(Activity activity, EditText editText) {
        _activity = activity;
        _editText = editText;

    }

    public static void log(final String message) {
        _activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                _editText.append(message + "\n");
            }
        });
    }
}
