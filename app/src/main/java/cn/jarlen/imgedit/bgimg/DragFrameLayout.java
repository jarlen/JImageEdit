package cn.jarlen.imgedit.bgimg;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.customview.widget.ViewDragHelper;

import java.util.ArrayList;
import java.util.List;

public class DragFrameLayout extends FrameLayout {

    /**
     * The list of {@link View}s that will be draggable.
     */
    private List<View> mDragViews;

    /**
     * The {@link DragFrameLayoutController} that will be notify on drag.
     */
    private DragFrameLayoutController mDragFrameLayoutController;

    private ViewDragHelper mDragHelper;

    public DragFrameLayout(Context context) {
        this(context, null, 0, 0);
    }

    public DragFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0, 0);
    }

    public DragFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public DragFrameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mDragViews = new ArrayList<View>();

        /**
         * Create the {@link ViewDragHelper} and set its callback.
         */
        mDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                return mDragViews.contains(child);
            }

            @Override
            public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
                super.onViewPositionChanged(changedView, left, top, dx, dy);
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                return left;
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                return top;
            }

            @Override
            public void onViewCaptured(View capturedChild, int activePointerId) {
                super.onViewCaptured(capturedChild, activePointerId);
                if (mDragFrameLayoutController != null) {
                    mDragFrameLayoutController.onDragDrop(true);
                }
            }

            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                super.onViewReleased(releasedChild, xvel, yvel);
                if (mDragFrameLayoutController != null) {
                    mDragFrameLayoutController.onDragDrop(false);
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getActionMasked();
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mDragHelper.cancel();
            return false;
        }
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        mDragHelper.processTouchEvent(ev);
        return true;
    }

    /**
     * Adds a new {@link View} to the list of views that are draggable within the container.
     *
     * @param dragView the {@link View} to make draggable
     */
    public void addDragView(View dragView) {
        mDragViews.add(dragView);
    }

    /**
     * Sets the {@link DragFrameLayoutController} that will receive the drag events.
     *
     * @param dragFrameLayoutController a {@link DragFrameLayoutController}
     */
    public void setDragFrameController(DragFrameLayoutController dragFrameLayoutController) {
        mDragFrameLayoutController = dragFrameLayoutController;
    }

    /**
     * A controller that will receive the drag events.
     */
    public interface DragFrameLayoutController {

        public void onDragDrop(boolean captured);
    }
}