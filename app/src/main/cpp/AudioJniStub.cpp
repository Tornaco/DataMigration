#include <jni.h>
#include <android/log.h>
#include "Logger.h"
#include "mp3/mp3.h"

extern "C"
jstring
Java_org_newstand_datamigration_utils_TrackUtils_getArtist(
        JNIEnv *env,
        jobject /* this */,
        jstring path) {

    const char *str;
    str = env->GetStringUTFChars(path, nullptr);
    if (str == nullptr) {
        LOGE("null path");
        return nullptr;
    }

    LOGD("Extract %s", str);

    MP3 mp3(str);

    std::string artist = mp3.get_artist();

    jstring res = env->NewStringUTF(artist.c_str());
    return res;
}

extern "C"
void
Java_org_newstand_datamigration_utils_TrackUtils_extractArt(
        JNIEnv *env,
        jobject /* this */,
        jstring path,
        jstring pathTo) {

    const char *source;
    source = env->GetStringUTFChars(path, nullptr);
    if (source == nullptr) {
        LOGE("null path");
        return;
    }

    const char *to;
    to = env->GetStringUTFChars(pathTo, nullptr);
    if (to == nullptr) {
        LOGE("null path");
        return;
    }

    LOGD("Extract delegate %s to %s", source, to);

    MP3 mp3(source);

    mp3.save_pic(to);
}
