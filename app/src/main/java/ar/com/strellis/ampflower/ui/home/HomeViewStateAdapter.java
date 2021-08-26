package ar.com.strellis.ampflower.ui.home;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import org.jetbrains.annotations.NotNull;

import ar.com.strellis.ampflower.ui.home.favorites.FavoritesFragment;

public class HomeViewStateAdapter extends FragmentStateAdapter {

    private FavoritesFragment favoritesFragment;
    private AlbumsFragment albumsFragment;
    private ArtistsFragment artistsFragment;
    private PlaylistsFragment playlistsFragment;

    public HomeViewStateAdapter(@NonNull @NotNull Fragment fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int position) {
        switch(position)
        {
            default:
            case 0:
                if(favoritesFragment==null)
                    favoritesFragment=new FavoritesFragment();
                return favoritesFragment;
            case 1:
                if(albumsFragment==null)
                    albumsFragment=new AlbumsFragment();
                return albumsFragment;
            case 2:
                if(artistsFragment==null)
                    artistsFragment=new ArtistsFragment();
                return artistsFragment;
            case 3:
                if(playlistsFragment==null)
                    playlistsFragment=new PlaylistsFragment();
                return playlistsFragment;
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
