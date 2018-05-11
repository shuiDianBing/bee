LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := photoBlur
LOCAL_LDFLAGS := -Wl,--build-id
LOCAL_SRC_FILES := \
	E:\project\bee\frameset\src\main\jni\blurNative.c \
	E:\project\bee\frameset\src\main\jni\stackBlur.c \

LOCAL_C_INCLUDES += E:\project\bee\frameset\src\main\jni
LOCAL_C_INCLUDES += E:\project\bee\frameset\src\main\app
LOCAL_LDLIBS    := -lm -llog -ljnigraphics

include $(BUILD_SHARED_LIBRARY)
