#ifndef __INCLUDE_HELPERS_H
#define __INCLUDE_HELPERS_H

#include <android/log.h>

// define ABI
#if defined(__arm__)
  #if defined(__ARM_ARCH_7A__)
    #if defined(__ARM_NEON__)
      #define ABI "armeabi-v7a with NEON"
    #else
      #define ABI "armeabi-v7a"
    #endif
  #else
   #define ABI "armeabi"
  #endif
#elif defined(__i386__)
   #define ABI "x86"
#elif defined(__mips__)
   #define ABI "mips"
#else
   #define ABI "unknown"
#endif

#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG, "Augmate.Native", __VA_ARGS__)

#endif