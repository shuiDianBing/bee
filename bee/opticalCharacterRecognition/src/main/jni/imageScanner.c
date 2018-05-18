//
// Created by Administrator on 2018/5/15.
//

/*
 * Class:     com_stynet_shuidianbing_opticalcharacterrecognition_zbar_ImageScanner
 * Method:    init
 * Signature: ()V
 */
#include <inttypes.h>
#include <assert.h>
#include <zbar.h>
#include <android/log.h>
#include "imageScanner.h"
#include "global.h"

JNIEXPORT void JNICALL Java_com_stynet_shuidianbing_opticalcharacterrecognition_zbar_ImageScanner_init
(JNIEnv *env, jclass cls){
    setImageScannerPeer(env,cls);//ImageScanner_peer = (*env)->GetFieldID(env, cls, "peer", "J");
}

/*
 * Class:     com_stynet_shuidianbing_opticalcharacterrecognition_zbar_ImageScanner
 * Method:    create
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_com_stynet_shuidianbing_opticalcharacterrecognition_zbar_ImageScanner_create
        (JNIEnv *env, jobject obj){
    zbar_image_scanner_t *zscn = zbar_image_scanner_create();
    if(!zscn){
        throw_exc(env, "java/lang/OutOfMemoryError", NULL);
        return 0;
    }
    addImageScannerCreate();
    return (intptr_t)zscn;
}

/*
 * Class:     com_stynet_shuidianbing_opticalcharacterrecognition_zbar_ImageScanner
 * Method:    destroy
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_stynet_shuidianbing_opticalcharacterrecognition_zbar_ImageScanner_destroy
(JNIEnv *env, jobject obj, jlong peer){
    zbar_image_scanner_destroy(PEER_CAST(peer));
    addImageScannerDestroy();
}

/*
 * Class:     com_stynet_shuidianbing_opticalcharacterrecognition_zbar_ImageScanner
 * Method:    setConfig
 * Signature: (III)V
 */
JNIEXPORT void JNICALL Java_com_stynet_shuidianbing_opticalcharacterrecognition_zbar_ImageScanner_setConfig
(JNIEnv *env, jobject obj, jint symbology, jint config, jint value){
    zbar_image_scanner_set_config(getImageScannerPeer(env,obj),symbology,config,value);//(GET_PEER(ImageScanner, obj), symbology, config, value);
}

/*
 * Class:     com_stynet_shuidianbing_opticalcharacterrecognition_zbar_ImageScanner
 * Method:    parseConfig
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_stynet_shuidianbing_opticalcharacterrecognition_zbar_ImageScanner_parseConfig
(JNIEnv *env, jobject obj, jstring cfg){
    const char *cfgstr = (*env)->GetStringUTFChars(env, cfg, NULL);
    if(!cfgstr)
        return;
    if(zbar_image_scanner_parse_config(getImageScannerPeer(env,obj),cfgstr))//(GET_PEER(ImageScanner, obj), cfgstr))
        throw_exc(env, "java/lang/IllegalArgumentException", "unknown configuration");
}

/*
 * Class:     com_stynet_shuidianbing_opticalcharacterrecognition_zbar_ImageScanner
 * Method:    enableCache
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_com_stynet_shuidianbing_opticalcharacterrecognition_zbar_ImageScanner_enableCache
(JNIEnv *env, jobject obj, jboolean enable){
    zbar_image_scanner_enable_cache(getImageScannerPeer(env,obj),enable);//(GET_PEER(ImageScanner, obj), enable);
}

/*
 * Class:     com_stynet_shuidianbing_opticalcharacterrecognition_zbar_ImageScanner
 * Method:    getResults
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_com_stynet_shuidianbing_opticalcharacterrecognition_zbar_ImageScanner_getResults
        (JNIEnv *env, jobject obj, jlong peer){
    const zbar_symbol_set_t *zsyms = zbar_image_scanner_get_results(PEER_CAST(peer));
    if(zsyms) {
        zbar_symbol_set_ref(zsyms, 1);
        addSymbolSetCreate();
    }
    return (intptr_t)zsyms;
}

/*
 * Class:     com_stynet_shuidianbing_opticalcharacterrecognition_zbar_ImageScanner
 * Method:    scanImage
 * Signature: (Lcom/stynet/shuidianbing/opticalcharacterrecognition/zbar/Image;)I
 */
JNIEXPORT jint JNICALL Java_com_stynet_shuidianbing_opticalcharacterrecognition_zbar_ImageScanner_scanImage
        (JNIEnv *env, jobject obj, jobject image){
    zbar_image_scanner_t *zscn = getImageScannerPeer(env,obj);//GET_PEER(ImageScanner, obj);
    zbar_image_t *zimg = getImagePeer(env,image);//GET_PEER(Image, image);
    int n = zbar_scan_image(zscn, zimg);
    if(0> n)
        throw_exc(env, "java/lang/UnsupportedOperationException", "unsupported image format");
    return n;
}

