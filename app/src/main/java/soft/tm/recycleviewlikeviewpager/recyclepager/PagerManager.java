package soft.tm.recycleviewlikeviewpager.recyclepager;

import android.graphics.Rect;
import android.support.annotation.FloatRange;
import android.support.annotation.IntDef;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by timofey.malygin on 25/08/16.
 */
public class PagerManager extends RecyclerView.LayoutManager {

    @IntDef({HORIZONTAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Orientation {
    }

    public static final int HORIZONTAL = 0;

    @Orientation
    private int orientation = HORIZONTAL;
    @FloatRange(from = 0.8f, to = 1.0f)
    private float scaleFactor = 0.9f;

    private int firstItemLeft, lastItemRight;

    public void setOrientation(@Orientation int orientation) {
        if (this.orientation == orientation) return;
        this.orientation = orientation;
        requestLayout();
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        detachAndScrapAttachedViews(recycler);
        if (orientation == HORIZONTAL) fillHorizontal(recycler);
    }

    private void fillHorizontal(RecyclerView.Recycler recycler) {
        int pos = 0;
        int width = getWidth();
        int viewLeft = -1;
        int itemCount = getItemCount();
        int viewWidth = (int) (width * scaleFactor);
        final int widthSpec = View.MeasureSpec.makeMeasureSpec(getWidth(), View.MeasureSpec.EXACTLY);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(getHeight(), View.MeasureSpec.EXACTLY);

        while (viewLeft <= firstItemLeft + 2 * viewWidth && pos < itemCount) {
            View view = recycler.getViewForPosition(pos);
            addView(view);
            measureChildWithDecorationsAndMargin(view, widthSpec, heightSpec);
            int decoratedMeasuredHeight = getDecoratedMeasuredHeight(view);
            if (viewLeft == -1) {
                firstItemLeft = (width - viewWidth) / 2;
                viewLeft = firstItemLeft;
            }

            layoutDecorated(view, viewLeft, 0, viewLeft + viewWidth, decoratedMeasuredHeight);
            viewLeft = getDecoratedRight(view);
            pos++;
        }
        lastItemRight = firstItemLeft + viewWidth;
    }

    private void measureChildWithDecorationsAndMargin(View child, int widthSpec, int heightSpec) {
        //TODO: fix measured for width
        Rect decorRect = new Rect();
        calculateItemDecorationsForChild(child, decorRect);
        RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) child.getLayoutParams();
        widthSpec = updateSpecWithExtra(widthSpec, lp.leftMargin + decorRect.left,
                lp.rightMargin + decorRect.right);
        heightSpec = updateSpecWithExtra(heightSpec, lp.topMargin + decorRect.top,
                lp.bottomMargin + decorRect.bottom);
        child.measure(widthSpec, heightSpec);
    }

    private int updateSpecWithExtra(int spec, int startInset, int endInset) {
        if (startInset == 0 && endInset == 0) {
            return spec;
        }
        final int mode = View.MeasureSpec.getMode(spec);
        if (mode == View.MeasureSpec.AT_MOST || mode == View.MeasureSpec.EXACTLY) {
            return View.MeasureSpec.makeMeasureSpec(
                    View.MeasureSpec.getSize(spec) - startInset - endInset, mode);
        }
        return spec;
    }


    @Override
    public boolean canScrollHorizontally() {
        return true;
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getChildCount() <= 1) return 0;
        dx = scrollHorizontal(dx);

        offsetChildrenHorizontal(-dx);

        removeInvisibleHorizontal(recycler);
        addNewItemsHorizontal(recycler);
        return dx;
    }

    private int scrollHorizontal(int dx) {
        final View firstView = getChildAt(0);
        final View lastView = getChildAt(getChildCount() - 1);

        if (getPosition(firstView) == 0) {
            final int decoratedLeft = getDecoratedLeft(firstView);
            int diff = firstItemLeft - decoratedLeft;
            if (diff < -dx) dx = -diff;
        } else if (getPosition(lastView) == getItemCount() - 1) {
            final int decorationRight = getDecoratedRight(lastView);
            int diff = decorationRight - lastItemRight;
            if (dx > diff) dx = diff;
        }
        return dx;
    }

    private void removeInvisibleHorizontal(RecyclerView.Recycler recycler) {
        final int width = getWidth();
        final int itemWidth = (int) (width * scaleFactor);
        for (int i = getChildCount() - 1; i >= 0; i--) {
            View child = getChildAt(i);
            int left = getDecoratedLeft(child);

            if (left > firstItemLeft + 2 * itemWidth) {
                detachView(child);
                recycler.recycleView(child);
                continue;
            }

            int right = getDecoratedRight(child);
            if (right < -itemWidth + firstItemLeft) {
                detachView(child);
                recycler.recycleView(child);
            }
        }
    }

    private void addNewItemsHorizontal(RecyclerView.Recycler recycler) {
        final int widthSpec = View.MeasureSpec.makeMeasureSpec(getWidth(), View.MeasureSpec.EXACTLY);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(getHeight(), View.MeasureSpec.EXACTLY);
        final int viewWidth = (int) (getWidth() * scaleFactor);

        // add to left
        final View firstChild = getChildAt(0);
        int firstPosition = getPosition(firstChild);
        if (firstPosition > 0) {
            int viewRight = getDecoratedLeft(firstChild);
            int pos = firstPosition - 1;
            final int leftBorder = firstItemLeft - viewWidth;
            while (viewRight > leftBorder && pos >= 0) {
                View view = recycler.getViewForPosition(pos);
                addView(view, 0);
                measureChildWithDecorationsAndMargin(view, widthSpec, heightSpec);
                int decoratedMeasuredHeight = getDecoratedMeasuredHeight(view);
                layoutDecorated(view, viewRight - viewWidth, 0, viewRight, decoratedMeasuredHeight);
                viewRight = getDecoratedLeft(view);
                pos--;
            }
        }

        // add to right
        View lastView = getChildAt(getChildCount() - 1);
        int lastPosition = getPosition(lastView);
        final int itemCount = getItemCount();
        if (lastPosition < itemCount - 1) {
            int viewLeft = getDecoratedRight(lastView);
            int pos = lastPosition + 1;
            final int rightBorder = firstItemLeft + 2 * viewWidth;
            while (viewLeft < rightBorder && pos < itemCount) {
                View view = recycler.getViewForPosition(pos);
                addView(view);
                measureChildWithDecorationsAndMargin(view, widthSpec, heightSpec);
                int decoratedMeasuredHeight = getDecoratedMeasuredHeight(view);
                layoutDecorated(view, viewLeft, 0, viewLeft + viewWidth, decoratedMeasuredHeight);
                viewLeft = getDecoratedRight(view);
                pos++;
            }
        }
    }
}
