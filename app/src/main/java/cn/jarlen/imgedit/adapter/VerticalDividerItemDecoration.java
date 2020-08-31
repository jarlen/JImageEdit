package cn.jarlen.imgedit.adapter;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.DimenRes;
import androidx.annotation.Dimension;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by hjl on 2017/5/8.
 */

public class VerticalDividerItemDecoration extends FlexibleDividerDecoration {

    private MarginProvider mMarginProvider;
    private int mFirstDividerTopMargin;
    private int mFirstDividerBottomMargin;

    protected VerticalDividerItemDecoration(Builder builder) {
        super(builder);
        mMarginProvider = builder.mMarginProvider;
        mFirstDividerTopMargin = builder.mFirstDividerTopMargin;
        mFirstDividerBottomMargin = builder.mFirstDividerBottomMargin;
    }

    @Override
    protected Rect getDividerBound(int position, RecyclerView parent, View child) {
        Rect bounds = new Rect(0, 0, 0, 0);
        int transitionX = (int) ViewCompat.getTranslationX(child);
        int transitionY = (int) ViewCompat.getTranslationY(child);
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
        bounds.top = parent.getPaddingTop() +
                mMarginProvider.dividerTopMargin(position, parent) + transitionY;
        bounds.bottom = parent.getHeight() - parent.getPaddingBottom() -
                mMarginProvider.dividerBottomMargin(position, parent) + transitionY;

        int dividerSize = getDividerSize(position, parent);
        boolean isReverseLayout = isReverseLayout(parent);
        if (mDividerType == DividerType.DRAWABLE) {
            // set left and right position of divider
            if (isReverseLayout) {
                bounds.right = child.getLeft() - params.leftMargin + transitionX;
                bounds.left = bounds.right - dividerSize;
            } else {
                bounds.left = child.getRight() + params.rightMargin + transitionX;
                bounds.right = bounds.left + dividerSize;
            }
        } else {
            // set center point of divider
            int halfSize = dividerSize / 2;
            if (isReverseLayout) {
                bounds.left = child.getLeft() - params.leftMargin - halfSize + transitionX;
            } else {
                bounds.left = child.getRight() + params.rightMargin + halfSize + transitionX;
            }
            bounds.right = bounds.left;
        }

        if (mPositionInsideItem) {
            if (isReverseLayout) {
                bounds.left += dividerSize;
                bounds.right += dividerSize;
            } else {
                bounds.left -= dividerSize;
                bounds.right -= dividerSize;
            }
        }

        return bounds;
    }

    @Override
    protected void setItemOffsets(Rect outRect, int position, RecyclerView parent) {
        if (mPositionInsideItem) {
            outRect.set(0, 0, 0, 0);
            return;
        }
        if (mShowFirstDivider && position == 0) {
            outRect.set(mFirstDividerSize, 0, getDividerSize(position, parent), 0);
            return;
        }
        if (isReverseLayout(parent)) {
            outRect.set(getDividerSize(position, parent), 0, 0, 0);
        } else {
            outRect.set(0, 0, getDividerSize(position, parent), 0);
        }
    }

    private int getDividerSize(int position, RecyclerView parent) {
        if (mPaintProvider != null) {
            return (int) mPaintProvider.dividerPaint(position, parent).getStrokeWidth();
        } else if (mSizeProvider != null) {
            return mSizeProvider.dividerSize(position, parent);
        } else if (mDrawableProvider != null) {
            Drawable drawable = mDrawableProvider.drawableProvider(position, parent);
            return drawable.getIntrinsicWidth();
        }
        throw new RuntimeException("failed to get size");
    }

    @Override
    protected Rect getDividerFirstBound(int position, RecyclerView parent, View child) {
        Rect bounds = new Rect(0, 0, 0, 0);
        int transitionX = (int) ViewCompat.getTranslationX(child);
        int transitionY = (int) ViewCompat.getTranslationY(child);
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
        bounds.top = parent.getPaddingTop() +
                mFirstDividerTopMargin + transitionY;
        bounds.bottom = parent.getHeight() - parent.getPaddingBottom() -
                mFirstDividerBottomMargin + transitionY;

        int dividerSize = mFirstDividerSize;
        if (mDividerType == DividerType.DRAWABLE) {
            bounds.right = child.getLeft() - params.leftMargin + transitionX;
            bounds.left = bounds.right - dividerSize;
        } else {
            // set center point of divider
            int halfSize = dividerSize / 2;
            bounds.left = child.getLeft() - params.leftMargin - halfSize + transitionX;
            bounds.right = bounds.left;
        }

        if (mPositionInsideItem) {
            bounds.left += dividerSize;
            bounds.right += dividerSize;
        }
        return bounds;
    }

    /**
     * Interface for controlling divider margin
     */
    public interface MarginProvider {

        /**
         * Returns top margin of divider.
         *
         * @param position Divider position (or group index for GridLayoutManager)
         * @param parent   RecyclerView
         * @return top margin
         */
        int dividerTopMargin(int position, RecyclerView parent);

        /**
         * Returns bottom margin of divider.
         *
         * @param position Divider position (or group index for GridLayoutManager)
         * @param parent   RecyclerView
         * @return bottom margin
         */
        int dividerBottomMargin(int position, RecyclerView parent);
    }

    public static class Builder extends FlexibleDividerDecoration.Builder<Builder> {

        private MarginProvider mMarginProvider = new MarginProvider() {
            @Override
            public int dividerTopMargin(int position, RecyclerView parent) {
                return 0;
            }

            @Override
            public int dividerBottomMargin(int position, RecyclerView parent) {
                return 0;
            }
        };
        private int mFirstDividerTopMargin;
        private int mFirstDividerBottomMargin;

        public Builder(Context context) {
            super(context);
        }

        public Builder margin(final int topMargin, final int bottomMargin) {
            return marginProvider(new MarginProvider() {
                @Override
                public int dividerTopMargin(int position, RecyclerView parent) {
                    return topMargin;
                }

                @Override
                public int dividerBottomMargin(int position, RecyclerView parent) {
                    return bottomMargin;
                }
            });
        }

        public Builder margin(int verticalMargin) {
            return margin(verticalMargin, verticalMargin);
        }

        public Builder marginResId(@DimenRes int topMarginId, @DimenRes int bottomMarginId) {
            return margin(mResources.getDimensionPixelSize(topMarginId),
                    mResources.getDimensionPixelSize(bottomMarginId));
        }

        public Builder showFirstDivider(@Dimension int dividerSize, @ColorInt int color, @Dimension int topMargin, @Dimension int bottomMargin) {
            super.mFirstDividerSize = dividerSize;
            super.mFirstDividerColor = color;
            this.mFirstDividerTopMargin = topMargin;
            this.mFirstDividerBottomMargin = bottomMargin;
            super.mShowFirstDivider = true;
            return this;
        }

        public Builder marginResId(@DimenRes int verticalMarginId) {
            return marginResId(verticalMarginId, verticalMarginId);
        }

        public Builder marginProvider(MarginProvider provider) {
            mMarginProvider = provider;
            return this;
        }

        public VerticalDividerItemDecoration build() {
            checkBuilderParams();
            return new VerticalDividerItemDecoration(this);
        }
    }
}