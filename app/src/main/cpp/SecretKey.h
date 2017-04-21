//
// Created by guohao4 on 2017/4/21.
//

#ifndef DATAMIGRATION_SECRETKEY_H
#define DATAMIGRATION_SECRETKEY_H

#include <jni.h>

#define UTF_8 "UTF-8"
#ifdef __cplusplus
extern "C" {
jstring
Java_org_newstand_datamigration_secure_EncryptManager_createSecretKey(JNIEnv *, jobject, jobject);
}
#endif

#endif //DATAMIGRATION_SECRETKEY_H
