//
// Created by guohao4 on 2017/4/21.
//

#include <string>
#include "SecretKey.h"

jstring
Java_org_newstand_datamigration_secure_EncryptManager_createSecretKey(JNIEnv *env, jobject thizz,
                                                                      jobject thiz) {
    std::string code = "qodqodjqrjqp33dkqpe9210-pd3je24u";
    jstring secretKey = env->NewStringUTF(code.c_str());
    return secretKey;
}
