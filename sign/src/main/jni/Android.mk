LOCAL_PATH:=$(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE:=sign

LOCAL_SRC_FILES:=com_zhan_haoqi_sign_Sign.cpp com_zhan_haoqi_sign_Sign.h

include $(BUILD_SHARED_LIBRARY)
