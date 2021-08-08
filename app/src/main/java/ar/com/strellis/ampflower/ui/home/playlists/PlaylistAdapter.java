package ar.com.strellis.ampflower.ui.home.playlists;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

import ar.com.strellis.ampflower.R;
import ar.com.strellis.ampflower.data.model.NetworkState;
import ar.com.strellis.ampflower.data.model.Playlist;

public class PlaylistAdapter extends PagedListAdapter<Playlist, RecyclerView.ViewHolder> {
    private NetworkState networkState;

    public PlaylistAdapter() {
        super(PlaylistAdapter.callback_diff);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == R.layout.list_item_playlist) {
            View playlistItemView = inflater.inflate(R.layout.list_item_playlist, parent, false);
            return new PlaylistViewHolder(playlistItemView);
        } else if (viewType == R.layout.network_state_item) {
            View view = inflater.inflate(R.layout.network_state_item, parent, false);
            return new NetworkStateItemViewHolder(view);
        } else
            throw new IllegalArgumentException("Unknown view type");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == R.layout.list_item_playlist) {
            PlaylistViewHolder playlistViewHolder = (PlaylistViewHolder) holder;
            Playlist playlist= getItem(position);
            playlistViewHolder.getPlaylistName().setText(Objects.requireNonNull(playlist).getName());
            playlistViewHolder.getSongsQuantity().setText(String.valueOf(playlist.getItems()));
            // Now, attempt to download the image.
            //Picasso.get().load(album.getArt()).into(playlistViewHolder.getPlaylistImage());
        }
        if (getItemViewType(position) == R.layout.network_state_item) {
            ((NetworkStateItemViewHolder) holder).bindView(networkState);
        }
    }

    public static DiffUtil.ItemCallback<Playlist> callback_diff = new DiffUtil.ItemCallback<Playlist>() {
        @Override
        public boolean areItemsTheSame(@NonNull Playlist oldItem, @NonNull Playlist newItem) {
            Log.d("DEBUG", "Callback comparing item " + oldItem.getId() + " with item " + newItem.getId());
            return oldItem.getId()==newItem.getId() && oldItem.getPlaylistName().equals(newItem.getPlaylistName());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Playlist oldItem, @NonNull Playlist newItem) {
            Log.d("DEBUG", "Callback comparing content of item " + oldItem.getId() + " with item " + newItem.getId());
            return oldItem.equals(newItem);
        }

    };

    private boolean hasExtraRow() {
        return networkState != null && networkState != NetworkState.LOADED;
    }

    @Override
    public int getItemViewType(int position) {
        if (hasExtraRow() && position == getItemCount() - 1) {
            return R.layout.network_state_item;
        } else {
            return R.layout.list_item_playlist;
        }
    }

    public void setNetworkState(NetworkState newNetworkState) {
        NetworkState previousState = this.networkState;
        boolean previousExtraRow = hasExtraRow();
        this.networkState = newNetworkState;
        boolean newExtraRow = hasExtraRow();
        if (previousExtraRow != newExtraRow) {
            if (previousExtraRow) {
                notifyItemRemoved(getItemCount());
            } else {
                notifyItemInserted(getItemCount());
            }
        } else if (newExtraRow && previousState != newNetworkState) {
            notifyItemChanged(getItemCount() - 1);
        }
    }
}
