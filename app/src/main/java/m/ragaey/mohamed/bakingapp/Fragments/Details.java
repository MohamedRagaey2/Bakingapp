package m.ragaey.mohamed.bakingapp.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import m.ragaey.mohamed.bakingapp.Activities.Ingredients;
import m.ragaey.mohamed.bakingapp.Models.Recipe;
import m.ragaey.mohamed.bakingapp.R;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class Details extends Fragment
        implements View.OnClickListener, ExoPlayer.EventListener {

    private Recipe recipe;
    private int mPosition;

    private FloatingActionButton floatingActionButton = null;
    private TextView step_description = null;
    private ImageButton previous = null;
    private ImageButton next = null;
    private SimpleExoPlayerView simpleExoPlayerView = null;
    private SimpleExoPlayer player;
    private String vedioURL;

    private MediaSessionCompat mediaSession = null;
    private PlaybackStateCompat.Builder stateBuilder = null;

    private long playbackPosition;

    public Details() {
        // Required empty public constructor
    }

    public static Details newInstance(int position, Recipe recipe1) {

        Details details = new Details();
        Bundle args = new Bundle();
        args.putInt("p", position);
        args.putSerializable("recipe", recipe1);
        details.setArguments(args);
        return details;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey("p")
                && getArguments().containsKey("recipe")) {

            mPosition = getArguments().getInt("p");
            recipe = (Recipe) getArguments().getSerializable("recipe");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);

        floatingActionButton = view.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(this);
        step_description = view.findViewById(R.id.step_description);

        previous = view.findViewById(R.id.previous);
        previous.setOnClickListener(this);

        next = view.findViewById(R.id.next);
        next.setOnClickListener(this);

        simpleExoPlayerView = view.findViewById(R.id.exo_ID);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        playbackPosition = C.TIME_UNSET;
        if (savedInstanceState != null) {
            playbackPosition = savedInstanceState.getLong("playbackPosition", C.TIME_UNSET);
        }

        initializePlayer();
        initializeMediaSession();
    }

    private void initializePlayer() {
        if (player == null) {
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            player = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
            simpleExoPlayerView.setPlayer(player);
            initializeVideo();
        }
    }

    private void initializeVideo() {
        step_description.setText(recipe.getSteps().get(mPosition).getDescription());

        vedioURL = recipe.getSteps().get(mPosition).getVideoURL();
        if (vedioURL.isEmpty()) {
            if (isConnected()) {
                Toast.makeText(getContext(), "No Vedio", Toast.LENGTH_LONG).show();
            }
        }
        Uri uri = Uri.parse(vedioURL);

        String userAgent = Util.getUserAgent(getContext(), "Baking");

        MediaSource mediaSource = new ExtractorMediaSource(uri,
                new DefaultDataSourceFactory(getContext(), userAgent),
                new DefaultExtractorsFactory(), null, null);

        if (isConnected()) {
            if (playbackPosition != C.TIME_UNSET) {
                player.seekTo(playbackPosition);
            }

            player.prepare(mediaSource);
            player.setPlayWhenReady(true);
        } else {
            Toast.makeText(getContext(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }

    }

    private void initializeMediaSession() {

        mediaSession = new MediaSessionCompat(getContext(), "MusicService");
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        mediaSession.setMediaButtonReceiver(null);

        stateBuilder = new PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY |
                        PlaybackStateCompat.ACTION_PAUSE |
                        PlaybackStateCompat.ACTION_PLAY_PAUSE |
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS);

        mediaSession.setPlaybackState(stateBuilder.build());

        mediaSession.setCallback(new MySessionCallback());

        mediaSession.setActive(true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.fab):

                Intent intent = new Intent(getContext(), Ingredients.class);
                intent.putExtra("recipe", recipe);
                getContext().startActivity(intent);

                break;
            case (R.id.previous):
                if (mPosition > 0) {
                    playbackPosition = C.TIME_UNSET;
                    mPosition--;
                    initializeVideo();
                }
                break;
            case (R.id.next):
                if (mPosition < recipe.getSteps().size() - 1) {
                    playbackPosition = C.TIME_UNSET;
                    mPosition++;
                    initializeVideo();
                }
                break;
        }
    }

    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    private void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();

            player.stop();
            player.release();
            player = null;
        }

        if (mediaSession != null) {
            mediaSession.setActive(false);
            mediaSession.release();
            mediaSession = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (Build.VERSION.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (Build.VERSION.SDK_INT > 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (Build.VERSION.SDK_INT > 23) {
            initializePlayer();
            initializeMediaSession();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT <= 23 || player == null) {
            initializePlayer();
            initializeMediaSession();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putLong("playbackPosition", playbackPosition);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object o) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroupArray, TrackSelectionArray trackSelectionArray) {

    }

    @Override
    public void onLoadingChanged(boolean b) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException e) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if ((playbackState == ExoPlayer.STATE_READY) && playWhenReady) {

            stateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    player.getCurrentPosition(),
                    1f);

        } else if ((playbackState == ExoPlayer.STATE_READY)) {

            stateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    player.getCurrentPosition(),
                    1f);

        }

        mediaSession.setPlaybackState(stateBuilder.build());
    }

    @Override
    public void onPositionDiscontinuity() {

    }


    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            super.onPlay();

            player.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            super.onPause();

            player.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();

            player.seekTo(0);
        }
    }

    public class MediaReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            MediaButtonReceiver.handleIntent(mediaSession, intent);
        }
    }

}
