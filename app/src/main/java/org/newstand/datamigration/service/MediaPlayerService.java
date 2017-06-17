package org.newstand.datamigration.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.nick.yinheng.model.IMediaTrack;
import com.nick.yinheng.service.IMusicPlayerService;
import com.nick.yinheng.service.IPlaybackListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MediaPlayerService extends Service implements MediaPlayer.OnCompletionListener {

    final List<IPlaybackListener> mListeners;
    List<IMediaTrack> tracks;
    private ServiceStub mStub;
    private LazyPlayer mPlayer;
    private State mState;

    public MediaPlayerService() {
        mListeners = new ArrayList<>();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mStub = new ServiceStub();
        mPlayer = new LazyPlayer();
        mPlayer.setOnCompletionListener(this);
        mState = State.Idle;
    }

    private void play(IMediaTrack track) {
        Preconditions.checkNotNull(track);
        setState(State.Playing);
        mPlayer.stop();
        mPlayer.reset();
        mPlayer.setCurrent(track);
        try {
            mPlayer.setDataSource(track.getUrl());
            mPlayer.prepare();
            notifyStart(track);
            mPlayer.start();
            notifyPlaying(track);
        } catch (IOException e) {
            notifyError(Integer.MIN_VALUE, e.getLocalizedMessage());
        }
    }

    private void pause() {
        if (getState() != State.Playing) {
            return;
        }
        setState(State.Paused);
        mPlayer.pause();
        notifyPaused(mPlayer.getCurrent());
    }

    private void resume() {
        if (getState() == State.Playing) {
            return;
        }
        setState(State.Playing);
        mPlayer.start();
        notifyResume(mPlayer.getCurrent());
    }

    private void stop() {
        if (getState() == State.Stopped) {
            return;
        }
        setState(State.Stopped);
        mPlayer.stop();
        notifyStop(mPlayer.getCurrent());
    }

    private void assumePendingList(List<IMediaTrack> tracks)
            throws RemoteException {
        this.tracks = tracks;
    }

    private void listen(IPlaybackListener listener) {
        Preconditions.checkNotNull(listener);
        synchronized (mListeners) {
            if (mListeners.contains(listener)) {
                throw new IllegalArgumentException("Listener "
                        + listener + " already registered.");
            }
            mListeners.add(listener);
        }
    }

    private void unListen(IPlaybackListener listener) {
        Preconditions.checkNotNull(listener);
        synchronized (mListeners) {
            if (!mListeners.contains(listener)) {
                throw new IllegalArgumentException("Listener "
                        + listener + " not registered.");
            }
            mListeners.remove(listener);
        }
    }

    private int getPlayMode() {
        return 0;
    }

    private void setPlayMode(int mode) {

    }

    private boolean isPlaying() {
        return getState() == State.Playing;
    }

    private void notifyPlaying(IMediaTrack track) {

        synchronized (mListeners) {

            for (IPlaybackListener listener : mListeners) {
                try {
                    listener.onPlayerPlaying(track);
                } catch (RemoteException e) {

                }
            }
        }
    }

    private void notifyPaused(IMediaTrack track) {

        synchronized (mListeners) {

            for (IPlaybackListener listener : mListeners) {
                try {
                    listener.onPlayerPaused(track);
                } catch (RemoteException e) {

                }
            }
        }
    }

    private void notifyResume(IMediaTrack track) {

        synchronized (mListeners) {

            for (IPlaybackListener listener : mListeners) {
                try {
                    listener.onPlayerResume(track);
                } catch (RemoteException e) {

                }
            }
        }
    }

    private void notifyStart(IMediaTrack track) {

        synchronized (mListeners) {

            for (IPlaybackListener listener : mListeners) {
                try {
                    listener.onPlayerStart(track);
                } catch (RemoteException e) {

                }
            }
        }
    }

    private void notifyStop(IMediaTrack track) {

        synchronized (mListeners) {

            for (IPlaybackListener listener : mListeners) {
                try {
                    listener.onPlayerStop(track);
                } catch (RemoteException e) {

                }
            }
        }
    }

    private void notifyComplete(IMediaTrack track) {

        synchronized (mListeners) {

            for (IPlaybackListener listener : mListeners) {
                try {
                    listener.onCompletion(track);
                } catch (RemoteException e) {

                }
            }
        }
    }

    private void notifyError(int errNo, String message) {

        synchronized (mListeners) {

            for (IPlaybackListener listener : mListeners) {
                try {
                    listener.onError(errNo, message);
                } catch (RemoteException e) {

                }
            }
        }
    }

    private void next() {
        int next = tracks.indexOf(mPlayer.getCurrent());
        switch (getPlayMode()) {
            case PlayMode.MODE_LIST:
                next = next + 1 == tracks.size() ? 0 : next + 1;
                break;
            case PlayMode.MODE_RANDOM:
                next = new Random(100).nextInt(tracks.size() - 1);
                break;
            case PlayMode.MODE_REPEAT_ALL:
                next = next++ == tracks.size() ? 0 : next + 1;
                break;
            case PlayMode.MODE_REPEAT_ONE:
                break;
        }
        play(tracks.get(next));
    }

    private void previous() {
        int next = tracks.indexOf(mPlayer.getCurrent());
        switch (getPlayMode()) {
            case PlayMode.MODE_LIST:
                next = next - 1 < 0 ? 0 : next - 1;
                break;
            case PlayMode.MODE_RANDOM:
                next = new Random(100).nextInt(tracks.size() - 1);
                break;
            case PlayMode.MODE_REPEAT_ALL:
                next = next - 1 < 0 ? 0 : next - 1;
                break;
            case PlayMode.MODE_REPEAT_ONE:
                break;
        }
        play(tracks.get(next));
    }

    private State getState() {
        return mState;
    }

    private void setState(State state) {
        this.mState = state;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mStub;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        next();
        notifyComplete(mPlayer.getCurrent());
    }

    private enum State {
        Playing, Paused, Stopped, Idle
    }

    public interface PlayMode {
        public final int MODE_REPEAT_ALL = 0x1;
        public final int MODE_REPEAT_ONE = 0x2;
        public final int MODE_RANDOM = 0x3;
        public final int MODE_LIST = 0x4;
    }

    public static class Proxy extends ServiceProxy implements IMusicPlayerService {

        private IMusicPlayerService mService;

        public Proxy(Context context) {
            super(context, getIntent(context));
            context.startService(getIntent(context));
        }

        private static Intent getIntent(Context context) {
            Intent intent = new Intent();
            intent.setClass(context, MediaPlayerService.class);
            intent.setPackage(context.getPackageName());
            return intent;
        }

        public static void play(final IMediaTrack track, Context context) {
            try {
                new Proxy(context).play(track);
            } catch (RemoteException e) {

            }
        }

        public static void pause(Context context) {
            try {
                new Proxy(context).pause();
            } catch (RemoteException e) {

            }
        }

        public static void stop(Context context) {
            try {
                new Proxy(context).stop();
            } catch (RemoteException e) {

            }
        }

        public static void resume(Context context) {
            try {
                new Proxy(context).resume();
            } catch (RemoteException e) {

            }
        }

        public static void listen(IPlaybackListener listener, Context context) {
            try {
                new Proxy(context).listen(listener);
            } catch (RemoteException e) {

            }
        }

        public static void unListen(IPlaybackListener listener, Context context) {
            try {
                new Proxy(context).unListen(listener);
            } catch (RemoteException e) {

            }
        }

        public static void next(Context context) {
            try {
                new Proxy(context).next();
            } catch (RemoteException e) {

            }
        }

        public static void previous(Context context) {
            try {
                new Proxy(context).previous();
            } catch (RemoteException e) {

            }
        }

        public static void setPlayMode(int mode, Context context) {
            try {
                new Proxy(context).setPlayMode(mode);
            } catch (RemoteException e) {

            }
        }

        public static int getPlayMode(Context context) {
            try {
                return new Proxy(context).getPlayMode();
            } catch (RemoteException e) {

            }
            return PlayMode.MODE_LIST;
        }

        public static void assumePendingList(final List<IMediaTrack> tracks, Context context) {
            try {
                new Proxy(context).assumePendingList(tracks);
            } catch (RemoteException e) {

            }
        }

        @Override
        public void onConnected(IBinder binder) {
            mService = IMusicPlayerService.Stub.asInterface(binder);
        }

        @Override
        public void play(final IMediaTrack track) throws RemoteException {
            setTask(new ProxyTask() {
                @Override
                public void run() throws RemoteException {
                    mService.play(track);
                }
            }, "play");
        }

        @Override
        public void assumePendingList(final List<IMediaTrack> tracks) throws RemoteException {
            setTask(new ProxyTask() {
                @Override
                public void run() throws RemoteException {
                    mService.assumePendingList(tracks);
                }
            }, "assumePendingList");
        }

        @Override
        public void pause() throws RemoteException {
            setTask(new ProxyTask() {
                @Override
                public void run() throws RemoteException {
                    mService.pause();
                }
            }, "pause");
        }

        @Override
        public void resume() throws RemoteException {
            setTask(new ProxyTask() {
                @Override
                public void run() throws RemoteException {
                    mService.resume();
                }
            }, "resume");
        }

        @Override
        public void stop() throws RemoteException {
            setTask(new ProxyTask() {
                @Override
                public void run() throws RemoteException {
                    mService.stop();
                }
            }, "stop");
        }

        @Override
        public void next() throws RemoteException {
            setTask(new ProxyTask() {
                @Override
                public void run() throws RemoteException {
                    mService.next();
                }
            }, "next");
        }

        @Override
        public void previous() throws RemoteException {
            setTask(new ProxyTask() {
                @Override
                public void run() throws RemoteException {
                    mService.previous();
                }
            }, "previous");
        }

        @Override
        public int getPlayMode() throws RemoteException {
            return 0;
        }

        @Override
        public void setPlayMode(final int mode) throws RemoteException {
            setTask(new ProxyTask() {
                @Override
                public void run() throws RemoteException {
                    mService.setPlayMode(mode);
                }
            }, "setPlayMode");
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            return false;
        }

        @Override
        public void listen(final IPlaybackListener listener) throws RemoteException {
            setTask(new ProxyTask() {
                @Override
                public void run() throws RemoteException {
                    mService.listen(listener);
                }
            }, "listen");
        }

        @Override
        public void unListen(final IPlaybackListener listener) throws RemoteException {
            setTask(new ProxyTask() {
                @Override
                public void run() throws RemoteException {
                    mService.unListen(listener);
                }
            }, "unListen");
        }

        @Override
        public IBinder asBinder() {
            return null;
        }
    }

    class ServiceStub extends IMusicPlayerService.Stub {

        @Override
        public void play(IMediaTrack track) throws RemoteException {
            MediaPlayerService.this.play(track);
        }

        @Override
        public void assumePendingList(List<IMediaTrack> tracks) throws RemoteException {
            MediaPlayerService.this.assumePendingList(tracks);
        }

        @Override
        public void pause() throws RemoteException {
            MediaPlayerService.this.pause();
        }

        @Override
        public void resume() throws RemoteException {
            MediaPlayerService.this.resume();
        }

        @Override
        public void stop() throws RemoteException {
            MediaPlayerService.this.stop();
        }

        @Override
        public void next() throws RemoteException {
            MediaPlayerService.this.next();
        }

        @Override
        public void previous() throws RemoteException {
            MediaPlayerService.this.previous();
        }

        @Override
        public int getPlayMode() throws RemoteException {
            return MediaPlayerService.this.getPlayMode();
        }

        @Override
        public void setPlayMode(int mode) throws RemoteException {
            MediaPlayerService.this.setPlayMode(mode);
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            return MediaPlayerService.this.isPlaying();
        }

        @Override
        public void listen(IPlaybackListener listener) throws RemoteException {
            MediaPlayerService.this.listen(listener);
        }

        @Override
        public void unListen(IPlaybackListener listener) throws RemoteException {
            MediaPlayerService.this.unListen(listener);
        }
    }

    private class LazyPlayer extends MediaPlayer {

        IMediaTrack current;

        public IMediaTrack getCurrent() {
            return current;
        }

        public void setCurrent(IMediaTrack current) {
            this.current = current;
        }
    }
}
