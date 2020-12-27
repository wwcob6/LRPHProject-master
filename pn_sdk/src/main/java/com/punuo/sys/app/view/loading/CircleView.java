package com.punuo.sys.app.view.loading;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * LoadingView外层的圆圈.
 * <p>
 * 说明：
 * 1、我们假设圆弧是由a,b两个点的轨迹构成，那么绘画圆弧分2个过程。
 * 2、a,b都从12点到12点，a结束后b开始，形态不同，每个过程是总时间的一半。
 * 3、整体顺时针旋转，总时间刚好是a,b之和。
 *
 */
public class CircleView extends View {

    private static final int ANIMATOR_DURATION = 2000;

    /**
     * 圆弧起始位置，单位为度。这里需要从12点开始绘制。
     * <p>
     * The arc is drawn clockwise. An angle of 0 degrees correspond to the
     * geometric angle of 0 degrees (3 o'clock on a watch.)
     */
    private static final float START_ANGLE = -90f;
    private static final float ANGLE_MAX = 360f;
    private static final float ANGLE_MIN = 0f;

    private Paint mPaint;
    private RectF mRectF;
    private ObjectAnimator mRotationAnim;
    private ValueAnimator mArcAnimator;

    /**
     * 圆弧扫过的角度，顺时针方向，单位为度，从右中间开始为零度。
     */
    private float mSweepAngle;

    private float mDiameter;
    private float mStrokeWidth;

    /**
     * 绘画圆弧分2个过程，这2个圆弧都从12点到12点，形态不同，每个过程是总时间的一半。
     */
    private boolean mArcRepeatFirst = true;
    private boolean mAnimationStarted = false;

    public CircleView(Context context) {
        this(context, null);
    }

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
    }

    private void initRectF(int width, int height) {
        if (mRectF != null) {
            return;
        }
        if (mDiameter == 0) {
            mDiameter = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 44f, getResources().getDisplayMetrics());
        }
        if (mStrokeWidth == 0) {
            mStrokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, getResources().getDisplayMetrics());
        }
        float r = mDiameter / 2 - mStrokeWidth;
        if (width > 0 && height > 0) {
            mRectF = new RectF(width / 2 - r, height / 2 - r, width / 2 + r, height / 2 + r);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0) {
            initRectF(w, h);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 防止没有在onSizeChanged()被初始化(基本上不可能，只是不放心...)
        initRectF(getWidth(), getHeight());

        if (mArcRepeatFirst) {
            canvas.drawArc(mRectF, START_ANGLE, mSweepAngle, false, mPaint);
        } else {
            canvas.drawArc(mRectF, START_ANGLE, -(ANGLE_MAX - mSweepAngle), false, mPaint);
        }
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE) {
            start();
        } else {
            stop();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stop();
    }

    public void setColor(@ColorInt int color) {
        mPaint.setColor(color);
    }

    public void setStrokeWidth(float strokeWidth) {
        mPaint.setStrokeWidth(strokeWidth);
        mStrokeWidth = strokeWidth;
    }

    public void setDiameter(float diameter) {
        mDiameter = diameter;
    }

    void initAnimation() {
        // 自身旋转动画
        mRotationAnim = ObjectAnimator.ofFloat(this, "rotation", ANGLE_MIN, ANGLE_MAX);
        mRotationAnim.setDuration(ANIMATOR_DURATION);
        mRotationAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        mRotationAnim.setRepeatCount(ValueAnimator.INFINITE);
        // 圆弧动画
        mArcAnimator = ValueAnimator.ofFloat(ANGLE_MIN, ANGLE_MAX);
        mArcAnimator.setDuration(ANIMATOR_DURATION / 2);
        mArcAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mArcAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mArcAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                mSweepAngle = 0;
                mArcRepeatFirst = !mArcRepeatFirst;
            }
        });
        mArcAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                postInvalidate();
                mSweepAngle = (float) animation.getAnimatedValue();
            }
        });
    }

    void start() {
        if (mAnimationStarted) {
            return;
        }
        mAnimationStarted = true;
        if (mRotationAnim == null || mArcAnimator == null) {
            initAnimation();
        }
        mRotationAnim.start();
        mArcAnimator.start();
    }

    void stop() {
        try {
            if (mRotationAnim != null) {
                mRotationAnim.cancel();
                mRotationAnim = null;
            }
            if (mArcAnimator != null) {
                mArcAnimator.cancel();
                mArcAnimator = null;
            }
            mAnimationStarted = false;
        } catch (Exception ignore) {
        }
    }

}
