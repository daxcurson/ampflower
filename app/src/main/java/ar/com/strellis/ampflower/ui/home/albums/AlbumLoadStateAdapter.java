package ar.com.strellis.ampflower.ui.home.albums;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.paging.LoadState;
import androidx.paging.LoadStateAdapter;

public class AlbumLoadStateAdapter extends LoadStateAdapter<LoadStateViewHolder>
{
    @Override
    public void onBindViewHolder(@NonNull LoadStateViewHolder networkStateItemViewHolder, @NonNull LoadState loadState) {
        networkStateItemViewHolder.bind(loadState);
    }

    @NonNull
    @Override
    public LoadStateViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, @NonNull LoadState loadState) {
        return new LoadStateViewHolder(viewGroup, null);
    }
}
