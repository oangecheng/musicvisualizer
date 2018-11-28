package com.ustc.orange.musicvisualizerview;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * 实现了比较平滑的频谱图
 */
public abstract class BaseVisualizerView extends View {

  protected int mLineWidth; // 频谱线条宽度
  protected Paint mPaint = new Paint();
  protected int mSpaceWidth; // 空隙宽度
  protected int mBaseHeight; // 基础高度
  protected int mFilterSize = 6; // 窗口大小，用于取平均值，默认为6
  protected float[] mData;// 绘制频谱所用的数据集合
  protected float[] mPoints; // 绘制线条的点集合
  protected int mDataNum; // 采样数据的个数

  public BaseVisualizerView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    init(context);
  }

  public void update(byte[] fft) {
    if (mDataNum == 0) {
      mDataNum = (getWidth() + mLineWidth) / (mSpaceWidth + mLineWidth);
    }
    if (mDataNum <= 0) {
      return;
    }
    transform(fft);
    averageFilter(30, 10);
    smoothFilter();
    updateCoordinate();
    invalidate();
  }

  /** 初始化画笔，线条宽度等 **/
  protected abstract void init(Context context);

  /** 拟合算法平滑处理 **/
  protected abstract void smoothFilter();

  /** 更新坐标 **/
  protected abstract void updateCoordinate();

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    int widthMode = MeasureSpec.getMode(widthMeasureSpec);
    int widthSize = MeasureSpec.getSize(widthMeasureSpec);

    int heightMode = MeasureSpec.getMode(heightMeasureSpec);
    int heightSize = MeasureSpec.getSize(heightMeasureSpec);

    // 设置wrap_content的默认宽 / 高值
    int mWidth = dip2px(getContext(), 200);
    int mHeight = dip2px(getContext(), 100);

    if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
      setMeasuredDimension(mWidth, mHeight);
    } else if (widthMode == MeasureSpec.AT_MOST) {
      setMeasuredDimension(mWidth, heightSize);
    } else if (heightMode == MeasureSpec.AT_MOST) {
      setMeasuredDimension(widthSize, mHeight);
    }
  }

  /** 将采集的fft数据转换成所需的数据 **/
  private void transform(byte[] fft) {
    int length = mDataNum + mFilterSize;
    if (mData == null) {
      mData = new float[length];
    }
    int index = 0;
    while (index < length) {
      if (fft.length < index + 2) {
        break;
      }
      mData[index] = (float) Math.abs(Math.hypot(fft[index], fft[index + 1]));
      index++;
    }
    // 这里是因为fft数据在最开始波动最大，因此将采样后的数据向后偏移1/4的长度
    // 避免大部分波峰在起始位置，仅仅为了美观，此段代码可去
    index = length / 4;
    float[] temp = new float[index];
    System.arraycopy(mData, length - index - 1, temp, 0, index);
    System.arraycopy(mData, 0, mData, index - 1, length - index);
    System.arraycopy(temp, 0, mData, 0, index);
  }

  /**
   * 将相邻数据的差值缩小到一定范围，具体参数可调
   *
   * @param maxOffset 超过这个值时（默认30），需要将相邻数据的差值缩小到offset
   * @param offset 缩小到指定范围（默认为10）
   */
  protected void averageFilter(int maxOffset, int offset) {
    if (mData == null) {
      return;
    }
    int averageIndex = 0;
    int filterIndex = 0;
    int len = mData.length;
    float[] filterBuffer = new float[mFilterSize];

    for (int i = 0; i < len - 2; i++) {
      float offset1 = Math.abs(mData[i] - mData[i + 1]);
      if (offset1 > maxOffset) {
        if (mData[i] > mData[i + 1]) {
          mData[i + 1] = mData[i] - offset;
        } else {
          mData[i] = mData[i + 1] - offset;
        }
      }
      float offset2 = Math.abs(mData[len - i - 1] - mData[len - i - 2]);
      if (offset2 > maxOffset) {
        if (mData[len - i - 1] > mData[len - i - 2]) {
          mData[len - i - 2] = mData[len - i - 1] - offset;
        } else {
          mData[len - i - 1] = mData[len - i - 2] - offset;
        }
      }
    }

    for (int i = 0; i < len; i++) {
      if ((mFilterSize + i) > len) {
        break;
      } else {
        for (int j = i; j < (mFilterSize + i); j++) {
          filterBuffer[filterIndex] = mData[j];
          filterIndex++;
        }
        mData[averageIndex] = average(filterBuffer);
        averageIndex++;
        filterIndex = 0;
      }
    }
  }

  protected int dip2px(Context context, float dpValue) {
    final float scale = context.getResources().getDisplayMetrics().density;
    return (int) (dpValue * scale + 0.5f);
  }

  //计算数组平均值
  private float average(float[] array) {
    long sum = 0;
    for (float i : array) {
      sum += i;
    }
    return sum / array.length;
  }
}
