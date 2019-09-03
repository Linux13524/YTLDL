#include <jni.h>
#include "../handle.h"
#include "YoutubeListDownloader/download/options.h"
#include "YoutubeListDownloader/youtube/video.h"

#ifndef PLATFORM_ANDROID_DOWNLOAD_GLOBAL_OPTIONS_CPP
#define PLATFORM_ANDROID_DOWNLOAD_GLOBAL_OPTIONS_CPP

extern "C" {

// setSaveVideoName
JNIEXPORT void JNICALL
Java_de_linux13524_ytldl_jniwrapper_download_GlobalOptions_setSaveVideoName
        (JNIEnv *env, jobject thiz, jboolean save_video_name_) {

    Download::Options::GlobalOptions().m_save_video_name = save_video_name_;
}

// setItags
JNIEXPORT void JNICALL
Java_de_linux13524_ytldl_jniwrapper_download_GlobalOptions_setItags
        (JNIEnv *env, jobject thiz, jintArray itags_) {

    jsize size = env->GetArrayLength(itags_);
    std::vector<int> vItags(size);

    jint *itags = env->GetIntArrayElements(itags_, nullptr);

    for (int i = 0; i < size; i++) {
        vItags[i] = itags[i];
    }

    Download::Options::GlobalOptions().m_itags = vItags;

    env->ReleaseIntArrayElements(itags_, itags, 0);
}

// setPath
JNIEXPORT void JNICALL
Java_de_linux13524_ytldl_jniwrapper_download_GlobalOptions_setPath
        (JNIEnv *env, jobject thiz, jstring path_) {

    const char *path = env->GetStringUTFChars(path_, nullptr);

    Download::Options::GlobalOptions().m_path = path;
}
#endif

}