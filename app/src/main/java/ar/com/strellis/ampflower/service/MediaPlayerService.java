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
import android.util.Log;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.lifecycle.LifecycleService;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.media3.common.AudioAttributes;
import androidx.media3.common.C;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.PlaybackParameters;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.session.CommandButton;
import androidx.media3.session.LibraryResult;
import androidx.media3.session.MediaLibraryService;
import androidx.media3.session.MediaSession;
import androidx.media3.session.SessionCommand;
import androidx.media3.session.SessionCommands;
import androidx.media3.ui.PlayerNotificationManager;
import androidx.paging.ExperimentalPagingApi;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ListenableFuture;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import ar.com.strellis.ampflower.BuildConfig;
import ar.com.strellis.ampflower.MainActivity;
import ar.com.strellis.ampflower.R;
import ar.com.strellis.ampflower.data.model.Song;
@UnstableApi
@ExperimentalPagingApi
public class MediaPlayerService extends MediaLibraryService
{
    public class MediaPlayerServiceBinder extends Binder
    {
        public MediaPlayerService getService()
        {
            return MediaPlayerService.this;
        }
    }
    private ExoPlayer exoPlayer;
    private PlayerNotificationManager playerNotificationManager;
    private MediaSession mediaSession;
    private MediaLibrarySession mediaLibrarySession = null;
    private List<CommandButton> commandButtons;
    private final MediaLibrarySession.Callback callback = new MediaLibrarySession.Callback() {
        @NonNull
        @Override
        public MediaSession.ConnectionResult onConnect(@NonNull MediaSession session, @NonNull MediaSession.ControllerInfo controller) {
            SessionCommands commands=MediaSession.ConnectionResult.DEFAULT_SESSION_AND_LIBRARY_COMMANDS.buildUpon().build();
            return new MediaSession.ConnectionResult.AcceptedResultBuilder(session).setAvailableSessionCommands(commands).build();
        }

        @NonNull
        @Override
        public ListenableFuture<LibraryResult<MediaItem>> onGetLibraryRoot(@NonNull MediaLibrarySession session, @NonNull MediaSession.ControllerInfo browser, @Nullable LibraryParams params) {
            return MediaLibrarySession.Callback.super.onGetLibraryRoot(session, browser, params);
        }

        @NonNull
        @Override
        public ListenableFuture<LibraryResult<MediaItem>> onGetItem(@NonNull MediaLibrarySession session, @NonNull MediaSession.ControllerInfo browser, @NonNull String mediaId) {
            return MediaLibrarySession.Callback.super.onGetItem(session, browser, mediaId);
        }

        @NonNull
        @Override
        public ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> onGetChildren(@NonNull MediaLibrarySession session, @NonNull MediaSession.ControllerInfo browser, @NonNull String parentId, int page, int pageSize, @Nullable LibraryParams params) {
            return MediaLibrarySession.Callback.super.onGetChildren(session, browser, parentId, page, pageSize, params);
        }

        @NonNull
        @Override
        public ListenableFuture<LibraryResult<Void>> onSubscribe(@NonNull MediaLibrarySession session, @NonNull MediaSession.ControllerInfo browser, @NonNull String parentId, @Nullable LibraryParams params) {
            return MediaLibrarySession.Callback.super.onSubscribe(session, browser, parentId, params);
        }

        @NonNull
        @Override
        public ListenableFuture<LibraryResult<Void>> onUnsubscribe(@NonNull MediaLibrarySession session, @NonNull MediaSession.ControllerInfo browser, @NonNull String parentId) {
            return MediaLibrarySession.Callback.super.onUnsubscribe(session, browser, parentId);
        }

        @NonNull
        @Override
        public ListenableFuture<LibraryResult<Void>> onSearch(@NonNull MediaLibrarySession session, @NonNull MediaSession.ControllerInfo browser, @NonNull String query, @Nullable LibraryParams params) {
            return MediaLibrarySession.Callback.super.onSearch(session, browser, query, params);
        }

        @NonNull
        @Override
        public ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> onGetSearchResult(@NonNull MediaLibrarySession session, @NonNull MediaSession.ControllerInfo browser, @NonNull String query, int page, int pageSize, @Nullable LibraryParams params) {
            return MediaLibrarySession.Callback.super.onGetSearchResult(session, browser, query, page, pageSize, params);
        }
    };
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
    public static final String ACTION_ADD_SONGS = BuildConfig.APPLICATION_ID+".action.ADD_SONGS";
    public static final String ACTION_MOVE_ITEM = BuildConfig.APPLICATION_ID+".action.MOVE_ITEM";
    public static final String ACTION_PLAY_ITEM = BuildConfig.APPLICATION_ID+".action.PLAY_ITEM";
    public static final String ACTION_DELETE_ITEM = BuildConfig.APPLICATION_ID+".action.DELETE_ITEM";
    public static final String ACTION_SEEK = BuildConfig.APPLICATION_ID+".action.SEEK";
    public static final String ACTION_RENEW_TOKEN = BuildConfig.APPLICATION_ID + ".action.RENEW_LOGINRESPONSE";

    public MediaPlayerService()
    {
        listeners=new LinkedList<>();
    }
    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.d("MediaPlayerService","Launching the media player service");
        exoPlayer=new ExoPlayer.Builder(getApplicationContext()).build();
        AudioAttributes audioAttributes=new AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                .build();
        exoPlayer.setAudioAttributes(audioAttributes,true);
        exoPlayer.addListener(new PlayerEventListener());
        playerNotificationManager=new PlayerNotificationManager.Builder(
                getApplicationContext(),
                PLAYBACK_NOTIFICATION_ID,
                PLAYBACK_CHANNEL_ID)
                .setNotificationListener(
                        new PlayerNotificationManager.NotificationListener() {
                            @Override
                            public void onNotificationCancelled(int notificationId, boolean dismissedByUser) {
                                Log.d("PlayerNotificationManager.NotificationListener.onNotificationCancelled", "Cancelled by user? " + dismissedByUser + ", notification id: " + notificationId);
                                stopSelf();
                            }

                            @Override
                            public void onNotificationPosted(int notificationId, @NotNull Notification notification, boolean ongoing) {
                                Log.d("PlayerNotificationManager.NotificationListener.onNotificationPosted", "Ongoing? " + ongoing + ", notification id: " + notificationId);
                                if (ongoing) {
                                    // Make sure the service will not be destroyed while playing media.
                                    startForeground(notificationId, notification);
                                } else {
                                    // make notification cancellable
                                    stopForeground(false);
                                }
                            }
                        })
                .setChannelNameResourceId(R.string.channel_name)
                .setChannelDescriptionResourceId(R.string.channel_desc)
                .setMediaDescriptionAdapter(
                        /*
                        This is an adapter to report the contents of the media that is currently being played,
                        so that the notification has it.
                        */
                        new PlayerNotificationManager.MediaDescriptionAdapter() {
                            @NotNull
                            @Override
                            public CharSequence getCurrentContentTitle(@NotNull Player player) {
                                if (player.getCurrentMediaItem() != null)
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
                                Log.d("MediaPlayerService", "I am asked to show the activity");
                                Intent intent=new Intent(getApplicationContext(), MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                return PendingIntent.getActivity(getApplicationContext(),
                                        0,
                                        intent,
                                        PendingIntent.FLAG_UPDATE_CURRENT| android.app.PendingIntent.FLAG_MUTABLE);
                            }
                            @NotNull
                            @Override
                            public CharSequence getCurrentContentText(@NotNull Player player) {
                                Log.d("MediaPlayerService","getCurrentContentText");
                                if(player.getCurrentMediaItem() != null) {
                                    player.getCurrentMediaItem();
                                    String album= Objects.requireNonNull(player.getCurrentMediaItem().mediaMetadata.albumTitle).toString();
                                    String artist=Objects.requireNonNull(player.getCurrentMediaItem().mediaMetadata.artist).toString();
                                    return album+" by "+artist;
                                }
                                else
                                    return "No media selected";
                            }

                            @Nullable
                            @Override
                            /*
                            This could be used to return the album art, maybe?
                            */
                            public Bitmap getCurrentLargeIcon(@NotNull Player player, @NotNull PlayerNotificationManager.BitmapCallback callback) {
                                Log.d("MediaPlayerService", "getCurrentLargeIcon");
                                return null;
                            }
                        }).build();

        playerNotificationManager.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        playerNotificationManager.setUseStopAction(true);
        playerNotificationManager.setPlayer(exoPlayer);
        mediaSession=new MediaLibrarySession.Builder(getApplicationContext(),exoPlayer,callback)
                .setId(BuildConfig.APPLICATION_ID+".mediaPlayerService"+MEDIA_SESSION_TAG)
                .build();
        mediaLibrarySession = new MediaLibrarySession.Builder(this, exoPlayer, callback).build();
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

    @Nullable
    @Override
    public MediaLibrarySession onGetSession(MediaSession.ControllerInfo controllerInfo) {
        return mediaLibrarySession;
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
        playerNotificationManager.setPlayer(null);
        if (mediaLibrarySession != null) {
            mediaLibrarySession.getPlayer().release();
            mediaLibrarySession.release();
            mediaLibrarySession = null;
        }
        exoPlayer.release();
        super.onDestroy();
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
                case ACTION_ADD_SONGS:
                    // There is a list of songs to ADD to the playlist.
                    songs=(List<Song>) intent.getSerializableExtra("LIST");
                    addSongsToPlaylist(songs);
                    break;
                case ACTION_MOVE_ITEM:
                    int fromPosition= (int) intent.getSerializableExtra("fromPosition");
                    int toPosition=(int)intent.getSerializableExtra("toPosition");
                    moveMediaItem(fromPosition,toPosition);
                    break;
                case ACTION_PLAY_ITEM:
                    int position=(int)intent.getSerializableExtra("position");
                    playItemInPosition(position);
                    break;
                case ACTION_DELETE_ITEM:
                    int positionToDelete=(int)intent.getSerializableExtra("position");
                    deleteItemInPosition(positionToDelete);
                    break;
                case ACTION_SEEK:
                    long seekPosition=(long)intent.getSerializableExtra("position");
                    seekCurrentlyPlayingItem(seekPosition);
            }
        }
    }

    private void seekCurrentlyPlayingItem(long seekPosition)
    {
        exoPlayer.seekTo(seekPosition);
    }
    /**
     * When I receive this message, I must put the songs I find selected
     * in the SongsViewModel into the playlist.
     */
    private void selectSongsIntoPlaylist(List<Song> songs)
    {
        Log.d("MediaPlayerService.selectSongsIntoPlaylist","I received "+songs.size()+" songs!!");
        // I'll just shove these songs into the playlist.
        List<MediaItem> items=convertSongsToMedia(songs);
        playList(items);
    }

    /**
     * Adds songs to the current playlist
     * @param songs List of songs to ADD to the current playlist
     */
    private void addSongsToPlaylist(List<Song> songs)
    {
        Log.d("MediaPlayerService.addSongsToPlaylist","I received "+songs.size()+" songs to ADD to the playlist!!");
        // I'll just shove these songs into the playlist.
        List<MediaItem> items=convertSongsToMedia(songs);
        addToPlaylist(items);
    }
    public List<MediaItem> convertSongsToMedia(List<Song> songs)
    {
        // The ExoPlayer accepts MediaItems, and I have songs. I'll create a map() to convert between formats.
        return songs.stream().map(song ->
                {
                    Bundle extras=new Bundle();
                    extras.putSerializable("ARTIST_ID",song.getArtist().getId());
                    MediaMetadata metadata=new MediaMetadata.Builder()
                            .setTitle(song.getTitle())
                            .setArtist(song.getArtist().getName())
                            .setAlbumArtist(song.getArtist().getName())
                            .setAlbumTitle(song.getAlbum().getName())
                            .setArtworkUri(Uri.parse(song.getArt()))
                            .setDisplayTitle(song.getTitle())
                            .setExtras(extras)
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
    public void addToPlaylist(List<MediaItem> playlist)
    {
        exoPlayer.addMediaItems(playlist);
    }
    public void playItemInPosition(int position)
    {
        // I have to tell the player to play this item
        exoPlayer.seekTo(position, C.TIME_UNSET);
    }
    public void pause()
    {
        Log.d("MediaPlayerService","Pausing the player service");
        exoPlayer.setPlayWhenReady(false);
    }
    public void moveMediaItem(int fromPosition,int toPosition)
    {
        Log.d("MediaPlayerService","Moving the item "+fromPosition+" to position "+toPosition);
        exoPlayer.moveMediaItem(fromPosition,toPosition);
    }
    public void deleteItemInPosition(int position)
    {
        Log.d("MediaPlayerService","Deleting the item "+position+" from the playlist");
        exoPlayer.removeMediaItem(position);
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
            l.setPlaying(exoPlayer.getCurrentMediaItem(),exoPlayer.getCurrentWindowIndex());
    }
    private void dispatchPausedEvent()
    {
        for(MediaServiceEventsListener l:listeners)
            l.setPaused();
    }
    private void dispatchSongChangedEvent(MediaItem item,int position)
    {
        Log.d("MediaPlayerService","Now playing song "+exoPlayer.getCurrentWindowIndex()+", position "+position);
        for(MediaServiceEventsListener l:listeners)
            l.songChanged(item,position);
    }
    private void dispatchProgressUpdateEvent()
    {
        // We'll dispatch this event only if it is necessary.
        int playbackstate=exoPlayer==null ? Player.STATE_IDLE : exoPlayer.getPlaybackState();
        assert exoPlayer != null;
        // Remove scheduled updates.
        // It turns out, that the very code of the ExoPlayer removes this progress update
        // before adding a new one with the postDelayed!!!!
        handler.removeCallbacks(updateAction);
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
    /**
     * Sends a message to the Activity to request the renewal of the LoginResponse, and maybe restart the player?
     */
    private void requestRenewLoginResponse() {
        Intent intent = new Intent(ACTION_RENEW_TOKEN);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private class PlayerEventListener implements Player.Listener {
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
        public void onPlayerError(PlaybackException error) {
            Log.d("MediaPlayerService", "The Player reported an error: " + error.getMessage() + " (" + error.errorCode + ")");
            // If we get a source error, maybe the session cookie needs updating.
            // This should send a message back to the activity to request a new LoginResponse.
            switch (error.errorCode) {
                case PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT:
                    // The server has gone away.
                    Log.d("MediaPlayerService", "The server has gone away, network connection timeout: " + error.getMessage() + " (" + error.errorCode + ")");
                    break;
                case PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS:
                    // There is an unexpected HTTP status, probably 403 because of the expired token.
                    Log.d("MediaPlayerService", "Bad HTTP status, will attempt to renew the service token: " + error.getMessage() + " (" + error.errorCode + ")");
                    requestRenewLoginResponse();
                    break;
                default:
                    Log.d("MediaPlayerService", "Unexpected error: " + error.getMessage() + " (" + error.errorCode + ")");
                    // Some unexpected error.
                    break;
                case PlaybackException.ERROR_CODE_AUDIO_TRACK_INIT_FAILED:
                case PlaybackException.ERROR_CODE_AUDIO_TRACK_WRITE_FAILED:
                case PlaybackException.ERROR_CODE_BEHIND_LIVE_WINDOW:
                case PlaybackException.ERROR_CODE_DECODER_INIT_FAILED:
                case PlaybackException.ERROR_CODE_DECODER_QUERY_FAILED:
                case PlaybackException.ERROR_CODE_DECODING_FAILED:
                case PlaybackException.ERROR_CODE_DECODING_FORMAT_EXCEEDS_CAPABILITIES:
                case PlaybackException.ERROR_CODE_DECODING_FORMAT_UNSUPPORTED:
                case PlaybackException.ERROR_CODE_DRM_CONTENT_ERROR:
                case PlaybackException.ERROR_CODE_DRM_DEVICE_REVOKED:
                case PlaybackException.ERROR_CODE_DRM_DISALLOWED_OPERATION:
                case PlaybackException.ERROR_CODE_DRM_LICENSE_ACQUISITION_FAILED:
                case PlaybackException.ERROR_CODE_DRM_LICENSE_EXPIRED:
                case PlaybackException.ERROR_CODE_DRM_PROVISIONING_FAILED:
                case PlaybackException.ERROR_CODE_DRM_SCHEME_UNSUPPORTED:
                case PlaybackException.ERROR_CODE_DRM_SYSTEM_ERROR:
                case PlaybackException.ERROR_CODE_DRM_UNSPECIFIED:
                case PlaybackException.ERROR_CODE_FAILED_RUNTIME_CHECK:
                case PlaybackException.ERROR_CODE_IO_CLEARTEXT_NOT_PERMITTED:
                case PlaybackException.ERROR_CODE_IO_FILE_NOT_FOUND:
                case PlaybackException.ERROR_CODE_IO_INVALID_HTTP_CONTENT_TYPE:
                case PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED:
                case PlaybackException.ERROR_CODE_IO_NO_PERMISSION:
                case PlaybackException.ERROR_CODE_IO_READ_POSITION_OUT_OF_RANGE:
                case PlaybackException.ERROR_CODE_IO_UNSPECIFIED:
                case PlaybackException.ERROR_CODE_PARSING_CONTAINER_MALFORMED:
                case PlaybackException.ERROR_CODE_PARSING_CONTAINER_UNSUPPORTED:
                case PlaybackException.ERROR_CODE_PARSING_MANIFEST_MALFORMED:
                case PlaybackException.ERROR_CODE_PARSING_MANIFEST_UNSUPPORTED:
                case PlaybackException.ERROR_CODE_REMOTE_ERROR:
                case PlaybackException.ERROR_CODE_TIMEOUT:
                case PlaybackException.ERROR_CODE_UNSPECIFIED:
                    Log.d("MediaPlayerService", "Unexpected error: " + error.getMessage() + " (" + error.errorCode + ")");
                    // Some unexpected error.
                    break;
            }
        }

        @Override
        public void onIsPlayingChanged(boolean isPlaying) {
            Log.d("MediaPlayerService.onIsPlayingChanged","Playing state changed to "+isPlaying);
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
        public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
            // The song being played changed. Inform the listeners.
            dispatchSongChangedEvent(mediaItem,exoPlayer.getCurrentWindowIndex());
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
