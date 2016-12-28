package pl.librus.client.ui;

import pl.librus.client.api.LibrusData;

/**
 * Created by szyme on 05.12.2016.
 */

public interface MainFragment {
    void refresh(LibrusData cache);

    void setOnSetupCompleteListener(OnSetupCompleteListener listener);

    interface OnSetupCompleteListener {
        void onSetupComplete();
    }
}
