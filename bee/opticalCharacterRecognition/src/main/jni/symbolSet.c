//
// Created by Administrator on 2018/5/15.
//
/*
 * Class:     com_stynet_shuidianbing_opticalcharacterrecognition_zbar_SymbolSet
 * Method:    init
 * Signature: ()V
 */
#include <inttypes.h>
#include <assert.h>
#include <zbar.h>
#include "global.h"
#include "symbol.h"
/*
 * Class:     com_stynet_shuidianbing_opticalcharacterrecognition_zbar_SymbolSet
 * Method:    init
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_stynet_shuidianbing_opticalcharacterrecognition_zbar_SymbolSet_init
(JNIEnv *env, jclass cls){
    setSymbolSetPeer(env,cls);//SymbolSet_peer = (*env)->GetFieldID(env, cls, "peer", "J");
}

/*
 * Class:     com_stynet_shuidianbing_opticalcharacterrecognition_zbar_SymbolSet
 * Method:    destroy
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_stynet_shuidianbing_opticalcharacterrecognition_zbar_SymbolSet_destroy
(JNIEnv *env, jobject job, jlong peer){
    zbar_symbol_set_ref(PEER_CAST(peer), -1);
    addSymbolSetDestroy();
}

/*
 * Class:     com_stynet_shuidianbing_opticalcharacterrecognition_zbar_SymbolSet
 * Method:    size
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_stynet_shuidianbing_opticalcharacterrecognition_zbar_SymbolSet_size
        (JNIEnv *env, jobject obj){
    zbar_symbol_set_t *zsyms = getSymbolSetPeer(env,obj);//GET_PEER(SymbolSet, obj);
    //if(!zsyms)
     //   return 0;
    return zsyms ? zbar_symbol_set_get_size(zsyms):0;
}

/*
 * Class:     com_stynet_shuidianbing_opticalcharacterrecognition_zbar_SymbolSet
 * Method:    firstSymbol
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_com_stynet_shuidianbing_opticalcharacterrecognition_zbar_SymbolSet_firstSymbol
        (JNIEnv *env, jobject obj, jlong peer){
    if(!peer)
        return 0;
    const zbar_symbol_t *zsym = zbar_symbol_set_first_symbol(PEER_CAST(peer));
    if(zsym) {
        zbar_symbol_ref(zsym, 1);
        addSymbolCreate();
    }
    return (intptr_t)zsym;
}
