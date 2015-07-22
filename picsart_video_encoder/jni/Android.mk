WORKING_DIR := $(call my-dir)
BINDINGS_DIR:=  $(WORKING_DIR)/bindings/JNI
include $(BINDINGS_DIR)/Android.mk
LOCAL_LDLIBS := -llog
