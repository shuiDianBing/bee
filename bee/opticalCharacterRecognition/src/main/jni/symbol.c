//
// Created by Administrator on 2018/5/15.
//
#include <inttypes.h>
#include <assert.h>
#include <zbar.h>
#include "global.h"
#include "symbol.h"
/*
 * Class:     com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Symbol
 * Method:    init
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Symbol_init
        (JNIEnv *env, jclass cls){
    setSymbolPeer();//Symbol_peer = (*env)->GetFieldID(env, cls, "peer", "J");
}

/*
 * Class:     com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Symbol
 * Method:    destroy
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Symbol_destroy
        (JNIEnv *env, jobject obj, jlong peer){
    zbar_symbol_ref(PEER_CAST(peer), -1);
    addSymbolDestroy();
}

/*
 * Class:     com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Symbol
 * Method:    getType
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Symbol_getType
        (JNIEnv *env, jobject obj, jlong peer){
    return zbar_symbol_get_type(PEER_CAST(peer));
}

/*
 * Class:     com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Symbol
 * Method:    getConfigMask
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Symbol_getConfigMask
        (JNIEnv *env, jobject obj){
    return zbar_symbol_get_configs(getSymbolPeer(obj));//(GET_PEER(Symbol, obj));
}

/*
 * Class:     com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Symbol
 * Method:    getModifierMask
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Symbol_getModifierMask
        (JNIEnv *, jobject);

/*
 * Class:     com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Symbol
 * Method:    getData
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Symbol_getData
        (JNIEnv *env, jobject obj){
    const char *data = zbar_symbol_get_data(getSymbolPeer(obj));//(GET_PEER(Symbol, obj));
    return(*env)->NewStringUTF(env, data);
}

/*
 * Class:     com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Symbol
 * Method:    getDataBytes
 * Signature: ()[B
 */
JNIEXPORT jbyteArray JNICALL Java_com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Symbol_getDataBytes
        (JNIEnv *env, jobject obj){
    const zbar_symbol_t *zsym = getSymbolPeer(obj);//GET_PEER(Symbol, obj);
    const void *data = zbar_symbol_get_data(zsym);
    unsigned long datalen = zbar_symbol_get_data_length(zsym);
    if(!data || !datalen)
        return NULL;
    jbyteArray bytes = (*env)->NewByteArray(env, datalen);
    if(!bytes)
        return NULL;
    (*env)->SetByteArrayRegion(env, bytes, 0, datalen, data);
    return bytes;
}

/*
 * Class:     com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Symbol
 * Method:    getQuality
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Symbol_getQuality
        (JNIEnv *env, jobject obj){
    return zbar_symbol_get_quality(getSymbolPeer(obj));//(GET_PEER(Symbol, obj));
}

/*
 * Class:     com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Symbol
 * Method:    getCount
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Symbol_getCount
        (JNIEnv *env, jobject obj){
    return zbar_symbol_get_count(getSymbolPeer(obj));//(GET_PEER(Symbol, obj));
}

/*
 * Class:     com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Symbol
 * Method:    getLocationSize
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Symbol_getLocationSize
        (JNIEnv *env, jobject obj, jlong peer){
    return zbar_symbol_get_loc_size(PEER_CAST(peer));
}

/*
 * Class:     com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Symbol
 * Method:    getLocationX
 * Signature: (JI)I
 */
JNIEXPORT jint JNICALL Java_com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Symbol_getLocationX
        (JNIEnv *env, jobject obj, jlong peer, jint idx){
    return zbar_symbol_get_loc_x(PEER_CAST(peer), idx);
}

/*
 * Class:     com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Symbol
 * Method:    getLocationY
 * Signature: (JI)I
 */
JNIEXPORT jint JNICALL Java_com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Symbol_getLocationY
        (JNIEnv *env, jobject obj, jlong peer, jint idx){
    return zbar_symbol_get_loc_y(PEER_CAST(peer), idx);
}

/*
 * Class:     com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Symbol
 * Method:    getOrientation
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Symbol_getOrientation
        (JNIEnv *env, jobject obj){
    return zbar_symbol_get_orientation(getSymbolPeer(env,obj));//(GET_PEER(Symbol, obj));
}

/*
 * Class:     com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Symbol
 * Method:    getComponents
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Symbol_getComponents
        (JNIEnv *env, jobject obj, jlong peer){
    const zbar_symbol_set_t *zsyms = zbar_symbol_get_components(PEER_CAST(peer));
    if(zsyms) {
        zbar_symbol_set_ref(zsyms, 1);
        addSymbolSetCreate();
    }
    return (intptr_t)zsyms;
}

/*
 * Class:     com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Symbol
 * Method:    next
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Symbol_next
        (JNIEnv *env, jobject obj){
    const zbar_symbol_t *zsym = zbar_symbol_next(getSymbolPeer(env,obj));//(GET_PEER(Symbol, obj));
    if(zsym) {
        zbar_symbol_ref(zsym, 1);
        addSymbolCreate();
    }
    return(intptr_t)zsym;
}

