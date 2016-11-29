package pl.librus.client.announcements;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.widget.TextView;

import pl.librus.client.R;

/**
 * A transition that animates the elevation of a View from a given value down to zero.
 * <p>
 * Useful for creating parent↔child navigation transitions
 * (https://www.google.com/design/spec/patterns/navigational-transitions.html#navigational-transitions-parent-to-child)
 * when combined with a {@link android.transition.ChangeBounds} on a shared element.
 */
public class ItemDetailsTransition extends Transition {

    private static final String PROPNAME_ELEVATION = "liftoff:elevation";

    private static final String[] transitionProperties = {
            PROPNAME_ELEVATION,
    };

    private final float initialElevation;
    private final float finalElevation;
    private Context context;

    public ItemDetailsTransition(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        final TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ItemDetailsTransition);
        initialElevation = ta.getDimension(R.styleable.ItemDetailsTransition_initialElevation, 0f);
        finalElevation = ta.getDimension(R.styleable.ItemDetailsTransition_finalElevation, 0f);
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
        if (startValues != null && endValues != null) {
            //TODO Add workaround for no shadow bug
            final ViewGroupOverlay overlay = sceneRoot.getOverlay();
            final View view = endValues.view;
            final View info = endValues.view.findViewById(R.id.fragment_announcement_details_info);
            View bottomPanel = endValues.view.findViewById(R.id.fragment_announcement_details_bottom_panel);
            final TextView topPanel = (TextView) endValues.view.findViewById(R.id.fragment_announcement_details_top_panel);
//            Animator elevationAnimator = ObjectAnimator.ofFloat(view, View.TRANSLATION_Z, initialElevation, finalElevation);
//            elevationAnimator.setInterpolator(new DecelerateInterpolator());

//           TOP PANEL
            ValueAnimator text = ValueAnimator.ofFloat(14, 20);
            text.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    topPanel.setTextSize(TypedValue.COMPLEX_UNIT_SP, (Float) valueAnimator.getAnimatedValue());
                }
            });
            Animator top = ObjectAnimator.ofFloat(topPanel, "alpha", 0.5f, 1f);
            Animator bottom = ObjectAnimator.ofFloat(bottomPanel, "alpha", 0.5f, 1f);

            AnimatorSet set = new AnimatorSet();
            set.playTogether(bottom, top);
//            set.setDuration(3000);
            set.addListener(getOverlayListener(overlay, view));
            return set;
        } else {
            return null;
        }
    }

    private Animator.AnimatorListener getOverlayListener(final ViewGroupOverlay overlay, final View view) {

        final ViewGroup parent = (ViewGroup) view.getParent();

        return new Animator.AnimatorListener() {
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
        };
    }

    Animator disappear(View view) {
//        view.setClipToOutline(true);
//        Rect startClipBounds = view.getClipBounds();
//        Rect endClipBounds = new Rect(startClipBounds.left, startClipBounds.top, startClipBounds.right, startClipBounds.top);
//        return ObjectAnimat       or.ofObject(view, "clipBounds", new RectEvaluator(), startClipBounds, endClipBounds);
        return ObjectAnimator.ofFloat(view, "alpha", 0f);
    }
}