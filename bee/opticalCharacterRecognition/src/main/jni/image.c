//
// Created by Administrator on 2018/5/15.
//
#include <inttypes.h>
#include <assert.h>
#include <zbar.h>
#include "global.h"
#include "image.h"
/*
 * Class:     com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Image
 * Method:    init
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Image_init
        (JNIEnv *env, jclass cls){
    //Image_peer = (*env)->GetFieldID(env,cls,"peer","J");
    setImagePeer(env,cls);
    //Image_data = (*env)->GetFieldID(env,cls,"data","Ljava/lang/Object;");
    setImageData(env,cls);
}

/*
 * Class:     com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Image
 * Method:    create
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Image_create
        (JNIEnv *env, jobject obj){
    zbar_image_t *zimg = zbar_image_create();
    if(!zimg){
        throw_exc(env,"java/lang/OutOfMemoryError",NULL);
        return 0;
    }
    addImageCreate();
    return (intptr_t)zimg;
}

/*
 * Class:     com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Image
 * Method:    destroy
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Image_destroy
        (JNIEnv *env, jobject obj, jlong peer){
    zbar_image_ref(PEER_CAST(peer),-1);
    addImageDestroy();
}
static inline uint32_t format_to_fourcc(JNIEnv *env, jstring format) {
    if(!format)
        goto invalid;
    int n = (*env)->GetStringLength(env, format);
    if(0 >= n || n > 4)
        goto invalid;
    char fmtstr[8];
    (*env)->GetStringUTFRegion(env, format, 0, n, fmtstr);
    uint32_t fourcc = 0;
    int i;
    for(i = 0; i < n; i++) {
        if(fmtstr[i] < ' ' || 'Z' < fmtstr[i] ||
           ('9' < fmtstr[i] && fmtstr[i] < 'A') ||
           (' ' < fmtstr[i] && fmtstr[i] < '0'))
            goto invalid;
        fourcc |= ((uint32_t)fmtstr[i]) << (8 * i);
    }
    return fourcc;

    invalid:throw_exc(env, "java/lang/IllegalArgumentException", "invalid format fourcc");
    return 0;
}
/*
 * Class:     com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Image
 * Method:    convert
 * Signature: (JLjava/lang/String;)J
 */
JNIEXPORT jlong JNICALL Java_com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Image_convert
        (JNIEnv *env, jobject job, jlong peer, jstring format){
    uint32_t fourcc = format_to_fourcc(env, format);
    if(!fourcc)
        return 0;
    zbar_image_t *zimg = zbar_image_convert(PEER_CAST(peer),fourcc);
    if(!zimg)
        throw_exc(env, "java/lang/UnsupportedOperationException", "unsupported image format");
    else
        addImageCreate();
    return (intptr_t)zimg;
}

/*
 * Class:     com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Image
 * Method:    getFormat
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Image_getFormat
        (JNIEnv *env, jobject obj){
    uint32_t  fourcc = zbar_image_get_format(getImagePeer(env,obj));//(GET_PEER(Image,obj));
    if(!fourcc)
        return NULL;
    char fmtstr[5] = { fourcc, fourcc >> 8, fourcc >> 16, fourcc >> 24, 0 };
    return (*env)->NewStringUTF(env, fmtstr);
}
/*
 * Class:     com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Image
 * Method:    setFormat
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Image_setFormat
        (JNIEnv *env, jobject obj, jstring format){
    uint32_t fourcc = format_to_fourcc(env, format);
    if(fourcc)
        zbar_image_set_format(getImagePeer(env,obj),fourcc);//(GET_PEER(Image,obj),fourcc);
}

/*
 * Class:     com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Image
 * Method:    getSequence
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Image_getSequence
        (JNIEnv *env, jobject obj){
    return zbar_image_get_sequence(getImagePeer(env,obj));//(GET_PEER(Image,obj));
}

/*
 * Class:     com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Image
 * Method:    setSequence
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Image_setSequence
        (JNIEnv *env, jobject obj, jint seq){
    zbar_image_set_sequence(getImagePeer(env,obj),seq);//(GET_PEER(Image,obj),seq);
}

/*
 * Class:     com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Image
 * Method:    getWidth
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Image_getWidth
        (JNIEnv *env, jobject obj){
    return zbar_image_get_width(getImagePeer(env,obj));//(GET_PEER(Image,obj));
}

/*
 * Class:     com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Image
 * Method:    getHeight
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Image_getHeight
        (JNIEnv *env, jobject obj){
    return zbar_image_get_height(getImagePeer(env,obj));//(GET_PEER(Image,obj));
}

/*
 * Class:     com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Image
 * Method:    getSize
 * Signature: ()[I
 */
JNIEXPORT jintArray JNICALL Java_com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Image_getSize
        (JNIEnv *env, jobject obj){
    jintArray size = (*env)->NewIntArray(env,2);
    if(!size)
        return NULL;
    unsigned dims[2];
    zbar_image_get_size(getImagePeer(env,obj),dims,dims +1);//(GET_PEER(Image, obj), dims, dims + 1);
    jint jdims[2] = { dims[0], dims[1] };
    (*env)->SetIntArrayRegion(env, size, 0, 2, jdims);
    return size;
}

/*
 * Class:     com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Image
 * Method:    setSize
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Image_setSize__II
        (JNIEnv *env, jobject obj, jint width, jint height){
    if(width <0)
        width = 0;
    if(height <0)
        height = 0;
    zbar_image_set_size(getImagePeer(env,obj),width,height);//(GET_PEER(Image, obj), width, height);
}

/*
 * Class:     com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Image
 * Method:    setSize
 * Signature: ([I)V
 */
JNIEXPORT void JNICALL Java_com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Image_setSize___3I
        (JNIEnv *env, jobject obj, jintArray size){
    if(2!= (*env)->GetArrayLength(env,size))
        throw_exc(env, "java/lang/IllegalArgumentException", "size must be an array of two ints");
    jint dims[2];
    (*env)->GetIntArrayRegion(env,size,0,2,dims);
    if(0> dims[0])
        dims[0]=0;
    if(0> dims[1])
        dims[1]=0;
    zbar_image_set_size(getImagePeer(env,obj),dims[0],dims[1]);//(GET_PEER(Image, obj), dims[0], dims[1]);
}

/*
 * Class:     com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Image
 * Method:    getCrop
 * Signature: ()[I
 */
JNIEXPORT jintArray JNICALL Java_com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Image_getCrop
        (JNIEnv *env, jobject obj){
    jintArray crop = (*env)->NewIntArray(env,4);
    if(!crop)
        return NULL;
    unsigned dims[4];
    zbar_image_get_crop(getImagePeer(env,obj),dims,dims +1,dims +2,dims +3);//(GET_PEER(Image, obj), dims, dims + 1, dims + 2, dims + 3);
    jint jdims[4] = { dims[0], dims[1], dims[2], dims[3] };
    (*env)->SetIntArrayRegion(env, crop, 0, 4, jdims);
    return crop;
}

/*
 * Class:     com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Image
 * Method:    setCrop
 * Signature: (IIII)V
 */
JNIEXPORT void JNICALL Java_com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Image_setCrop__IIII
        (JNIEnv * env, jobject obj, jint x, jint y, jint w, jint h){
    VALIDATE_CROP(x, w);
    VALIDATE_CROP(y, h);
    zbar_image_set_crop(getImagePeer(env,obj),x,y,w,h);//(GET_PEER(Image, obj), x, y, w, h);
}

/*
 * Class:     com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Image
 * Method:    setCrop
 * Signature: ([I)V
 */
JNIEXPORT void JNICALL Java_com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Image_setCrop___3I
        (JNIEnv *env, jobject obj, jintArray crop){
    if(4!=(*env)->GetArrayLength(env, crop))
        throw_exc(env, "java/lang/IllegalArgumentException", "crop must be an array of four ints");
    jint dims[4];
    (*env)->GetIntArrayRegion(env,crop,0,4,dims);
    VALIDATE_CROP(dims[0],dims[2]);
    VALIDATE_CROP(dims[1],dims[3]);
    zbar_image_set_crop(getImagePeer(env,obj),dims[0],dims[1],dims[2],dims[3]);//(GET_PEER(Image,obj),dims[0],dims[1],dims[2],dims[3]);
}

#undef VALIDATE_CROP
/*
 * Class:     com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Image
 * Method:    getData
 * Signature: ()[B
 */
JNIEXPORT jbyteArray JNICALL Java_com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Image_getData
        (JNIEnv *env, jobject obj){
    jobject data = (*env)->GetObjectField(env, obj, Image_data);
    if(data)
        return(data);
    zbar_image_t *zimg = getImagePeer(env,obj);//GET_PEER(Image, obj);
    data = zbar_image_get_userdata(zimg);
    if(data)
        return(data);
    unsigned long rawlen = zbar_image_get_data_length(zimg);
    const void *raw = zbar_image_get_data(zimg);
    if(!rawlen || !raw)
        return(NULL);
    data = (*env)->NewByteArray(env, rawlen);
    if(!data)
        return NULL;
    (*env)->SetByteArrayRegion(env, data, 0, rawlen, raw);
    (*env)->SetObjectField(env, obj, Image_data, data);
    return data;
}

static JavaVM *jvm = NULL;
JNIEXPORT jint JNICALL
JNI_OnLoad (JavaVM *_jvm, void *reserved) {
    jvm = _jvm;
    return(JNI_VERSION_1_2);
}
JNIEXPORT void JNICALL
JNI_OnUnload (JavaVM *_jvm, void *reserved) {
    assert(stats.SymbolSet_create == stats.SymbolSet_destroy);
    assert(stats.Symbol_create == stats.Symbol_destroy);
    assert(stats.Image_create == stats.Image_destroy);
    assert(stats.ImageScanner_create == stats.ImageScanner_destroy);
}
static void Image_cleanupIntArray (zbar_image_t *zimg) {
    jobject data = zbar_image_get_userdata(zimg);
    assert(data);
    JNIEnv *env = NULL;
    if((*jvm)->AttachCurrentThread(jvm, (void*)&env, NULL))
        return;
    assert(env);
    if(env && data) {
        void *raw = (void*)zbar_image_get_data(zimg);
        assert(raw);
        /* const image data is unchanged - abort copy back */
        (*env)->ReleaseIntArrayElements(env, data, raw, JNI_ABORT);
        (*env)->DeleteGlobalRef(env, data);
        zbar_image_set_userdata(zimg, NULL);
    }
}
static void Image_cleanupByteArray (zbar_image_t *zimg) {
    jobject data = zbar_image_get_userdata(zimg);
    assert(data);
    JNIEnv *env = NULL;
    if((*jvm)->AttachCurrentThread(jvm, (void*)&env, NULL))
        return;
    assert(env);
    if(env && data) {
        void *raw = (void*)zbar_image_get_data(zimg);
        assert(raw);
        /* const image data is unchanged - abort copy back */
        (*env)->ReleaseByteArrayElements(env, data, raw, JNI_ABORT);
        (*env)->DeleteGlobalRef(env, data);
        zbar_image_set_userdata(zimg, NULL);
    }
}
static inline void Image_setData (JNIEnv *env, jobject obj, jbyteArray data, void *raw, unsigned long rawlen, zbar_image_cleanup_handler_t *cleanup) {
    if (!data)
        cleanup = NULL;
    (*env)->SetObjectField(env, obj, Image_data, data);
    zbar_image_t *zimg = getImagePeer(env,obj);//GET_PEER(Image, obj);
    zbar_image_set_data(zimg, raw, rawlen, cleanup);
    zbar_image_set_userdata(zimg, (*env)->NewGlobalRef(env, data));
}
/*
 * Class:     com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Image
 * Method:    setData
 * Signature: ([B)V
 */
JNIEXPORT void JNICALL Java_com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Image_setData___3B
        (JNIEnv *env, jobject obj, jbyteArray data){
    jbyte *raw = NULL;
    unsigned long rawlen = 0;
    if(data) {
        raw = (*env)->GetByteArrayElements(env, data, NULL);
        if(!raw)
            return;
        rawlen = (*env)->GetArrayLength(env, data);
    }
    Image_setData(env, obj, data, raw, rawlen, Image_cleanupByteArray);
}

/*
 * Class:     com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Image
 * Method:    setData
 * Signature: ([I)V
 */
JNIEXPORT void JNICALL Java_com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Image_setData___3I
        (JNIEnv *env, jobject obj, jintArray data){
    jint *raw = NULL;
    unsigned long rawlen = 0;
    if(data) {
        raw = (*env)->GetIntArrayElements(env, data, NULL);
        if(!raw)
            return;
        rawlen = (*env)->GetArrayLength(env, data) * sizeof(*raw);
    }
    Image_setData(env, obj, data, raw, rawlen, Image_cleanupIntArray);
}

/*
 * Class:     com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Image
 * Method:    getSymbols
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_com_stynet_shuidianbing_opticalcharacterrecognition_zbar_Image_getSymbols
        (JNIEnv *env, jobject obj, jlong peer){
    const zbar_symbol_set_t *zsyms = zbar_image_get_symbols(PEER_CAST(peer));
    if(zsyms) {
        zbar_symbol_set_ref(zsyms, 1);
        addSymbolSetCreate();
    }
    return (intptr_t)zsyms;
}


