// IPlaybackListener.aidl
package com.nick.yinheng.service;

import com.nick.yinheng.model.IMediaTrack;

interface IPlaybackListener {
    void onPlayerStart(in IMediaTrack track);
    void onPlayerPlaying(in IMediaTrack track);
    void onPlayerPaused(in IMediaTrack track);
    void onPlayerResume(in IMediaTrack track);
    void onPlayerStop(in IMediaTrack track);
    void onCompletion(in IMediaTrack track);
    void onError(int errNo, String errMsg);

}
