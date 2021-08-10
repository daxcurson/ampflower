package ar.com.strellis.ampflower.service;

import com.google.android.exoplayer2.MediaItem;

public interface MediaServiceEventsListener {
    void setBuffering(boolean status);
    void setPlaying(MediaItem mediaItem);
    void setPaused();
    void updateProgress(PlayerPositionEvent position);
}
