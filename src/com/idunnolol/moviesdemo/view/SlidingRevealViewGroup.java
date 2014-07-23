package com.idunnolol.moviesdemo.view;

import com.idunnolol.moviesdemo.ui.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;


/**
 * Holds two views which "slide" open
 *
 * It is constructed via three view:
 * 0 - A Spacer.  This represents the space the cover takes up when slid out.
 * 1 - The sliding view.  This hides itself.
 * 2 - The cover view.  This is always visible.
 */
public class SlidingRevealViewGroup extends RelativeLayout {

	public enum Reveal {
		LEFT,
		RIGHT
	}

	// Which side to reveal
	private Reveal mReveal;

	// Represents how far in we are sliding; 0% == totally revealed, 100% == totally hidden
	private float mRevealPercent;

	// Internal Views
	private View mSpaceView;
	private View mSlidingView;
	private View mCoverView;
	private View mSlideOverlayLeft;
	private View mSlideOverlayMiddle;
	private View mSlideOverlayRight;

	public SlidingRevealViewGroup(Context context) {
		this(context, null);
	}

	public SlidingRevealViewGroup(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SlidingRevealViewGroup(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);

		if (changed) {
			updateSlide();
		}
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		mSpaceView = getChildAt(0);
		mSlidingView = getChildAt(1);
		mCoverView = getChildAt(2);

		mSlideOverlayLeft = findViewById(R.id.slide_overlay_left);
		mSlideOverlayMiddle = findViewById(R.id.slide_overlay_middle);
		mSlideOverlayRight = findViewById(R.id.slide_overlay_right);
	}

	public void setReveal(Reveal reveal) {
		mReveal = reveal;
	}

	public void setRevealPercent(float revealPercent) {
		mRevealPercent = revealPercent;

		updateSlide();
	}

	public float getRevealPercent() {
		return mRevealPercent;
	}

	public float getSlideRevealX() {
		return mSlidingView.getWidth() - Math.abs(mSlidingView.getTranslationX())
				- (mCoverView.getWidth() - mSpaceView.getWidth());
	}

	public float getSlideHideX() {
		return getWidth() - mCoverView.getWidth();
	}

	private void updateSlide() {
		int width = getWidth();
		if (width == 0) {
			// We haven't measured yet; wait until measurement to slide
			return;
		}

		// Hide part of the cover, based on the spacer
		int widthDiff = mCoverView.getWidth() - mSpaceView.getWidth();
		float coverTransX = mRevealPercent * widthDiff;
		mCoverView.setTranslationX(mReveal == Reveal.RIGHT ? -coverTransX : coverTransX);

		// Slide to reveal the View
		//
		// We need to take into account the cover sliding as well, so that the moment
		// you start sliding over, the cover doesn't keep hiding things
		float coverLeftoverX = (1 - mRevealPercent) * widthDiff;
		float translationX = (1 - mRevealPercent) * mSlidingView.getWidth() - coverLeftoverX;
		mSlidingView.setTranslationX(mReveal == Reveal.RIGHT ? -translationX : translationX);

		// Slide the middle so that its right edge is underneath what is revealed
		// Slide the right so that it's always on the right corner
		// TODO: This doesn't work for Reveal.LEFT
		mSlideOverlayMiddle.setTranslationX(-translationX);
		mSlideOverlayRight.setTranslationX(-translationX);

		onUpdateSlide();
	}

	protected void onUpdateSlide() {
		// For subclasses to implement if necessary
	}

	public void setUseHardwareLayers(boolean useHardwareLayers) {
		// For subclasses to initialize hardware layers during animations

		int toLayerType = useHardwareLayers ? View.LAYER_TYPE_HARDWARE : View.LAYER_TYPE_NONE;
		mSlideOverlayLeft.setLayerType(toLayerType, null);
		mSlideOverlayMiddle.setLayerType(toLayerType, null);
		mSlideOverlayRight.setLayerType(toLayerType, null);
	}
}
