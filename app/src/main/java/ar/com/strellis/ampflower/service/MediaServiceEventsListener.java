package ar.com.strellis.ampflower.service;

public interface MediaServiceEventsListener {
    void setBuffering(boolean status);
    void setPlaying();
    void setPaused();
    void updateProgress(PlayerPositionEvent position);
}
