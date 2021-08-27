package ar.com.strellis.ampflower.ui.home.favorites;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GridSpace extends RecyclerView.ItemDecoration
{
    private int spanCount;
    private int spacing;
    private boolean includeEdge;

    public GridSpace(int spanCount,int spacing,boolean includeEdge)
    {
        this.spanCount=spanCount;
        this.spacing=spacing;
        this.includeEdge=includeEdge;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position=parent.getChildAdapterPosition(view);
        int column=position % spanCount;
        if(includeEdge)
        {
            outRect.left=spacing-column*spacing/spanCount;
            outRect.right=(column+1)*spacing/spanCount;
            if(position<spanCount)
            {
                outRect.top=spacing;
            }
            outRect.bottom=spacing;
        }
        else
        {
            outRect.left=column*spacing/spanCount;
            outRect.right=spacing-(column+1)*spacing/spanCount;
            if(position>=spanCount)
            {
                outRect.top=spacing;
            }
        }
    }
}
