package com.stynet.widget.helper;

import android.os.SystemClock;

/**
 * Created by xx shuiDianBing, 2018/09/29-10:24:10:24.Refer to the website: nullptr
 **/

public class HorizontalScroller {
    private int startX;
    private int startY;
    private int distanceX;
    private int distanceY;

    private int currentX;
    private int currentY;

    private long startTime;
    private long duration = 1000L;

    private boolean isFinish;

    /**
     *
     * @param scrollX
     *            x坐标
     * @param scrollY
     *            y坐标
     * @param distanceX
     *            X方向移动的距离
     * @param distanceY
     *            y方向移动的距离
     */
    public void startScroll(int scrollX, int scrollY, int distanceX, int distanceY) {
        startX = scrollX;
        startY = scrollY;
        this.distanceX = distanceX;
        this.distanceY = distanceY;
        isFinish = false;
        startTime = SystemClock.uptimeMillis();
    }

    /**
     * 计算偏移量，
     *
     * @return true 还在移动 false：移动已经停止
     */
    public boolean computeScrollOffset() {
        if (isFinish) {
            return false;
        }

        long timePassed = SystemClock.uptimeMillis() - startTime;

        if (timePassed < duration) {

            currentX = (int) (startX + distanceX * timePassed / duration);
            currentY = (int) (startY + distanceY * timePassed / duration);

            System.out.println("currentX:::" + currentX);
        } else if (timePassed >= duration) {
            currentX = startX + distanceX;
            currentY = startY + distanceY;
            isFinish = true;
        }

        return true;
    }

    public int getCurrX() {
        return currentX;
    }

    public void setCurrentX(int currentX) {
        this.currentX = currentX;
    }

    public int getCurrentY() {
        return currentY;
    }

    public void setCurrentY(int currentY) {
        this.currentY = currentY;
    }
}
