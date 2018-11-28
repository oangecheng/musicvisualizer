package com.ustc.orange.musicvisualizerview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * 矩形的频谱图，矩形的宽度设置小一点比较美观
 */
public class BarVisualizerView extends BaseVisualizerView {

  public BarVisualizerView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  protected void init(Context context) {
    mSpaceWidth = dip2px(context, 1f);
    mLineWidth = dip2px(context, 2);
    mBaseHeight = dip2px(context, 5);
    mPaint.setStrokeWidth(mLineWidth);
    mPaint.setAntiAlias(true);
    mPaint.setStrokeCap(Paint.Cap.ROUND);
    mPaint.setColor(context.getResources().getColor(R.color.white));
  }

  @Override
  protected void smoothFilter() {
    mData = SmoothFilter.cubicSmooth7(mData);
  }

  @Override
  protected void updateCoordinate() {
    if (mData == null) {
      return;
    }
    if (mPoints == null || mPoints.length < mDataNum * 4) {
      mPoints = new float[mDataNum * 4];
    }
    final int baseX = getWidth() / mDataNum;
    final int baseLine = getHeight();

    for (int i = 0; i < mDataNum; i++) {
      final int xi = baseX * i + baseX;
      mPoints[i * 4] = xi;
      mPoints[i * 4 + 2] = xi;
      float offset = mData[i] * 1.2f + mBaseHeight;
      mPoints[i * 4 + 1] = baseLine + offset;
      mPoints[i * 4 + 3] = baseLine - offset;
    }
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    if (mPoints != null) {
      canvas.drawLines(mPoints, mPaint);
    }
  }
}
