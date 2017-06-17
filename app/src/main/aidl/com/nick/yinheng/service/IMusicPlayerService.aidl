// IMusicPlayerService.aidl
package com.nick.yinheng.service;

import com.nick.yinheng.service.IPlaybackListener;
import com.nick.yinheng.model.IMediaTrack;

interface IMusicPlayerService {

    void play(in IMediaTrack track);
    void assumePendingList(in List<IMediaTrack> tracks);

    void pause();
    void resume();
    void stop();

    void next();
    void previous();

    void setPlayMode(int mode);
    int getPlayMode();

    boolean isPlaying();

    void listen(in IPlaybackListener listener);
    void unListen(in IPlaybackListener listener);
}
