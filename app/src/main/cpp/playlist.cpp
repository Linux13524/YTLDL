#include <jni.h>
#include "handle.h"
#include "YoutubeListDownloader/youtube/playlist.h"

#ifndef PLATFORM_ANDROID_PLAYLIST_CPP
#define PLATFORM_ANDROID_PLAYLIST_CPP

extern "C" {

// getNative
JNIEXPORT jlong JNICALL
Java_de_linux13524_ytldl_jniwrapper_Playlist_00024Companion_getNative
        (JNIEnv *env, jobject thiz, jstring id_) {

    const char *id = env->GetStringUTFChars(id_, 0);

    Youtube::Playlist *playlist = new Youtube::Playlist(Youtube::Playlist::Get(id));

    return (jlong) playlist;
}

// getId
JNIEXPORT jstring JNICALL
Java_de_linux13524_ytldl_jniwrapper_Playlist_getId
        (JNIEnv *env, jobject thiz) {

    Youtube::Playlist *playlist = getHandle<Youtube::Playlist>(env, thiz);

    return env->NewStringUTF(playlist->GetId().c_str());
}

// getChannelId
JNIEXPORT jstring JNICALL
Java_de_linux13524_ytldl_jniwrapper_Playlist_getChannelId
        (JNIEnv *env, jobject thiz) {

    Youtube::Playlist *playlist = getHandle<Youtube::Playlist>(env, thiz);

    return env->NewStringUTF(playlist->GetChannelId().c_str());
}

// getTitle
JNIEXPORT jstring JNICALL
Java_de_linux13524_ytldl_jniwrapper_Playlist_getTitle
        (JNIEnv *env, jobject thiz) {

    Youtube::Playlist *playlist = getHandle<Youtube::Playlist>(env, thiz);

    return env->NewStringUTF(playlist->GetTitle().c_str());
}

// loadVideos
JNIEXPORT void JNICALL
Java_de_linux13524_ytldl_jniwrapper_Playlist_loadVideos
        (JNIEnv *env, jobject thiz) {

    Youtube::Playlist *playlist = getHandle<Youtube::Playlist>(env, thiz);

    playlist->LoadVideos()->join();
}

// downloadVideos
JNIEXPORT void JNICALL
Java_de_linux13524_ytldl_jniwrapper_Playlist_downloadVideos
        (JNIEnv *env, jobject thiz, jintArray itags_, jobject callbackProgress_, jstring folder_) {

    Youtube::Playlist *playlist = getHandle<Youtube::Playlist>(env, thiz);
    const char *folder = env->GetStringUTFChars(folder_, 0);

    jsize size = env->GetArrayLength(itags_);
    std::vector<int> vItags(size);

    jint *itags = env->GetIntArrayElements(itags_, NULL);

    for (int i = 0; i < size; i++) {
        vItags[i] = itags[i];
    }

    jclass lClass = env->FindClass("de/linux13524/ytldl/jniwrapper/Playlist$ProgressCallback");
    jclass gClass = reinterpret_cast<jclass>(env->NewGlobalRef(lClass));

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

    playlist->DownloadVideos(vItags, f, folder);

    env->ReleaseIntArrayElements(itags_, itags, 0);
}

}

#endif

