#include <jni.h>
#include "handle.h"
#include "YoutubeListDownloader/youtube/video.h"

#ifndef PLATFORM_ANDROID_VIDEO_CPP
#define PLATFORM_ANDROID_VIDEO_CPP

extern "C" {

// getNative
JNIEXPORT jlong JNICALL
Java_de_linux13524_ytldl_jniwrapper_Video_00024Companion_getNative
        (JNIEnv *env, jobject thiz, jstring id_) {

    const char *id = env->GetStringUTFChars(id_, nullptr);

    Youtube::Video *video = new Youtube::Video(Youtube::Video::Get(id));

    return (jlong) video;
}

// getId
JNIEXPORT jstring JNICALL
Java_de_linux13524_ytldl_jniwrapper_Video_getId
        (JNIEnv *env, jobject thiz) {

    auto *video = getHandle<Youtube::Video>(env, thiz);

    return env->NewStringUTF(video->GetId().c_str());
}

// getChannelId
JNIEXPORT jstring JNICALL
Java_de_linux13524_ytldl_jniwrapper_Video_getChannelId
        (JNIEnv *env, jobject thiz) {

    auto *video = getHandle<Youtube::Video>(env, thiz);

    return env->NewStringUTF(video->GetChannelId().c_str());
}

// getTitle
JNIEXPORT jstring JNICALL
Java_de_linux13524_ytldl_jniwrapper_Video_getTitle
        (JNIEnv *env, jobject thiz) {

    auto *video = getHandle<Youtube::Video>(env, thiz);

    return env->NewStringUTF(video->GetTitle().c_str());
}

// getDescription
JNIEXPORT jstring JNICALL
Java_de_linux13524_ytldl_jniwrapper_Video_getDescription
        (JNIEnv *env, jobject thiz) {

    auto *video = getHandle<Youtube::Video>(env, thiz);

    return env->NewStringUTF(video->GetDescription().c_str());
}

// getTags
JNIEXPORT jobject JNICALL
Java_de_linux13524_ytldl_jniwrapper_Video_getTags
        (JNIEnv *env, jobject thiz) {

    auto *video = getHandle<Youtube::Video>(env, thiz);

    jclass clazzArrayList = env->FindClass("java/util/ArrayList");

    jobject returnList = env->NewObject(clazzArrayList,
                                        env->GetMethodID(clazzArrayList, "<init>", "()V"));

    for (auto &t: video->GetTags()) {
        jstring tag = env->NewStringUTF(t.c_str());

        env->CallBooleanMethod(returnList, env->GetMethodID(clazzArrayList, "add", "(Ljava/lang/Object;)Z"), tag);
    }

    return returnList;
}

// getQualities
JNIEXPORT jobject JNICALL
Java_de_linux13524_ytldl_jniwrapper_Video_getQualities
        (JNIEnv *env, jobject thiz) {

    auto *video = getHandle<Youtube::Video>(env, thiz);

    jclass clazzArrayList = env->FindClass("java/util/ArrayList");
    jclass clazzQuality = env->FindClass(
            "de/linux13524/ytldl/jniwrapper/Video$Quality");

    jobject returnList = env->NewObject(clazzArrayList,
                                        env->GetMethodID(clazzArrayList, "<init>", "()V"));

    for (auto &q : video->GetQualities()) {

        jstring url = env->NewStringUTF(q.m_url.c_str());

        jmethodID id = env->GetMethodID(clazzQuality, "<init>",
                                        "(Lde/linux13524/ytldl/jniwrapper/Video;ILjava/lang/String;)V");

        jobject quality = env->NewObject(clazzQuality, id, thiz, q.m_itag, url);


        env->CallBooleanMethod(returnList,
                               env->GetMethodID(clazzArrayList, "add", "(Ljava/lang/Object;)Z"),
                               quality);
    }


    return returnList;
}

// loadThumbnail
JNIEXPORT void JNICALL
Java_de_linux13524_ytldl_jniwrapper_Video_loadThumbnail
        (JNIEnv *env, jobject thiz) {

    auto *video = getHandle<Youtube::Video>(env, thiz);

    video->LoadThumbnail();
}

// loadDownloadLinks
JNIEXPORT void JNICALL
Java_de_linux13524_ytldl_jniwrapper_Video_loadDownloadLinks
        (JNIEnv *env, jobject thiz) {

    auto *video = getHandle<Youtube::Video>(env, thiz);

    video->LoadDownloadLinks();
}

// printFormats
JNIEXPORT void JNICALL
Java_de_linux13524_ytldl_jniwrapper_Video_printFormats
        (JNIEnv *env, jobject thiz) {

    auto *video = getHandle<Youtube::Video>(env, thiz);

    video->PrintFormats();
}

// download
JNIEXPORT void JNICALL
Java_de_linux13524_ytldl_jniwrapper_Video_downloadDir
        (JNIEnv *env, jobject thiz, jintArray itags_, jstring folder_) {

    auto *video = getHandle<Youtube::Video>(env, thiz);
    const char *folder = env->GetStringUTFChars(folder_, nullptr);

    jsize size = env->GetArrayLength(itags_);
    std::vector<int> vItags(size);

    jint *itags = env->GetIntArrayElements(itags_, nullptr);

    for (int i = 0; i < size; i++) {
        vItags[i] = itags[i];
    }

    Youtube::Video::Download(*video, vItags, folder);

    env->ReleaseIntArrayElements(itags_, itags, 0);
}

// download
JNIEXPORT void JNICALL
Java_de_linux13524_ytldl_jniwrapper_Video_download
        (JNIEnv *env, jobject thiz, jintArray itags_) {

    auto *video = getHandle<Youtube::Video>(env, thiz);

    jsize size = env->GetArrayLength(itags_);
    std::vector<int> vItags(size);

    jint *itags = env->GetIntArrayElements(itags_, nullptr);

    for (int i = 0; i < size; i++) {
        vItags[i] = itags[i];
    }

    Youtube::Video::Download(*video, vItags);

    env->ReleaseIntArrayElements(itags_, itags, 0);
}

}
#endif
