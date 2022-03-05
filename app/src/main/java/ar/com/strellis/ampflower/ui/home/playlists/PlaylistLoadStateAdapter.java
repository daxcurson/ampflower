package ar.com.strellis.ampflower.ui.home.playlists;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.paging.LoadState;
import androidx.paging.LoadStateAdapter;
import androidx.recyclerview.widget.RecyclerView;

import ar.com.strellis.ampflower.R;
import ar.com.strellis.ampflower.databinding.NetworkStateItemBinding;
import ar.com.strellis.ampflower.ui.home.albums.AlbumLoadStateAdapter;

public class PlaylistLoadStateAdapter extends LoadStateAdapter<PlaylistLoadStateAdapter.LoadStateViewHolder>
{
    @Override
    public void onBindViewHolder(@NonNull PlaylistLoadStateAdapter.LoadStateViewHolder networkStateItemViewHolder, @NonNull LoadState loadState) {
        networkStateItemViewHolder.bind(loadState);
    }

    @NonNull
    @Override
    public PlaylistLoadStateAdapter.LoadStateViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, @NonNull LoadState loadState) {
        return new PlaylistLoadStateAdapter.LoadStateViewHolder(viewGroup,null);
    }
    public static class LoadStateViewHolder extends RecyclerView.ViewHolder {
        // Define Progress bar
        private final ProgressBar mProgressBar;
        // Define error TextView
        private final TextView mErrorMsg;
        // Define Retry button
        private Button mRetry;

        LoadStateViewHolder(
                @NonNull ViewGroup parent,
                @NonNull View.OnClickListener retryCallback) {
            super(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.network_state_item, parent, false));
            NetworkStateItemBinding binding = NetworkStateItemBinding.bind(itemView);
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
}
