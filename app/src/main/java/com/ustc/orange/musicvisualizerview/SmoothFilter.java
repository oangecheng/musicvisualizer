package com.ustc.orange.musicvisualizerview;

/**
 * 数据拟合平滑算法，这里只实现了三次五阶和三次七阶的算法
 * 参考资料，数学手册，手册链接地址 https://pan.baidu.com/s/1sbBUBtRK5K8jSmnOkuFavg
 * 第718页三次抛物线的滑动平均法
 * 数据波动幅度更加平滑
 *
 * Author: orange910617@gmail.com
 * date: 2018/11/27
 */
public class SmoothFilter {

  /**
   * 三次五阶平滑算法
   *
   * @param data 待处理的数据
   * @return 处理后得到的数据
   */
  public static float[] cubicSmooth5(float[] data) {
    if (data == null || data.length < 5) {
      return data;
    }
    int len = data.length;
    float[] out = new float[len];
    out[0] = (69 * data[0] + 4 * data[1] - 6 * data[2] + 4 * data[3] - data[4]) / 70;
    out[1] = (2 * data[0] + 27 * data[1] + 12 * data[2] - 8 * data[3] + 2 * data[4]) / 35;
    for (int i = 2; i <= len - 3; i++) {
      out[i] = (-3 * (data[i - 2] + data[i + 2]) + 12 * (data[i - 1] + data[i + 1]) + 17 * data[i]) / 35;
    }
    out[len - 2] = (2 * data[len - 5] - 8 * data[len - 4] + 12 * data[len - 3] + 27 * data[len - 2] + 2 * data[len - 1]) / 35;
    out[len - 1] = (-data[len - 5] + 4 * data[len - 4] - 6 * data[len - 3] + 4 * data[len - 2] + 69 * data[len - 1]) / 70;
    return out;
  }

  /**
   * 三次五阶平滑算法
   *
   * @param data 待处理的数据
   * @return 处理后得到的数据
   */
  public static float[] cubicSmooth7(float[] data) {
    if (data == null || data.length < 7) {
      return data;
    }
    int len = data.length;
    float[] out = new float[len];
    out[0] = (39 * data[0] + 8 * data[1] - 4 * data[2] - 4 * data[3] + 1 * data[4] + 4 * data[5] - 2 * data[6]) / 42;
    out[1] = (8 * data[0] + 19 * data[1] + 16 * data[2] + 6 * data[3] - 4 * data[4] - 7 * data[5] + 4 * data[6]) / 42;
    out[2] = (-4 * data[0] + 16 * data[1] + 19 * data[2] + 12 * data[3] + 2 * data[4] - 4 * data[5] + 1 * data[6]) / 42;
    for (int i = 3; i <= len - 4; i++) {
      out[i] = (-2 * (data[i - 3] + data[i + 3]) + 3 * (data[i - 2] + data[i + 2]) + 6 * (data[i - 1] + data[i + 1]) + 7 * data[i]) / 21;
    }
    out[len - 3] = (-4 * data[len - 1] + 16 * data[len - 2] + 19 * data[len - 3] + 12 * data[len - 4] + 2 * data[len - 5] - 4 * data[len - 6] + 1 * data[len - 7]) / 42;
    out[len - 2] = (8 * data[len - 1] + 19 * data[len - 2] + 16 * data[len - 3] + 6 * data[len - 4] - 4 * data[len - 5] - 7 * data[len - 6] + 4 * data[len - 7]) / 42;
    out[len - 1] = (39 * data[len - 1] + 8 * data[len - 2] - 4 * data[len - 3] - 4 * data[len - 4] + 1 * data[len - 5] + 4 * data[len - 6] - 2 * data[len - 7]) / 42;
    return out;
  }
}
