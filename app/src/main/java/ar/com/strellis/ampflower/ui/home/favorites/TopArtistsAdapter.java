package ar.com.strellis.ampflower.ui.home.favorites;

import android.content.Context;
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
import ar.com.strellis.ampflower.data.model.Artist;

public class TopArtistsAdapter extends PagingDataAdapter<Artist,TopArtistsAdapter.ArtistViewHolder>
{
    private static final int LOADING_ITEM=0;
    private static final int ALBUM_ITEM=1;

    public TopArtistsAdapter()
    {
        super(callback_diff);
    }

    @NonNull
    @Override
    public TopArtistsAdapter.ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context=parent.getContext();
        LayoutInflater inflater=LayoutInflater.from(context);
        View itemView=inflater.inflate(R.layout.item_home_featured_artist,parent,false);
        return new TopArtistsAdapter.ArtistViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TopArtistsAdapter.ArtistViewHolder holder, int position) {
        Artist currentArtist=getItem(position);
        if(currentArtist!=null)
        {
            // Load the art
            Picasso.get().load(currentArtist.getArt())
                    .fit()
                    .into(holder.getImgAlbum());
            holder.getFeaturedArtistName().setText(currentArtist.getName());
        }
    }

    @Override
    public int getItemViewType(int position)
    {
        return position==getItemCount() ? ALBUM_ITEM : LOADING_ITEM;
    }
    public static class ArtistViewHolder extends RecyclerView.ViewHolder
    {
        private final ImageView imgAlbum;
        private final TextView featuredArtistName;

        public ArtistViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAlbum=itemView.findViewById(R.id.item_home_featured_img_artist);
            featuredArtistName=itemView.findViewById(R.id.item_home_top_artist_name);
        }
        public ImageView getImgAlbum()
        {
            return imgAlbum;
        }
        public TextView getFeaturedArtistName()
        {
            return featuredArtistName;
        }
    }
    public static DiffUtil.ItemCallback<Artist> callback_diff=new DiffUtil.ItemCallback<Artist>() {
        @Override
        public boolean areItemsTheSame(@NonNull Artist oldItem, @NonNull Artist newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Artist oldItem,@NonNull Artist newItem) {
            return oldItem.equals(newItem);
        }

    };
}
