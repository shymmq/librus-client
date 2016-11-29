package pl.librus.client.ui;

import android.animation.Animator;
import android.animation.TimeInterpolator;

/**
 * Created by szyme on 27.11.2016.
 */

public class OverlayAnimator extends Animator {
    @Override
    public long getStartDelay() {
        return 0;
    }

    @Override
    public void setStartDelay(long l) {

    }

    @Override
    public Animator setDuration(long l) {
        return null;
    }

    @Override
    public long getDuration() {
        return 0;
    }

    @Override
    public void setInterpolator(TimeInterpolator timeInterpolator) {

    }

    @Override
    public boolean isRunning() {
        return false;
    }
}
