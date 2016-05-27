package rnp.ezvent;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

public class RV extends RecyclerView {

    private OnScrollListener listener;

    public RV(Context context) {
        super(context);
    }

    public RV(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RV(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
/*
    @Override
    public void setOnScrollListener(OnScrollListener listener) {
        super.setOnScrollListener(listener);
        this.listener = listener;
    }
*/
    @Override
    public void addOnScrollListener(OnScrollListener listener) {
        super.addOnScrollListener(listener);
        this.listener = listener;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int preFirstChildPosition = getChildCount() != 0 ? getChildViewHolder(getChildAt(0)).getPosition() : -1;
        int preLastChildPosition = getChildCount() != 0 ? getChildViewHolder(getChildAt(getChildCount() - 1)).getPosition() : -1;
        super.onLayout(changed, l, t, r, b);
        int postFirstChildPosition = getChildCount() != 0 ? getChildViewHolder(getChildAt(0)).getPosition() : -1;
        int postLastChildPosition = getChildCount() != 0 ? getChildViewHolder(getChildAt(getChildCount() - 1)).getPosition() : -1;

        // TODO: Insert proper DX and DY values
    //    if (preFirstChildPosition != postFirstChildPosition || preLastChildPosition != postLastChildPosition)
            listener.onScrolled(this, 0, 1000);

    }
}
