package com.ustc.orange.musicvisualizerview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * 线条频谱图
 */
public class LineVisualizerView extends BaseVisualizerView {

  private Path mPath = new Path();

  public LineVisualizerView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  protected void init(Context context) {
    mSpaceWidth = dip2px(context, 1);
    mLineWidth = dip2px(context, 2);
    mBaseHeight = dip2px(context, 10);
    mPaint.setStrokeWidth(mLineWidth);
    mPaint.setAntiAlias(true);
    mPaint.setColor(context.getResources().getColor(R.color.white));
    mPaint.setStyle(Paint.Style.STROKE);
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
      mPoints = new float[mDataNum * 2];
    }
    final int baseX = getWidth() / mDataNum;
    final int baseLine = getHeight();

    for (int i = 0; i < mDataNum; i++) {
      final int xi = baseX * i + baseX;
      mPoints[i * 2] = xi;
      float offset = mData[i] * 1.2f + mBaseHeight;
      mPoints[i * 2 + 1] = baseLine - offset;
    }
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    if (mPoints != null) {
      mPath.reset();
      mPath.moveTo(mPoints[0], mPoints[1]);
      for (int i = 0; i < mDataNum; i++) {
        mPath.lineTo(mPoints[2 * i], mPoints[2 * i + 1]);
      }
      canvas.drawPath(mPath, mPaint);
    }
  }
}
