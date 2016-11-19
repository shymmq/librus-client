package pl.librus.client;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.view.animation.DecelerateInterpolator;

/**
 * A transition that animates the elevation of a View from a given value down to zero.
 * <p>
 * Useful for creating parent↔child navigation transitions
 * (https://www.google.com/design/spec/patterns/navigational-transitions.html#navigational-transitions-parent-to-child)
 * when combined with a {@link android.transition.ChangeBounds} on a shared element.
 */
public class LiftOff extends Transition {

    private static final String PROPNAME_ELEVATION = "liftoff:elevation";

    private static final String[] transitionProperties = {
            PROPNAME_ELEVATION,
    };

    private final float initialElevation;
    private final float finalElevation;

    public LiftOff(Context context, AttributeSet attrs) {
        super(context, attrs);
        final TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.LiftOff);
        initialElevation = ta.getDimension(R.styleable.LiftOff_initialElevation, 0f);
        finalElevation = ta.getDimension(R.styleable.LiftOff_finalElevation, 0f);
        ta.recycle();
    }

    @Override
    public String[] getTransitionProperties() {
        return transitionProperties;
    }

    @Override
    public void captureStartValues(TransitionValues transitionValues) {
        transitionValues.values.put(PROPNAME_ELEVATION, initialElevation);
    }

    @Override
    public void captureEndValues(TransitionValues transitionValues) {
        transitionValues.values.put(PROPNAME_ELEVATION, finalElevation);
//        transitionValues.values.put(PROPNAME_HEIGHT, transitionValues.view.getHeight());
    }

    @Override
    public Animator createAnimator(final ViewGroup sceneRoot, TransitionValues startValues,
                                   final TransitionValues endValues) {
        final ViewGroupOverlay overlay = sceneRoot.getOverlay();
        final View view = endValues.view;
        final ViewGroup parent = (ViewGroup) view.getParent();
        overlay.add(view);
        Animator elevationAnimator = ObjectAnimator.ofFloat(view, View.TRANSLATION_Z,
                initialElevation, finalElevation);
        elevationAnimator.setInterpolator(new DecelerateInterpolator());
        elevationAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                overlay.add(view);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                overlay.remove(view);
                parent.addView(view);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                overlay.remove(view);
                parent.addView(view);
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        return elevationAnimator;
    }
}