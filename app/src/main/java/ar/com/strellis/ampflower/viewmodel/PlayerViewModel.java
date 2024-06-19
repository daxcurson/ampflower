package ar.com.strellis.ampflower.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.media3.common.MediaItem;
import androidx.media3.common.util.UnstableApi;
import androidx.paging.ExperimentalPagingApi;


import ar.com.strellis.ampflower.MainActivity;

@UnstableApi
@ExperimentalPagingApi
public class PlayerViewModel extends ViewModel
{
    private final MutableLiveData<MainActivity.State> playerState;
    private final MutableLiveData<MediaItem> mediaItem;
    private final MutableLiveData<Long> currentPosition;

    public PlayerViewModel()
    {
        playerState=new MutableLiveData<>();
        mediaItem=new MutableLiveData<>();
        currentPosition=new MutableLiveData<>();
    }
    public void setPlayerState(MainActivity.State state)
    {
        playerState.setValue(state);
    }
    public MutableLiveData<MainActivity.State> getPlayerState()
    {
        return playerState;
    }
    public void setMediaItem(MediaItem item)
    {
        this.mediaItem.setValue(item);
    }
    public MutableLiveData<MediaItem> getMediaItem()
    {
        return this.mediaItem;
    }
    public void setCurrentPosition(long position)
    {
        currentPosition.setValue(position);
    }
    public MutableLiveData<Long> getCurrentPosition() {
        return currentPosition;
    }
}
