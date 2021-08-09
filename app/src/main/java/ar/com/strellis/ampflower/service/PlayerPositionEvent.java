package ar.com.strellis.ampflower.service;

public class PlayerPositionEvent {
    private long position;
    private long bufferedPosition;
    private long duration;

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    public long getBufferedPosition() {
        return bufferedPosition;
    }

    public void setBufferedPosition(long bufferedPosition) {
        this.bufferedPosition = bufferedPosition;
    }
    public void setDuration(Long duration)
    {
        this.duration=duration;
    }
    public long getDuration() {
        return duration;
    }
}
