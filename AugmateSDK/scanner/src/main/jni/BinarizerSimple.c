#include <jni.h>
#include <stdio.h>

#include "Helpers.h"

void binarizerSimpleByteToIntArray(unsigned char* src, unsigned int* dst, unsigned int width, unsigned int height) {
    int i;
    for(i = 0; i < width * height; i ++) {
        int value = (src[i] & 0xFF) < 80 ? 0 : 255;
        dst[i] = value & 0xFF;
    }
}
