package ar.com.strellis.ampflower.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.lifecycle.LifecycleService;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import ar.com.strellis.ampflower.BuildConfig;
import ar.com.strellis.ampflower.MainActivity;
import ar.com.strellis.ampflower.R;
import wseemann.media.FFmpegMediaMetadataRetriever;

public class MediaPlayerService extends LifecycleService
{
    public class MediaPlayerServiceBinder extends Binder
    {
        public MediaPlayerService getService()
        {
            return MediaPlayerService.this;
        }
        /*public ExoPlayer getExoPlayer()
        {
            return MediaPlayerService.this.getExoPlayer();
        }*/
    }
    private SimpleExoPlayer exoPlayer;
    private PlayerNotificationManager playerNotificationManager;
    private MediaSessionCompat mediaSession;
    private MediaSessionConnector mediaSessionConnector;
    private static final String PLAYBACK_CHANNEL_ID="ampflower_channel";
    private static final int PLAYBACK_NOTIFICATION_ID=1;
    private static final String MEDIA_SESSION_TAG="sed_audio";
    private static final String ARG_URI="uri_string";
    private final List<MediaServiceEventsListener> listeners;
    private Handler handler;
    private Runnable updateAction;
    public ExoPlayer getExoPlayer()
    {
        return exoPlayer;
    }
    public static final String ACTION_STOP = BuildConfig.APPLICATION_ID + ".action.STOP";
    public static final String ACTION_PLAY = BuildConfig.APPLICATION_ID + ".action.PLAY";
    public static final String ACTION_PREVIOUS = BuildConfig.APPLICATION_ID + ".action.PREVIOUS";
    public static final String ACTION_NEXT = BuildConfig.APPLICATION_ID + ".action.NEXT";
    public static final String ACTION_TOGGLE = BuildConfig.APPLICATION_ID + ".action.TOGGLE_PLAYPAUSE";


    public MediaPlayerService()
    {
        listeners=new LinkedList<>();
    }
    @Override
    public void onCreate()
    {
        super.onCreate();
        exoPlayer=new SimpleExoPlayer.Builder(getApplicationContext()).build();
        AudioAttributes audioAttributes=new AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.CONTENT_TYPE_MUSIC)
                .build();
        exoPlayer.setAudioAttributes(audioAttributes,true);
        exoPlayer.addListener(new PlayerEventListener());
        playerNotificationManager=PlayerNotificationManager.createWithNotificationChannel(
                getApplicationContext(),
                PLAYBACK_CHANNEL_ID,
                R.string.channel_name,
                R.string.channel_desc,
                PLAYBACK_NOTIFICATION_ID,
                /*
                  This is an adapter to report the contents of the media that is currently being played,
                  so that the notification has it.
                 */
                new PlayerNotificationManager.MediaDescriptionAdapter()
                {
                    @NotNull
                    @Override
                    public CharSequence getCurrentContentTitle(@NotNull Player player) {
                        if(player.getCurrentMediaItem()!=null)
                            return Objects.requireNonNull(player.getCurrentMediaItem().mediaMetadata.title);
                        else
                            return "No media playing";
                    }

                    @Nullable
                    @Override
                    /*
                      Opens the application activity when the notification is clicked.
                      @param Player player
                     */
                    public PendingIntent createCurrentContentIntent(@NotNull Player player) {
                        Log.d("MediaPlayerService","I am asked to show the activity");
                        return PendingIntent.getBroadcast(getApplicationContext(),
                                0,
                                new Intent(getApplicationContext(), MainActivity.class),
                                PendingIntent.FLAG_UPDATE_CURRENT);
                    }

                    @NotNull
                    @Override
                    public CharSequence getCurrentContentText(@NotNull Player player) {
                        Log.d("MediaPlayerService","getCurrentContentText");
                        try {
                            FFmpegMediaMetadataRetriever mmr = new FFmpegMediaMetadataRetriever();
                            if(player.getCurrentMediaItem()!=null && player.getCurrentMediaItem().playbackProperties!=null) {
                                String uri = Objects.requireNonNull(Objects.requireNonNull(player.getCurrentMediaItem()).playbackProperties).uri.toString();
                                Log.d("MediaPlayerService", "Media URL=" + uri);
                                mmr.setDataSource(uri);
                                String album = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ALBUM);
                                String artist = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ARTIST);
                                mmr.release();
                                if (album != null && artist != null)
                                    return album + " by " + artist;
                            }
                        }
                        catch(IllegalArgumentException e)
                        {
                            Log.d("MediaPlayerService","Error downloading metadata in getCurrentContentText");
                        }
                        return "Temporarily unavailable";
                    }

                    @Nullable
                    @Override
                    /*
                    This could be used to return the album art, maybe?
                     */
                    public Bitmap getCurrentLargeIcon(@NotNull Player player, @NotNull PlayerNotificationManager.BitmapCallback callback) {
                        Log.d("MediaPlayerService","getCurrentLargeIcon");
                        return null;
                    }
                },
                new PlayerNotificationManager.NotificationListener() {
                    @Override
                    public void onNotificationCancelled(int notificationId, boolean dismissedByUser) {
                        Log.d("PlayerNotificationManager.NotificationListener.onNotificationCancelled","Cancelled by user? "+dismissedByUser+", notification id: "+notificationId);
                        stopSelf();
                    }

                    @Override
                    public void onNotificationPosted(int notificationId, @NotNull Notification notification, boolean ongoing) {
                        Log.d("PlayerNotificationManager.NotificationListener.onNotificationPosted","Ongoing? "+ongoing+", notification id: "+notificationId);
                        if(ongoing)
                        {
                            // Make sure the service will not be destroyed while playing media.
                            startForeground(notificationId,notification);
                        }
                        else
                        {
                            // make notification cancellable
                            stopForeground(false);
                        }
                    }
                }
        );
        playerNotificationManager.setUseStopAction(true);
        playerNotificationManager.setPlayer(exoPlayer);
        mediaSession=new MediaSessionCompat(getApplicationContext(),MEDIA_SESSION_TAG);
        mediaSession.setActive(true);
        playerNotificationManager.setMediaSessionToken(mediaSession.getSessionToken());
        mediaSessionConnector=new MediaSessionConnector(mediaSession);
        mediaSessionConnector.setQueueNavigator(new TimelineQueueNavigator(mediaSession) {
            @NotNull
            @Override
            public MediaDescriptionCompat getMediaDescription(@NotNull Player player, int windowIndex) {
                Bitmap bitmap = getBitmapFromVectorDrawable(getApplicationContext(), R.drawable.bs_play_2);
                Bundle extras = new Bundle();
                FFmpegMediaMetadataRetriever mmr = new FFmpegMediaMetadataRetriever();
                String uri= Objects.requireNonNull(Objects.requireNonNull(player.getCurrentMediaItem()).playbackProperties).uri.toString();
                Log.d("MediaPlayerService","Media URL="+uri);
                String album="";
                String artist="";
                try {
                    mmr.setDataSource(uri);
                    album = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ALBUM);
                    artist = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ARTIST);
                    mmr.release();
                }
                catch(IllegalArgumentException e)
                {
                    Log.d("MediaPlayerService","Error when retrieving metadata");
                }
                extras.putParcelable(MediaMetadataCompat.METADATA_KEY_ALBUM_ART,bitmap);
                extras.putParcelable(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON,bitmap);
                extras.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist);
                extras.putString(MediaMetadataCompat.METADATA_KEY_ALBUM,album);
                // Here we would send the title of the song.
                CharSequence title="No media playing";
                if(player.getCurrentMediaItem()!=null)
                    title = player.getCurrentMediaItem().mediaMetadata.title;
                Log.d("MediaPlayerService","Playing "+title);
                return new MediaDescriptionCompat.Builder()
                        .setIconBitmap(bitmap)
                        .setTitle(title)
                        .setExtras(extras)
                        .build();
            }
        });
        mediaSessionConnector.setPlayer(exoPlayer);
        handler=new Handler();
        // I configure the handler to send periodic updates.
        updateAction= this::dispatchProgressUpdateEvent;
    }
    public void addEventListener(MediaServiceEventsListener listener)
    {
        this.listeners.add(listener);
    }
    public void removeEventListener(MediaServiceEventsListener listener)
    {
        listeners.remove(listener);
    }
    @Override
    public IBinder onBind(@NotNull Intent intent)
    {
        super.onBind(intent);
        handleIntent(intent);
        return new MediaPlayerServiceBinder();
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        assert intent != null;
        handleIntent(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        mediaSession.release();
        mediaSessionConnector.setPlayer(null);
        playerNotificationManager.setPlayer(null);
        exoPlayer.release();
        super.onDestroy();
    }
    private void handleIntent(Intent intent)
    {
        Uri uri=intent.getParcelableExtra(ARG_URI);
        // It seems that I would receive a command to play a song here?
        // I would also receive the name, to display in the notification box. How clever!
        play(uri);
    }
    public void play(Uri uri)
    {
        if(uri!=null) {
            MediaItem item = MediaItem.fromUri(uri);
            exoPlayer.setMediaItem(item);
            exoPlayer.prepare();
            exoPlayer.setPlayWhenReady(true);
        }
    }
    public void playList(List<MediaItem> playlist)
    {
        exoPlayer.setMediaItems(playlist);
        exoPlayer.prepare();
        exoPlayer.setPlayWhenReady(true);
    }
    public void pause()
    {
        exoPlayer.setPlayWhenReady(false);
    }
    private Bitmap getBitmapFromVectorDrawable(Context context, @DrawableRes int drawableId)
    {
        Drawable contextCompat= ContextCompat.getDrawable(context, drawableId);

        Drawable drawable = DrawableCompat.wrap(contextCompat).mutate();

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static Intent newIntent(Context context)
    {
        // If we have a previously-established position to play the media, for example,
        // if we already know which song we are going to play and I want to play it
        // at a specific position, we would send that song and position as a parameter and add the
        // values to the Intent via a putExtra() call.
        return new Intent(context,MediaPlayerService.class);
    }
    private void dispatchBufferingEvent(boolean statusToInform)
    {
        for(MediaServiceEventsListener l:listeners) {
            l.setBuffering(statusToInform);
        }
    }
    private void dispatchPlayingEvent()
    {
        for(MediaServiceEventsListener l:listeners)
            l.setPlaying();
    }
    private void dispatchPausedEvent()
    {
        for(MediaServiceEventsListener l:listeners)
            l.setPaused();
    }
    private void dispatchProgressUpdateEvent()
    {
        // We'll dispatch this event only if it is necessary.
        int playbackstate=exoPlayer==null ? Player.STATE_IDLE : exoPlayer.getPlaybackState();
        assert exoPlayer != null;
        long position=exoPlayer.getCurrentPosition();
        long nextUpdateTime=1000-position%1000;
        PlayerPositionEvent event = new PlayerPositionEvent();
        event.setPosition(position);
        event.setBufferedPosition(exoPlayer.getBufferedPosition());
        event.setDuration(exoPlayer.getDuration());
        // If the player is playing, we dispatch the event at the next position,
        // which is within the next second. If it is not playing,
        // it must be buffering, I'll just wait one full second.
        if(exoPlayer!=null && exoPlayer.isPlaying()) {
            for (MediaServiceEventsListener l : listeners)
                l.updateProgress(event);
            // Now that the event has been sent, schedule the next update.
            handler.postDelayed(updateAction,nextUpdateTime);
        }
        else if(playbackstate!=Player.STATE_ENDED && playbackstate!=Player.STATE_IDLE)
        {
            for(MediaServiceEventsListener l:listeners)
                l.updateProgress(event);
            handler.postDelayed(updateAction,1000);
        }
    }
    private class PlayerEventListener implements Player.Listener
    {
        @Override
        public void onPlaybackStateChanged(int state) {
            String stringState="";
            switch (state) {
                case Player.STATE_BUFFERING:
                    stringState="Buffering";
                    // I have to inform the model that I'm buffering...
                    dispatchBufferingEvent(true);
                    dispatchPlayingEvent();
                    break;
                case Player.STATE_ENDED:
                    stringState="Ended";
                    dispatchBufferingEvent(false);
                    break;
                case Player.STATE_IDLE:
                    stringState="Idle";
                    dispatchBufferingEvent(false);
                    dispatchPlayingEvent();
                    break;
                case Player.STATE_READY:
                    dispatchBufferingEvent(false);
                    stringState="Ready";
                    if(exoPlayer.getPlayWhenReady()) {
                        stringState += " and playing";
                        dispatchPlayingEvent();
                    }
                    else {
                        stringState += " and paused";
                        dispatchPausedEvent();
                    }
                    break;
            }
            Log.d("DEBUG","Player state changed to "+stringState);
        }

        @Override
        public void onPlayerError(@NotNull ExoPlaybackException error) {
            Log.d("DEBUG","The Player reported an error: "+error.getMessage());
        }

        @Override
        public void onIsPlayingChanged(boolean isPlaying) {
            Log.d("DEBUG","Playing state changed to "+isPlaying);
            if(isPlaying) {
                dispatchPlayingEvent();
                // Also dispatch an update event!
                // Progress updates then will concatenate themselves one after the other until
                // the playback ends.
                dispatchProgressUpdateEvent();
            }
            else
                dispatchPausedEvent();
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {
        }

        @Override
        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
        }

        @Override
        public void onPlaybackParametersChanged(@NotNull PlaybackParameters playbackParameters) {
        }
    }
}
