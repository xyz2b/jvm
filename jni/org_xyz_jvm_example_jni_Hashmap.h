/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class org_xyz_jvm_example_jni_Hashmap */

#ifndef _Included_org_xyz_jvm_example_jni_Hashmap
#define _Included_org_xyz_jvm_example_jni_Hashmap
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     org_xyz_jvm_example_jni_Hashmap
 * Method:    createHashmap
 * Signature: ()Ljava/util/HashMap;
 */
JNIEXPORT jobject JNICALL Java_org_xyz_jvm_example_jni_Hashmap_createHashmap
  (JNIEnv *, jclass);

/*
 * Class:     org_xyz_jvm_example_jni_Hashmap
 * Method:    get
 * Signature: (Ljava/util/HashMap;Ljava/lang/String;)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_org_xyz_jvm_example_jni_Hashmap_get__Ljava_util_HashMap_2Ljava_lang_String_2
  (JNIEnv *, jclass, jobject, jstring);

/*
 * Class:     org_xyz_jvm_example_jni_Hashmap
 * Method:    get
 * Signature: (Ljava/util/HashMap;Ljava/lang/Object;)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_org_xyz_jvm_example_jni_Hashmap_get__Ljava_util_HashMap_2Ljava_lang_Object_2
  (JNIEnv *, jclass, jobject, jobject);

/*
 * Class:     org_xyz_jvm_example_jni_Hashmap
 * Method:    put
 * Signature: (Ljava/util/HashMap;Ljava/lang/String;Ljava/lang/Object;)V
 */
JNIEXPORT void JNICALL Java_org_xyz_jvm_example_jni_Hashmap_put__Ljava_util_HashMap_2Ljava_lang_String_2Ljava_lang_Object_2
  (JNIEnv *, jclass, jobject, jstring, jobject);

/*
 * Class:     org_xyz_jvm_example_jni_Hashmap
 * Method:    put
 * Signature: (Ljava/util/HashMap;Ljava/lang/Object;Ljava/lang/Object;)V
 */
JNIEXPORT void JNICALL Java_org_xyz_jvm_example_jni_Hashmap_put__Ljava_util_HashMap_2Ljava_lang_Object_2Ljava_lang_Object_2
  (JNIEnv *, jclass, jobject, jobject, jobject);

#ifdef __cplusplus
}
#endif
#endif
