//
// Created by Administrator on 2018/5/15.
//
#include <jni.h>
#ifndef BEE_GLOBAL_GLOBAL_H
#define BEE_GLOBAL_GLOBAL_H

struct Status{
    int symbolSetCreate, symbolSetDestroy;
    int symbolCreate, symbolDestroy;
    int imageCreate, imageDestroy;
    int imageScannerCreate, imageScannerDestroy;
};
#define PEER_CAST(l) ((void*)(uintptr_t)(l))
#define GET_PEER(c, o) PEER_CAST((*env)->GetLongField(env, (o), c ## _peer))
#define VALIDATE_CROP(u, m) \
    if((u) < 0) {           \
        (m) += (u);         \
        (u) = 0;            \
    }
void setSymbolSetPeer(JNIEnv *env, jclass cls);
jfieldID getSymbolSetPeer(JNIEnv*,jobject);
void setSymbolPeer(JNIEnv *env, jclass cls);
jfieldID getSymbolPeer(JNIEnv*,jobject);
void setImagePeer(JNIEnv *env, jclass cls);
jfieldID getImagePeer(JNIEnv*,jobject);
void setImageData(JNIEnv *env, jclass cls);
jfieldID getImageData(JNIEnv*,jobject);
void setImageScannerPeer(JNIEnv *env, jclass cls);
jfieldID getImageScannerPeer(JNIEnv*,jobject);
struct Status getStatus();
void addImageCreate();
void addImageDestroy();
void addSymbolSetCreate();
void addSymbolSetDestroy();
void addSymbolCreate();
void addSymbolDestroy();
void addImageScannerCreate();
void addImageScannerDestroy();
inline void throw_exc(JNIEnv *env, const char *name, const char *msg);
#endif //BEE_GLOBAL_GLOBAL_H
