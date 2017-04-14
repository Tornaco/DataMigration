#include <jni.h>
#include "mp3/mp3.h"

extern "C"
jstring
Java_org_newstand_datamigration_secure_DonateQRPathRetriever_getPathForDonateQRImage(
        JNIEnv *env,
        jobject /* this */) {
    std::string path = "https://raw.githubusercontent.com/Tornaco/DataMigration/master/HELP.md";
    jstring res = env->NewStringUTF(path.c_str());
    return res;
}