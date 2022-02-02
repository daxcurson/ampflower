package ar.com.strellis.ampflower.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.exoplayer2.MediaItem;

import ar.com.strellis.ampflower.MainActivity;

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
