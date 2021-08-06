package ar.com.strellis.ampflower.ui.utils;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

public abstract class ClickItemTouchListener implements RecyclerView.OnItemTouchListener {

    private final GestureDetector mGestureDetector;

    public ClickItemTouchListener(RecyclerView hostView) {
        mGestureDetector = new ItemClickGestureDetector(hostView.getContext(),
                new ItemClickGestureListener(hostView));
    }

    private boolean isAttachedToWindow(RecyclerView hostView) {
        return hostView.isAttachedToWindow();
    }

    private boolean hasAdapter(RecyclerView hostView) {
        return (hostView.getAdapter() != null);
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent event) {
        if (!isAttachedToWindow(recyclerView) || !hasAdapter(recyclerView)) {
            return false;
        }

        mGestureDetector.onTouchEvent(event);
        return false;
    }

    @Override
    public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent event) {

    }

    public abstract boolean onClick(RecyclerView parent, View view, int position, long id);

    public abstract boolean onLongClick(RecyclerView parent, View view, int position, long id);

    private class ItemClickGestureDetector extends GestureDetector {
        private final ItemClickGestureListener mGestureListener;

        public ItemClickGestureDetector(Context context, ItemClickGestureListener listener) {
            super(context, listener);
            mGestureListener = listener;
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            final boolean handled = super.onTouchEvent(event);

            final int action = event.getAction() & MotionEvent.ACTION_MASK;
            if (action == MotionEvent.ACTION_UP) {
                mGestureListener.dispatchSingleTapUpIfNeeded(event);
            }

            return handled;
        }
    }

    private class ItemClickGestureListener extends GestureDetector.SimpleOnGestureListener {
        private final RecyclerView mHostView;
        private View mTargetChild;

        public ItemClickGestureListener(RecyclerView hostView) {
            mHostView = hostView;
        }

        public void dispatchSingleTapUpIfNeeded(MotionEvent event) {

            if (mTargetChild != null) {
                onSingleTapUp(event);
            }
        }

        @Override
        public boolean onDown(MotionEvent event) {
            final int x = (int) event.getX();
            final int y = (int) event.getY();

            mTargetChild = mHostView.findChildViewUnder(x, y);
            return (mTargetChild != null);
        }

        @Override
        public void onShowPress(MotionEvent event) {
            if (mTargetChild != null) {
                mTargetChild.setPressed(true);
            }
        }

        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            boolean handled = false;

            if (mTargetChild != null) {
                mTargetChild.setPressed(false);

                final int position = mHostView.getChildAdapterPosition(mTargetChild);
                final long id = Objects.requireNonNull(mHostView.getAdapter()).getItemId(position);
                handled = onClick(mHostView, mTargetChild, position, id);

                mTargetChild = null;
            }

            return handled;
        }

        @Override
        public boolean onScroll(MotionEvent event, MotionEvent event2, float v, float v2) {
            if (mTargetChild != null) {
                mTargetChild.setPressed(false);
                mTargetChild = null;

                return true;
            }

            return false;
        }

        @Override
        public void onLongPress(MotionEvent event) {
            if (mTargetChild == null) {
                return;
            }

            final int position = mHostView.getChildAdapterPosition(mTargetChild);
            final long id = Objects.requireNonNull(mHostView.getAdapter()).getItemId(position);
            final boolean handled = onLongClick(mHostView, mTargetChild, position, id);

            if (handled) {
                mTargetChild.setPressed(false);
                mTargetChild = null;
            }
        }
    }
}
