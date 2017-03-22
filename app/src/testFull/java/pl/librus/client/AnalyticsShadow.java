package pl.librus.client;

import android.content.Context;

import com.google.firebase.analytics.FirebaseAnalytics;

import org.mockito.Mockito;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

@Implements(FirebaseAnalytics.class)
public class AnalyticsShadow {

    @Implementation
    public static FirebaseAnalytics getInstance(Context var0) {
        return Mockito.mock(FirebaseAnalytics.class);
    }

}
