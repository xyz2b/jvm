/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class org_xyz_jvm_example_jni_HelloWorld */

#ifndef _Included_org_xyz_jvm_example_jni_HelloWorld
#define _Included_org_xyz_jvm_example_jni_HelloWorld
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     org_xyz_jvm_example_jni_HelloWorld
 * Method:    showVal
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_xyz_jvm_example_jni_HelloWorld_showVal
  (JNIEnv *, jclass);

/*
 * Class:     org_xyz_jvm_example_jni_HelloWorld
 * Method:    getVal
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_xyz_jvm_example_jni_HelloWorld_getVal
  (JNIEnv *, jclass);

/*
 * Class:     org_xyz_jvm_example_jni_HelloWorld
 * Method:    setVal
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_org_xyz_jvm_example_jni_HelloWorld_setVal
  (JNIEnv *, jclass, jint);

/*
 * Class:     org_xyz_jvm_example_jni_HelloWorld
 * Method:    showVal2
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_xyz_jvm_example_jni_HelloWorld_showVal2
  (JNIEnv *, jobject);

/*
 * Class:     org_xyz_jvm_example_jni_HelloWorld
 * Method:    getVal2
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_xyz_jvm_example_jni_HelloWorld_getVal2
  (JNIEnv *, jobject);

/*
 * Class:     org_xyz_jvm_example_jni_HelloWorld
 * Method:    setVal2
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_org_xyz_jvm_example_jni_HelloWorld_setVal2
  (JNIEnv *, jobject, jint);

/*
 * Class:     org_xyz_jvm_example_jni_HelloWorld
 * Method:    createThread
 * Signature: ()Lorg/xyz/jvm/example/jni/JavaThread;
 */
JNIEXPORT jobject JNICALL Java_org_xyz_jvm_example_jni_HelloWorld_createThread
  (JNIEnv *, jclass);

/*
 * Class:     org_xyz_jvm_example_jni_HelloWorld
 * Method:    threadRunFast
 * Signature: (Lorg/xyz/jvm/example/jni/JavaThread;)V
 */
JNIEXPORT void JNICALL Java_org_xyz_jvm_example_jni_HelloWorld_threadRunFast
  (JNIEnv *, jclass, jobject);

/*
 * Class:     org_xyz_jvm_example_jni_HelloWorld
 * Method:    throwException
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_xyz_jvm_example_jni_HelloWorld_throwException
  (JNIEnv *, jobject);

#ifdef __cplusplus
}
#endif
#endif
