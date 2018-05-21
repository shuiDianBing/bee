//
// Created by Administrator on 2018/5/15.
//
#include <jni.h>
#include <inttypes.h>
#include <assert.h>
#include <zbar.h>
#include "global.h"

static struct Status status;
void setSymbolSetPeer(JNIEnv *env, jclass cls){
    SymbolSet_peer = (*env)->GetFieldID(env, cls, "peer", "J");
}
jfieldID getSymbolSetPeer(JNIEnv *env,jobject obj){
    return GET_PEER(SymbolSet,obj);
}
void setSymbolPeer(JNIEnv *env, jclass cls){
    Symbol_peer = (*env)->GetFieldID(env, cls, "peer", "J");
}
jfieldID getSymbolPeer(JNIEnv *env,jobject obj){
    return GET_PEER(Symbol,obj);
}
void setImagePeer(JNIEnv *env, jclass cls){
    Image_peer = (*env)->GetFieldID(env,cls,"peer","J");
}
jfieldID getImagePeer(JNIEnv *env,jobject obj){
    return GET_PEER(Image,obj);
}
void setImageData(JNIEnv *env, jclass cls){
    Image_data = (*env)->GetFieldID(env,cls,"data","Ljava/lang/Object;");
}
jfieldID getImageData(JNIEnv *env,jobject obj){
    return Image_data;//GET_PEER(ImageData,obj);
}
void setImageScannerPeer(JNIEnv *env, jclass cls){
    ImageScanner_peer = (*env)->GetFieldID(env, cls, "peer", "J");
}
jfieldID getImageScannerPeer(JNIEnv *env,jobject obj){
    return GET_PEER(ImageScanner,obj);
}

struct Status getStatus(){
    return status;
}
void addImageCreate(){
    status.imageCreate++;
}
void addImageDestroy(){
    status.imageDestroy++;
}
void addSymbolSetCreate(){
    status.symbolSetCreate++;
}
void addSymbolSetDestroy(){
    status.symbolSetDestroy++;
}
void addSymbolCreate(){
    status.symbolCreate++;
}
void addSymbolDestroy(){
    status.symbolDestroy++;
}
void addImageScannerCreate(){
    status.imageScannerCreate++;
}
void addImageScannerDestroy(){
    status.imageScannerDestroy++;
}
inline void throw_exc(JNIEnv *env, const char *name, const char *msg){
    jclass cls = (*env)->FindClass(env, name);
    if(cls)
        (*env)->ThrowNew(env, cls, msg);
    (*env)->DeleteLocalRef(env, cls);
}