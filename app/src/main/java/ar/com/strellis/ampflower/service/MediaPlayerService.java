package ar.com.strellis.ampflower.service;

import android.annotation.SuppressLint;
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
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.lifecycle.LifecycleService;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.MediaMetadata;
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
import java.util.stream.Collectors;

import ar.com.strellis.ampflower.BuildConfig;
import ar.com.strellis.ampflower.MainActivity;
import ar.com.strellis.ampflower.R;
import ar.com.strellis.ampflower.data.model.Song;

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
    private final List<MediaServiceEventsListener> listeners;
    private Handler handler;
    private Runnable updateAction;

    public static final String ACTION_STOP = BuildConfig.APPLICATION_ID + ".action.STOP";
    public static final String ACTION_PLAY = BuildConfig.APPLICATION_ID + ".action.PLAY";
    public static final String ACTION_PAUSE = BuildConfig.APPLICATION_ID + ".action.PAUSE";
    public static final String ACTION_PREVIOUS = BuildConfig.APPLICATION_ID + ".action.PREVIOUS";
    public static final String ACTION_NEXT = BuildConfig.APPLICATION_ID + ".action.NEXT";
    public static final String ACTION_TOGGLE = BuildConfig.APPLICATION_ID + ".action.TOGGLE_PLAYPAUSE";
    public static final String ACTION_SELECT_SONGS = BuildConfig.APPLICATION_ID+".action.SELECT_SONGS";

    public MediaPlayerService()
    {
        listeners=new LinkedList<>();
    }
    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.d("MediaPlayerService","Launching the media player service");
        exoPlayer=new SimpleExoPlayer.Builder(getApplicationContext()).build();
        AudioAttributes audioAttributes=new AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.CONTENT_TYPE_MUSIC)
                .build();
        exoPlayer.setAudioAttributes(audioAttributes,true);
        exoPlayer.addListener(new PlayerEventListener());
        playerNotificationManager=new PlayerNotificationManager.Builder(
                getApplicationContext(),
                PLAYBACK_NOTIFICATION_ID,
                PLAYBACK_CHANNEL_ID,
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

                    @SuppressLint("UnspecifiedImmutableFlag")
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
                        if(player.getCurrentMediaItem() != null) {
                            player.getCurrentMediaItem();

                        }
                        String album= Objects.requireNonNull(player.getCurrentMediaItem().mediaMetadata.albumTitle).toString();
                        String artist=Objects.requireNonNull(player.getCurrentMediaItem().mediaMetadata.artist).toString();
                        return album+" by "+artist;
                        /*
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

                         */
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
                })
                .setNotificationListener(
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
                })
                .setChannelNameResourceId(R.string.channel_name)
                .setChannelDescriptionResourceId(R.string.channel_desc)
                .build();
        playerNotificationManager.setPriority(NotificationCompat.PRIORITY_DEFAULT);
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
                /*
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
                }*/
                String album= Objects.requireNonNull(Objects.requireNonNull(player.getCurrentMediaItem()).mediaMetadata.albumTitle).toString();
                String artist= Objects.requireNonNull(player.getCurrentMediaItem().mediaMetadata.artist).toString();

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
        String action=intent.getAction();
        if(action!=null)
        {
            switch (action)
            {
                case ACTION_STOP:
                    exoPlayer.stop();
                    break;
                case ACTION_PLAY:
                    exoPlayer.play();
                    break;
                case ACTION_PAUSE:
                    exoPlayer.pause();
                    break;
                case ACTION_NEXT:
                    exoPlayer.next();
                    break;
                case ACTION_PREVIOUS:
                    exoPlayer.previous();
                    break;
                case ACTION_TOGGLE:
                    if(exoPlayer.isPlaying())
                        exoPlayer.pause();
                    else
                        exoPlayer.play();
                    break;
                case ACTION_SELECT_SONGS:
                    // Recover the list from the Intent
                    List<Song> songs = (List<Song>) intent.getSerializableExtra("LIST");
                    selectSongsIntoPlaylist(songs);
                    break;
            }
        }
    }

    /**
     * When I receive this message, I must put the songs I find selected
     * in the SongsViewModel into the playlist.
     */
    private void selectSongsIntoPlaylist(List<Song> songs)
    {
        Log.d("MediaPlayerService","I received songs!!");
        // I'll just shove these songs into the playlist.
        List<MediaItem> items=convertSongsToMedia(songs);
        playList(items);
    }
    public List<MediaItem> convertSongsToMedia(List<Song> songs)
    {
        // The ExoPlayer accepts MediaItems, and I have songs. I'll create a map() to convert between formats.
        return songs.stream().map(song ->
                {
                    MediaMetadata metadata=new MediaMetadata.Builder()
                            .setTitle(song.getTitle())
                            .setArtist(song.getArtist().getName())
                            .setAlbumArtist(song.getArtist().getName())
                            .setAlbumTitle(song.getAlbum().getName())
                            .setArtworkUri(Uri.parse(song.getArt()))
                            .build();
                    return new MediaItem.Builder().setUri(song.getUrl())
                            .setMediaMetadata(metadata)
                            .build();
                }
        ).collect(Collectors.toList());
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
        Log.d("MediaPlayerService","Pausing the player service");
        exoPlayer.setPlayWhenReady(false);
    }
    private Bitmap getBitmapFromVectorDrawable(Context context, @DrawableRes int drawableId)
    {
        Drawable contextCompat= ContextCompat.getDrawable(context, drawableId);

        assert contextCompat != null;
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
            l.setPlaying(exoPlayer.getCurrentMediaItem());
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
        else if(exoPlayer!=null && playbackstate==Player.STATE_BUFFERING)
        {
            Log.d("MediaPlayerService","Player in state: "+playbackstate);
            Log.d("MediaPlayerService","The player is buffering, sending progress update");
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