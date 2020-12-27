package com.punuo.sys.app.view.loading;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;

/**
 * Created by han.chen.
 * Date on 2019-06-03.
 **/
public class LoadingView extends FrameLayout {
    private static final int DEFAULT_COLOR = 0xFFF2C306;
    private CircleView mCircleView;
    public LoadingView(@NonNull Context context) {
        super(context);
    }

    public LoadingView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        final float defaultDiameter = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 44f, metrics);// 圆圈直径
        final float defaultStrokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, metrics);// 圆圈线条宽度

        LayoutParams circleLp = new LayoutParams((int) defaultDiameter, (int) defaultDiameter);
        circleLp.gravity = Gravity.CENTER;

        mCircleView = new CircleView(context);
        addView(mCircleView, circleLp);
        mCircleView.setColor(DEFAULT_COLOR);
        mCircleView.setDiameter(defaultDiameter);
        mCircleView.setStrokeWidth(defaultStrokeWidth);
    }
}
