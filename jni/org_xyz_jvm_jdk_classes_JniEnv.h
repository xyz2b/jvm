/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class org_xyz_jvm_jdk_classes_JniEnv */

#ifndef _Included_org_xyz_jvm_jdk_classes_JniEnv
#define _Included_org_xyz_jvm_jdk_classes_JniEnv
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     org_xyz_jvm_jdk_classes_JniEnv
 * Method:    loadClassFile
 * Signature: (Ljava/lang/String;)Lorg/xyz/jvm/jdk/classes/Handle;
 */
JNIEXPORT jobject JNICALL Java_org_xyz_jvm_jdk_classes_JniEnv_loadClassFile
  (JNIEnv *, jclass, jstring);

/*
 * Class:     org_xyz_jvm_jdk_classes_JniEnv
 * Method:    getMethodId
 * Signature: (Lorg/xyz/jvm/jdk/classes/Handle;Ljava/lang/String;Ljava/lang/String;)Lorg/xyz/jvm/jdk/classes/Handle;
 */
JNIEXPORT jobject JNICALL Java_org_xyz_jvm_jdk_classes_JniEnv_getMethodId
  (JNIEnv *, jclass, jobject, jstring, jstring);

/*
 * Class:     org_xyz_jvm_jdk_classes_JniEnv
 * Method:    callStaticVoidMethod
 * Signature: (Lorg/xyz/jvm/jdk/classes/Handle;Lorg/xyz/jvm/jdk/classes/Handle;)V
 */
JNIEXPORT void JNICALL Java_org_xyz_jvm_jdk_classes_JniEnv_callStaticVoidMethod
  (JNIEnv *, jclass, jobject, jobject);

#ifdef __cplusplus
}
#endif
#endif
