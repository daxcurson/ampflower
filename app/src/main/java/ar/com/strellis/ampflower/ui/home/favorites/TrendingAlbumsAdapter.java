package ar.com.strellis.ampflower.ui.home.favorites;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import ar.com.strellis.ampflower.R;
import ar.com.strellis.ampflower.data.model.Album;

public class TrendingAlbumsAdapter extends PagingDataAdapter<Album,TrendingAlbumsAdapter.AlbumViewHolder>
{
    private static final int LOADING_ITEM=0;
    private static final int ALBUM_ITEM=1;

    public TrendingAlbumsAdapter()
    {
        super(callback_diff);
    }

    @NonNull
    @Override
    public TrendingAlbumsAdapter.AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context=parent.getContext();
        LayoutInflater inflater=LayoutInflater.from(context);
        View itemView=inflater.inflate(R.layout.item_home_featured_album,parent,false);
        return new TrendingAlbumsAdapter.AlbumViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TrendingAlbumsAdapter.AlbumViewHolder holder, int position) {
        Album currentAlbum=getItem(position);
        if(currentAlbum!=null)
        {
            // Load the art
            Picasso.get().load(currentAlbum.getArt())
                    .fit()
                    .into(holder.getImgAlbum());
            holder.getFeaturedAlbumTitle().setText(currentAlbum.getName());
            holder.getFeaturedArtistName().setText(currentAlbum.getArtist().getName());
        }
    }

    @Override
    public int getItemViewType(int position)
    {
        return position==getItemCount() ? ALBUM_ITEM : LOADING_ITEM;
    }
    public static class AlbumViewHolder extends RecyclerView.ViewHolder
    {
        private final ImageView imgAlbum;
        private final TextView featuredAlbumTitle;
        private final TextView featuredArtistName;

        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAlbum=itemView.findViewById(R.id.item_home_featured_img_album);
            featuredAlbumTitle=itemView.findViewById(R.id.item_home_featured_album_title);
            featuredArtistName=itemView.findViewById(R.id.item_home_featured_album_artist);
        }
        public ImageView getImgAlbum()
        {
            return imgAlbum;
        }
        public TextView getFeaturedAlbumTitle()
        {
            return featuredAlbumTitle;
        }
        public TextView getFeaturedArtistName()
        {
            return featuredArtistName;
        }
    }
    public static DiffUtil.ItemCallback<Album> callback_diff=new DiffUtil.ItemCallback<Album>() {
        @Override
        public boolean areItemsTheSame(@NonNull Album oldItem, @NonNull Album newItem) {
            Log.d("TrendingAlbumsAdapter","Callback comparing item "+oldItem.getId()+" with item "+newItem.getId());
            return oldItem.getId().equals(newItem.getId()) && oldItem.getArtist().getId().equals(newItem.getArtist().getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Album oldItem,@NonNull Album newItem) {
            Log.d("TrendingAlbumsAdapter","Callback comparing content of item "+oldItem.getId()+" with item "+newItem.getId());
            return oldItem.equals(newItem);
        }

    };
}
