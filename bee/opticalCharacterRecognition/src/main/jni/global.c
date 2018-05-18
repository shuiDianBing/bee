//
// Created by Administrator on 2018/5/15.
//
#include <jni.h>
#include "global.h"
static jfieldID SymbolSet_peer;
static jfieldID Symbol_peer;
static jfieldID Image_peer, Image_data;
static jfieldID ImageScanner_peer;

static struct Status status;
void setSymbolSetPeer(JNIEnv *env, jclass cls){
    SymbolSet_peer = (*env)->GetFieldID(env, cls, "peer", "J");
}
jfieldID getSymbolSetPeer(JNIEnv *env,jobject obj){
    return GET_PEER(env,SymbolSet,obj);
}
void setSymbolPeer(JNIEnv *env, jclass cls){
    Symbol_peer = (*env)->GetFieldID(env, cls, "peer", "J");
}
jfieldID getSymbolPeer(JNIEnv *env,jobject obj){
    return GET_PEER(env,Symbol,obj);
}
void setImagePeer(JNIEnv *env, jclass cls){
    Image_peer = (*env)->GetFieldID(env,cls,"peer","J");
}
jfieldID getImagePeer(JNIEnv *env,jobject obj){
    return GET_PEER(env,Image,obj);
}
void setImageData(JNIEnv *env, jclass cls){
    Image_data = (*env)->GetFieldID(env,cls,"data","Ljava/lang/Object;");
}
jfieldID getImageData(JNIEnv *env,jobject obj){
    return GET_PEER(env,ImageData,obj);
}
void setImageScannerPeer(JNIEnv *env, jclass cls){
    ImageScanner_peer = (*env)->GetFieldID(env, cls, "peer", "J");
}
jfieldID getImageScannerPeer(JNIEnv env,jobject obj){
    return GET_PEER(env,ImageScanner,obj);
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