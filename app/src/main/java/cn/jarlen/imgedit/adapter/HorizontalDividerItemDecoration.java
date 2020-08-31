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
public class HorizontalDividerItemDecoration extends FlexibleDividerDecoration {
    private MarginProvider mMarginProvider;
    private int mFirstDividerLeftMargin;
    private int mFirstDividerRightMargin;

    protected HorizontalDividerItemDecoration(Builder builder) {
        super(builder);
        mMarginProvider = builder.mMarginProvider;
        mFirstDividerLeftMargin = builder.mFirstDividerLeftMargin;
        mFirstDividerRightMargin = builder.mFirstDividerRightMargin;
    }

    @Override
    protected Rect getDividerBound(int position, RecyclerView parent, View child) {
        Rect bounds = new Rect(0, 0, 0, 0);
        int transitionX = (int) ViewCompat.getTranslationX(child);
        int transitionY = (int) ViewCompat.getTranslationY(child);
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
        bounds.left = parent.getPaddingLeft() +
                mMarginProvider.dividerLeftMargin(position, parent) + transitionX;
        bounds.right = parent.getWidth() - parent.getPaddingRight() -
                mMarginProvider.dividerRightMargin(position, parent) + transitionX;
        int dividerSize = getDividerSize(position, parent);
        boolean isReverseLayout = isReverseLayout(parent);
        if (mDividerType == DividerType.DRAWABLE) {
            // set top and bottom position of divider
            if (isReverseLayout) {
                bounds.bottom = child.getTop() - params.topMargin + transitionY;
                bounds.top = bounds.bottom - dividerSize;
            } else {
                bounds.top = child.getBottom() + params.bottomMargin + transitionY;
                bounds.bottom = bounds.top + dividerSize;
            }
        } else {
            // set center point of divider
            int halfSize = dividerSize / 2;
            if (isReverseLayout) {
                bounds.top = child.getTop() - params.topMargin - halfSize + transitionY;
            } /*else if (mShowFirstDivider && position == -1) {
                bounds.top = child.getTop() - params.topMargin - halfSize + transitionY;
            }*/ else {
                bounds.top = child.getBottom() + params.bottomMargin + halfSize + transitionY;
            }
            bounds.bottom = bounds.top;
        }
        if (mPositionInsideItem) {
            if (isReverseLayout) {
                bounds.top += dividerSize;
                bounds.bottom += dividerSize;
            } else {
                bounds.top -= dividerSize;
                bounds.bottom -= dividerSize;
            }
        }
        return bounds;
    }

    @Override
    protected Rect getDividerFirstBound(int position, RecyclerView parent, View child) {
        Rect bounds = new Rect(0, 0, 0, 0);
        int transitionX = (int) ViewCompat.getTranslationX(child);
        int transitionY = (int) ViewCompat.getTranslationY(child);
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
        bounds.left = parent.getPaddingLeft() +
                mFirstDividerLeftMargin + transitionX;
        bounds.right = parent.getWidth() - parent.getPaddingRight() -
                mFirstDividerRightMargin + transitionX;
        int dividerSize = mFirstDividerSize;
        if (mDividerType == DividerType.DRAWABLE) {
            // set top and bottom position of divider
            bounds.bottom = child.getTop() - params.topMargin + transitionY;
            bounds.top = bounds.bottom - dividerSize;
        } else {
            // set center point of divider
            int halfSize = dividerSize / 2;
            bounds.top = child.getTop() - params.topMargin - halfSize + transitionY;
            bounds.bottom = bounds.top;
        }
        if (mPositionInsideItem) {
            bounds.top += dividerSize;
            bounds.bottom += dividerSize;
        }
        return bounds;
    }

    @Override
    protected void setItemOffsets(Rect outRect, int position, RecyclerView parent) {
        if (mPositionInsideItem) {
            outRect.set(0, 0, 0, 0);
            return;
        }
        if (mShowFirstDivider && position == 0 && parent.getAdapter().getItemCount() == 1) {
            //只有一个item时
            outRect.set(0, mFirstDividerSize, 0, 0);
            return;
        }
        if (mShowFirstDivider && position == 0) {
            outRect.set(0, mFirstDividerSize, 0, getDividerSize(position, parent));
            return;
        }
        if (isReverseLayout(parent)) {
            outRect.set(0, getDividerSize(position, parent), 0, 0);
        } else {
            outRect.set(0, 0, 0, getDividerSize(position, parent));
        }
    }

    private int getDividerSize(int position, RecyclerView parent) {
        if (mPaintProvider != null) {
            return (int) mPaintProvider.dividerPaint(position, parent).getStrokeWidth();
        } else if (mSizeProvider != null) {
            return mSizeProvider.dividerSize(position, parent);
        } else if (mDrawableProvider != null) {
            Drawable drawable = mDrawableProvider.drawableProvider(position, parent);
            return drawable.getIntrinsicHeight();
        }
        throw new RuntimeException("failed to get size");
    }

    /**
     * Interface for controlling divider margin
     */
    public interface MarginProvider {
        /**
         * Returns left margin of divider.
         *
         * @param position Divider position (or group index for GridLayoutManager)
         * @param parent   RecyclerView
         * @return left margin
         */
        int dividerLeftMargin(int position, RecyclerView parent);

        /**
         * Returns right margin of divider.
         *
         * @param position Divider position (or group index for GridLayoutManager)
         * @param parent   RecyclerView
         * @return right margin
         */
        int dividerRightMargin(int position, RecyclerView parent);
    }

    public static class Builder extends FlexibleDividerDecoration.Builder<Builder> {
        private int mFirstDividerLeftMargin;
        private int mFirstDividerRightMargin;
        private MarginProvider mMarginProvider = new MarginProvider() {
            @Override
            public int dividerLeftMargin(int position, RecyclerView parent) {
                return 0;
            }

            @Override
            public int dividerRightMargin(int position, RecyclerView parent) {
                return 0;
            }
        };

        public Builder(Context context) {
            super(context);
        }

        public Builder margin(final int leftMargin, final int rightMargin) {
            return marginProvider(new MarginProvider() {
                @Override
                public int dividerLeftMargin(int position, RecyclerView parent) {
                    return leftMargin;
                }

                @Override
                public int dividerRightMargin(int position, RecyclerView parent) {
                    return rightMargin;
                }
            });
        }

        public Builder showFirstDivider(@Dimension int dividerSize, @ColorInt int color, @Dimension int leftMargin, @Dimension int rightMargin) {
            mFirstDividerSize = dividerSize;
            mFirstDividerColor = color;
            this.mFirstDividerLeftMargin = leftMargin;
            this.mFirstDividerRightMargin = rightMargin;
            mShowFirstDivider = true;
            return this;
        }

        public Builder margin(int horizontalMargin) {
            return margin(horizontalMargin, horizontalMargin);
        }

        public Builder marginResId(@DimenRes int leftMarginId, @DimenRes int rightMarginId) {
            return margin(mResources.getDimensionPixelSize(leftMarginId),
                    mResources.getDimensionPixelSize(rightMarginId));
        }

        public Builder marginResId(@DimenRes int horizontalMarginId) {
            return marginResId(horizontalMarginId, horizontalMarginId);
        }

        public Builder marginProvider(MarginProvider provider) {
            mMarginProvider = provider;
            return this;
        }

        public HorizontalDividerItemDecoration build() {
            checkBuilderParams();
            return new HorizontalDividerItemDecoration(this);
        }
    }
}