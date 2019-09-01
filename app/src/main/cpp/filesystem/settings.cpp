#include <jni.h>
#include "YoutubeListDownloader/utils/fs.h"

#ifndef PLATFORM_ANDROID_FILESYSTEM_SETTINGS_CPP
#define PLATFORM_ANDROID_FILESYSTEM_SETTINGS_CPP

extern "C" {

// setDbPath
JNIEXPORT void JNICALL
Java_de_linux13524_ytldl_jniwrapper_filesystem_Settings_setDbPath
(JNIEnv *env, jobject thiz, jstring path_) {

    const char *path = env->GetStringUTFChars(path_, nullptr);

    Filesystem::Settings::Instance().SetDbPath(path);
}

}
#endif

