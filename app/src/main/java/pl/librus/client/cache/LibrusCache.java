package pl.librus.client.cache;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

import org.joda.time.LocalDateTime;

import java.io.IOException;

import pl.librus.client.BuildConfig;
import pl.librus.client.LibrusUtils;

/**
 * Created by szyme on 13.01.2017.
 */


/**
 * Abstract class for all cache types.
 */
public interface LibrusCache {


    /**
     * Set the period in which the data is still valid and can be used again.
     *
     * @return Expiration period, in milliseconds.
     */
    long getExpirationPeriod();

//    boolean isValid() {
//        if (version != BuildConfig.VERSION_CODE) {
//            Log.e(LibrusUtils.TAG, filename + " is not valid \n" +
//                    "cause: Version mismatch. cached version: " + version + " current version: " + BuildConfig.VERSION_CODE);
//            return false;
//        } else if (timestamp + getExpirationPeriod() < System.currentTimeMillis()) {
//            Log.e(LibrusUtils.TAG, filename + " is not valid \n" +
//                    "cause: Expired: " + new LocalDateTime(timestamp + getExpirationPeriod()).toString());
//            return false;
//        } else return true;
//    }
}
