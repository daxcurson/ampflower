package ar.com.strellis.ampflower.ui.home.albums;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.paging.LoadState;
import androidx.recyclerview.widget.RecyclerView;

import ar.com.strellis.ampflower.R;
import ar.com.strellis.ampflower.databinding.ListItemNetworkStateBinding;

public class LoadStateViewHolder extends RecyclerView.ViewHolder {
    // Define Progress bar
    private final ProgressBar mProgressBar;
    // Define error TextView
    private final TextView mErrorMsg;
    // Define Retry button
    private Button mRetry;

    public LoadStateViewHolder(
            @NonNull ViewGroup parent,
            @NonNull View.OnClickListener retryCallback) {
        super(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_network_state, parent, false));

        ListItemNetworkStateBinding binding = ListItemNetworkStateBinding.bind(itemView);
        mProgressBar = binding.networkStateItemProgressBar;
        mErrorMsg = binding.networkStateItemErrorMsg;
        //mRetry = binding.retryButton;
        //mRetry.setOnClickListener(retryCallback);
    }

    public void bind(LoadState loadState) {
        // Check load state
        if (loadState instanceof LoadState.Error) {
            // Get the error
            LoadState.Error loadStateError = (LoadState.Error) loadState;
            // Set text of Error message
            mErrorMsg.setText(loadStateError.getError().getLocalizedMessage());
        }
        // set visibility of widgets based on LoadState
        mProgressBar.setVisibility(loadState instanceof LoadState.Loading
                ? View.VISIBLE : View.GONE);
        //mRetry.setVisibility(loadState instanceof LoadState.Error
        //        ? View.VISIBLE : View.GONE);
        mErrorMsg.setVisibility(loadState instanceof LoadState.Error
                ? View.VISIBLE : View.GONE);
    }
}