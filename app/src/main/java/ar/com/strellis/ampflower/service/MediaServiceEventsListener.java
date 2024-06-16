package ar.com.strellis.ampflower.service;

import androidx.media3.common.MediaItem;

public interface MediaServiceEventsListener {
    void setBuffering(boolean status);
    void setPlaying(MediaItem mediaItem, int positionInPlaylist);
    void setPaused();
    void updateProgress(PlayerPositionEvent position);
    void songChanged(MediaItem mediaItem,int positionInPlaylist);
}
