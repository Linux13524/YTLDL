#include <jni.h>
#include "handle.h"
#include "YoutubeListDownloader/youtube/channel.h"

#ifndef PLATFORM_ANDROID_CHANNEL_CPP
#define PLATFORM_ANDROID_CHANNEL_CPP

extern "C" {

// getNative
JNIEXPORT jlong JNICALL
Java_de_linux13524_ytldl_jniwrapper_Channel_00024Companion_getNative
        (JNIEnv *env, jobject thiz, jstring id_, jboolean isUsername) {

    const char *id = env->GetStringUTFChars(id_, nullptr);

    Youtube::Channel *Channel = new Youtube::Channel(Youtube::Channel::Get(id, isUsername));

    return (jlong) Channel;
}

// getId
JNIEXPORT jstring JNICALL
Java_de_linux13524_ytldl_jniwrapper_Channel_getId
        (JNIEnv *env, jobject thiz) {

    auto *Channel = getHandle<Youtube::Channel>(env, thiz);

    return env->NewStringUTF(Channel->GetId().c_str());
}

// getTitle
JNIEXPORT jstring JNICALL
Java_de_linux13524_ytldl_jniwrapper_Channel_getTitle
        (JNIEnv *env, jobject thiz) {

    auto *Channel = getHandle<Youtube::Channel>(env, thiz);

    return env->NewStringUTF(Channel->GetTitle().c_str());
}

// loadVideos
JNIEXPORT void JNICALL
Java_de_linux13524_ytldl_jniwrapper_Channel_loadVideos
        (JNIEnv *env, jobject thiz) {

    auto *Channel = getHandle<Youtube::Channel>(env, thiz);

    Channel->LoadVideos()->join();
}

// downloadVideos
JNIEXPORT void JNICALL
Java_de_linux13524_ytldl_jniwrapper_Channel_downloadVideos
        (JNIEnv *env, jobject thiz, jobject callbackProgress_) {

    auto *channel = getHandle<Youtube::Channel>(env, thiz);

    auto lClass = env->FindClass("de/linux13524/ytldl/jniwrapper/Channel$ProgressCallback");
    auto gClass = reinterpret_cast<jclass>(env->NewGlobalRef(lClass));

    jobject gCallbackProgress = env->NewGlobalRef(callbackProgress_);

    JavaVM *jvm;
    env->GetJavaVM(&jvm);
    auto f = [jvm, gClass, gCallbackProgress](size_t progress, size_t total) {
        JNIEnv *lEnv;
        bool needsDetach = false;
        if (jvm->GetEnv((void **) &lEnv, JNI_VERSION_1_6) == JNI_EDETACHED) {
            jvm->AttachCurrentThread(&lEnv, nullptr);
            needsDetach = true;
        }

        jmethodID method = lEnv->GetMethodID(gClass, "update", "(II)V");
        lEnv->CallVoidMethod(gCallbackProgress, method, progress, total);

        if (progress == total) {
            lEnv->DeleteGlobalRef(gCallbackProgress);
            lEnv->DeleteGlobalRef(gClass);
        }

        if (needsDetach) jvm->DetachCurrentThread();
    };

    channel->DownloadVideos(f);
}

}

#endif

