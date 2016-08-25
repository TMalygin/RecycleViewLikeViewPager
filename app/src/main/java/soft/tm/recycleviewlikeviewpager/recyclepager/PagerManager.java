package soft.tm.recycleviewlikeviewpager.recyclepager;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by timofey.malygin on 25/08/16.
 */
public class PagerManager extends RecyclerView.LayoutManager {

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        final View view = recycler.getViewForPosition(0);
        addView(view);
        measureChildWithMargins(view, 0, 0);
        layoutDecorated(view, 0,0, getWidth(), getHeight());
    }
}
