LOCAL_PATH := $(call my-dir)
KATSUNA_COMMON_PATH := $(ANDROID_BUILD_TOP)/frameworks/KatsunaCommon
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-java-files-under, app/src/main)

LOCAL_MANIFEST_FILE := app/src/main/AndroidManifest.xml
LOCAL_FULL_LIBS_MANIFEST_FILES := $(KATSUNA_COMMON_PATH)/commons/src/main/AndroidManifest.xml
LOCAL_ASSET_DIR := $(LOCAL_PATH)/app/src/main/assets
LOCAL_RESOURCE_DIR := $(LOCAL_PATH)/app/src/main/res
LOCAL_RESOURCE_DIR += frameworks/support/v7/appcompat/res
LOCAL_RESOURCE_DIR += frameworks/support/design/res

LOCAL_STATIC_JAVA_LIBRARIES := android-support-v4
LOCAL_STATIC_JAVA_LIBRARIES += android-support-v7-appcompat
LOCAL_STATIC_JAVA_LIBRARIES += android-support-design

LOCAL_STATIC_JAVA_AAR_LIBRARIES := roundedimageview

# Include KatsunaCommon into this app
LOCAL_REQUIRED_MODULES := KatsunaCommon
LOCAL_STATIC_JAVA_LIBRARIES += KatsunaCommon
# Include KatsunaCommon resources
LOCAL_RESOURCE_DIR += frameworks/KatsunaCommon/commons/src/main/res

LOCAL_AAPT_INCLUDE_ALL_RESOURCES := true
LOCAL_AAPT_FLAGS := --version-code 2
LOCAL_AAPT_FLAGS += --version-name "1.1.0"
LOCAL_AAPT_FLAGS := --auto-add-overlay
LOCAL_AAPT_FLAGS += --generate-dependencies
LOCAL_AAPT_FLAGS += --extra-packages com.katsuna.commons
LOCAL_AAPT_FLAGS += --extra-packages android.support.v7.appcompat
LOCAL_AAPT_FLAGS += --extra-packages android.support.design
LOCAL_AAPT_FLAGS += --extra-packages com.makeramen.roundedimageview

LOCAL_PACKAGE_NAME := KatsunaKeyboard
LOCAL_CERTIFICATE := platform

LOCAL_PROGUARD_FLAG_FILES := app/proguard-rules.pro
LOCAL_PROGUARD_FLAGS := -ignorewarnings -include build/core/proguard_basic_keeps.flags
LOCAL_PROGUARD_ENABLED := nosystem

include $(BUILD_PACKAGE)
