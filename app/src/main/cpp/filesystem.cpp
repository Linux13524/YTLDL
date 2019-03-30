#include <jni.h>
#include "YoutubeListDownloader/utils/fs.h"

extern "C" {

// setDbPath
JNIEXPORT void JNICALL
Java_de_linux13524_ytldl_jniwrapper_Filesystem_00024Settings_setDbPath
(JNIEnv *env, jobject thiz, jstring path_) {

    const char *path = env->GetStringUTFChars(path_, nullptr);

    Filesystem::Settings::Instance().SetDbPath(path);
}

// setVideoPath
JNIEXPORT void JNICALL
Java_de_linux13524_ytldl_jniwrapper_Filesystem_00024Settings_setVideoPath
        (JNIEnv *env, jobject thiz, jstring path_) {

    const char *path = env->GetStringUTFChars(path_, nullptr);

    Filesystem::Settings::Instance().SetVideoPath(path);
}

}

