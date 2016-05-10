package com.android.eisenflow;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by Sve on 5/1/16.
 */
public class SwipeDetector implements View.OnTouchListener {
    private static final int MIN_LOCK_DISTANCE = 30; // disallow motion intercept
    private static final int MIN_DISTANCE = 550;
    private boolean motionInterceptDisallowed = false;
    private float downX, upX;
    private int pos;
    private TasksListHolder holder;
    private RecyclerView recyclerView;
    private int priority;
    private RelativeLayout currentMenuLayout;

    public SwipeDetector(TasksListHolder holder, RecyclerView recyclerView, int priority, int pos) {
        this.pos = pos;
        this.holder = holder;
        this.recyclerView = recyclerView;
        this.priority = priority;

        currentMenuLayout = getCorrectLayout();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                downX = event.getX();
                return true; // allow other events like Click to be processed
            }

            case MotionEvent.ACTION_MOVE: {
                upX = event.getX();
                float deltaX = downX - upX;

                if (deltaX < 0 && currentMenuLayout !=null && currentMenuLayout.getVisibility() == View.GONE) {
                    return true;
                }

                if (Math.abs(deltaX) > MIN_LOCK_DISTANCE && recyclerView != null && !motionInterceptDisallowed) {
                    recyclerView.requestDisallowInterceptTouchEvent(true);
                    motionInterceptDisallowed = true;
                }

                if (deltaX > 0) {
                    if(currentMenuLayout != null) {
                        currentMenuLayout.setVisibility(View.VISIBLE);
                    }
                }

                if (deltaX < 0 && currentMenuLayout!= null && currentMenuLayout.getVisibility() == View.VISIBLE) {
                        currentMenuLayout.setVisibility(View.GONE);
                }

                swipe(-(int) deltaX);
                return true;
            }

            case MotionEvent.ACTION_UP:
                upX = event.getX();
                float deltaX = upX - downX;

                if (Math.abs(deltaX) < MIN_DISTANCE) {
                    swipe(0);
                }

                if (recyclerView != null) {
                    recyclerView.requestDisallowInterceptTouchEvent(false);
                    motionInterceptDisallowed = false;
                }

                if (currentMenuLayout!= null && currentMenuLayout.getVisibility() != View.VISIBLE) {
                    swipe(0);
                }
                return true;

            case MotionEvent.ACTION_CANCEL:
                return true;
        }

        return true;
    }

    private void swipe(int distance) {
        View animationView = holder.mainLayout;
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) animationView.getLayoutParams();
        params.rightMargin = -distance;
        params.leftMargin = distance;
        animationView.setLayoutParams(params);
    }

    private RelativeLayout getCorrectLayout() {
        switch (priority) {
            case 0:
                return holder.priority_0_layout;
            case 1:
                return holder.priority_1_layout;
        }

        return null;
    }
}