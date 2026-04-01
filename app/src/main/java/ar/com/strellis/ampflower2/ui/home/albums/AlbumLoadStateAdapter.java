package ar.com.strellis.ampflower2.ui.home.albums;

import android.view.ViewGroup;

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
