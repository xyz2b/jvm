package org.xyz.jvm.hotspot.src.share.vm.classfile;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.xyz.jvm.hotspot.src.share.tools.DataTranslate;
import org.xyz.jvm.hotspot.src.share.vm.memory.ResourceObj;
import org.xyz.jvm.hotspot.src.share.vm.oops.DescriptorInfo;
import org.xyz.jvm.hotspot.src.share.vm.runtime.JavaVFrame;
import org.xyz.jvm.hotspot.src.share.vm.runtime.StackValue;
import org.xyz.jvm.hotspot.src.share.vm.utilities.BasicType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 解析方法、属性的描述符
 * */
@Slf4j
@Data
public class DescriptorStream extends ResourceObj {
    // 描述符
    private String descriptorInfo;
    // 描述符字节流
    private byte[] descriptor;
    // 字节流的读取索引
    private int index = 0;
    // 方法参数的个数
    private int methodParamsSize;

    // 按顺序存储解析完的方法参数
    private List<DescriptorInfo> parameters = new ArrayList<>();

    // 解析完的返回参数类型
    private DescriptorInfo returnElement;

    public DescriptorStream(String descriptorInfo) {
        this.descriptorInfo = descriptorInfo;
    }

    /**
     * 将不同类型的值压入操作数栈中
     * */
    public void pushField(Object o, JavaVFrame frame) {
        switch (returnElement.getType()) {
            case BasicType.T_BOOLEAN:
                frame.getOperandStack().push(new StackValue(BasicType.T_BOOLEAN, (boolean)o));

                break;
            case BasicType.T_BYTE:
                frame.getOperandStack().push(new StackValue(BasicType.T_BYTE, (byte)o));

                break;
            case BasicType.T_CHAR:
                frame.getOperandStack().push(new StackValue(BasicType.T_CHAR, (char)o));

                break;
            case BasicType.T_SHORT:
                frame.getOperandStack().push(new StackValue(BasicType.T_SHORT, (short)o));

                break;
            case BasicType.T_INT:
                frame.getOperandStack().push(new StackValue(BasicType.T_INT, (int)o));

                break;
            case BasicType.T_LONG:
                frame.getOperandStack().push(new StackValue(BasicType.T_LONG, (int)o));

                break;
            case BasicType.T_DOUBLE:
                frame.getOperandStack().push(new StackValue(BasicType.T_DOUBLE, (int)o));

                break;
            case BasicType.T_OBJECT:
                frame.getOperandStack().push(new StackValue(BasicType.T_OBJECT, o));

                break;
            case BasicType.T_ARRAY:
                frame.getOperandStack().push(new StackValue(BasicType.T_OBJECT, o));

                break;
            default:
                throw new Error("无法识别的参数类型");
        }
    }

    /**
     * 根据形参列表的类型，从操作数栈中弹出实参值
     * @return 实参值列表
     * */
    public Object[] getParamsVal(JavaVFrame frame) {
        Object[] values = new Object[getMethodParamsSize()];

        for (int i = 0; i < getMethodParamsSize(); i++) {
            DescriptorInfo info = getParameters().get(i);

            switch (info.getType()) {
                // 如果形参类型为boolean类型，从操作数栈中弹出boolean类型的值
                case BasicType.T_BOOLEAN:
                    values[i] = (boolean) frame.getOperandStack().pop().getObject();
                    break;

                // 如果形参类型为byte类型，从操作数栈中弹出byte类型的值
                case BasicType.T_BYTE:
                    values[i] = (byte) frame.getOperandStack().pop().getObject();
                    break;

                // 如果形参类型为char类型，从操作数栈中弹出char类型的值
                case BasicType.T_CHAR:
                    values[i] = (char) frame.getOperandStack().pop().getObject();
                    break;

                // 如果形参类型为short类型，从操作数栈中弹出short类型的值
                case BasicType.T_SHORT:
                    values[i] = (short) frame.getOperandStack().pop().getObject();
                    break;

                // 如果形参类型为int类型，从操作数栈中弹出int类型的值
                case BasicType.T_INT:
                    values[i] = (int) frame.getOperandStack().pop().getObject();
                    break;

                // 如果形参类型为long类型，从操作数栈中弹出long类型的值
                case BasicType.T_LONG:
                    values[i] = (long) frame.getOperandStack().pop().getObject();
                    break;

                // 如果形参类型为double类型，从操作数栈中弹出double类型的值
                case BasicType.T_DOUBLE:
                    values[i] = (double) frame.getOperandStack().pop().getObject();
                    break;

                // 如果形参类型为引用类型，从操作数栈中弹出引用类型的值
                case BasicType.T_OBJECT:
                    values[i] = frame.getOperandStack().pop().getObject();
                    break;

                // 如果形参类型为数组类型，从操作数栈中弹出数组类型的值
                case BasicType.T_ARRAY:
                    values[i] = frame.getOperandStack().pop().getObject();
                    break;

                default:
                    throw new Error("无法识别的参数类型");
            }
        }

        return values;

    }

    /**
     * 获取形参列表中每个元素类型对应的Class对象
     * @return 按照顺序存放的形参列表中每个元素类型的Class对象
     * */
    public Class<?>[] getParamsType() {
        Class<?>[] types = new Class[getMethodParamsSize()];

        for (int i = 0; i < getMethodParamsSize(); i++) {
            DescriptorInfo info = getParameters().get(i);

            switch (info.getType()) {
                case BasicType.T_BOOLEAN: {
                    types[i] = boolean.class;
                    break;
                }
                case BasicType.T_BYTE: {
                    types[i] = byte.class;
                    break;
                }
                case BasicType.T_CHAR: {
                    types[i] = char.class;
                    break;
                }
                case BasicType.T_SHORT: {
                    types[i] = short.class;
                    break;
                }
                case BasicType.T_INT: {
                    types[i] = int.class;
                    break;
                }
                case BasicType.T_FLOAT: {
                    types[i] = float.class;
                    break;
                }
                case BasicType.T_LONG: {
                    types[i] = long.class;
                    break;
                }
                case BasicType.T_DOUBLE: {
                    types[i] = double.class;
                    break;
                }
                case BasicType.T_OBJECT: {
                    // Object通过类的全限定名 使用反射来获取其Class对象
                    try {
                        types[i] = Class.forName(info.getTypeDesc().replace('/', '.'));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case BasicType.T_ARRAY: {
                    /**
                     * 数组类型通过数组类型的描述符 使用反射来获取其Class对象
                     * 如: int[][] --> [[I
                     * 如: String[] --> [Ljava/lang/String;
                     * */

                    // 数组维度
                    int dimension = info.getArrayDimension();
                    // 根据维度构造描述符的数组标识部分，"[" 重复数组维度次
                    String arrayPrefix = String.join("", Collections.nCopies(dimension, DataTranslate.byteToString(BasicType.JVM_SIGNATURE_ARRAY)));

                    try {
                        switch (info.getArrayElementType().getType()) {
                            // 描述符为 "[I" （[数量根据维度来，这里只是举例）
                            case BasicType.T_BOOLEAN: {
                                types[i] = Class.forName(arrayPrefix + DataTranslate.byteToString(BasicType.JVM_SIGNATURE_BOOLEAN));
                                break;
                            }
                            // 描述符为 "[B" （[数量根据维度来，这里只是举例）
                            case BasicType.T_BYTE: {
                                types[i] = Class.forName(arrayPrefix + DataTranslate.byteToString(BasicType.JVM_SIGNATURE_BYTE));
                                break;
                            }
                            // 描述符为 "[C" （[数量根据维度来，这里只是举例）
                            case BasicType.T_CHAR: {
                                types[i] = Class.forName(arrayPrefix + DataTranslate.byteToString(BasicType.JVM_SIGNATURE_CHAR));
                                break;
                            }
                            // 描述符为 "[S" （[数量根据维度来，这里只是举例）
                            case BasicType.T_SHORT: {
                                types[i] = Class.forName(arrayPrefix + DataTranslate.byteToString(BasicType.JVM_SIGNATURE_SHORT));
                                break;
                            }
                            // 描述符为 "[I" （[数量根据维度来，这里只是举例）
                            case BasicType.T_INT: {
                                types[i] = Class.forName(arrayPrefix + DataTranslate.byteToString(BasicType.JVM_SIGNATURE_INT));
                                break;
                            }
                            // 描述符为 "[F" （[数量根据维度来，这里只是举例）
                            case BasicType.T_FLOAT: {
                                types[i] = Class.forName(arrayPrefix + DataTranslate.byteToString(BasicType.JVM_SIGNATURE_FLOAT));
                                break;
                            }
                            // 描述符为 "[J" （[数量根据维度来，这里只是举例）
                            case BasicType.T_LONG: {
                                types[i] = Class.forName(arrayPrefix + DataTranslate.byteToString(BasicType.JVM_SIGNATURE_LONG));
                                break;
                            }
                            // 描述符为 "[D" （[数量根据维度来，这里只是举例）
                            case BasicType.T_DOUBLE: {
                                types[i] = Class.forName(arrayPrefix + DataTranslate.byteToString(BasicType.JVM_SIGNATURE_DOUBLE));
                                break;
                            }
                            // 描述符为 "[Ljava/lang/String;"（[数量根据维度来，具体数组类型根据引用类型来，这里只是举例）
                            case BasicType.T_OBJECT: {
                                // 如果数组元素的类型为引用类型，则引用类型存储在数组的arrayElementType(DescriptorInfo)中的typeDesc中
                                types[i] = Class.forName(arrayPrefix + DataTranslate.byteToString(BasicType.JVM_SIGNATURE_CLASS) +
                                        info.getArrayElementType().getTypeDesc().replace("/", ".") +
                                        DataTranslate.byteToString(BasicType.JVM_SIGNATURE_END_CLASS));
                                break;
                            }
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                default:
                    log.error("无法识别的类型: " + info.getType());
                    throw new Error("无法识别的参数类型");
            }

        }

        return types;
    }

    /**
     * 解析方法的描述符
     * */
    public void parseMethod() {
        parseMethodParams();
        parseReturn();
    }

    /**
     * 解析方法返回值类型
     * */
    public void parseReturn() {
        // 取返回值
        int paramEndIndex = descriptorInfo.indexOf(')');
        String returnStr = descriptorInfo.substring(paramEndIndex + 1, descriptorInfo.length());

        // 调用解析方法
        returnElement = new DescriptorStream(returnStr).doParse().get(0);
        log.debug("该方法的返回值: " + returnElement);
    }

    /**
     * 解析方法参数类型
     * */
    private void parseMethodParams() {
        log.info("解析方法描述符");
        // 找到形参列表括号的位置，左括号和右括号中间就是形参列表
        int paramStartIndex = descriptorInfo.indexOf(BasicType.JVM_SIGNATURE_START_FUNC);
        int paramEndIndex = descriptorInfo.indexOf(BasicType.JVM_SIGNATURE_END_FUNC);
        // 取形参列表
        String paramStr = descriptorInfo.substring(paramStartIndex + 1, paramEndIndex);

        // 调用解析方法
        parameters = new DescriptorStream(paramStr).doParse();
        methodParamsSize = parameters.size();

        log.info("该方法描述符形参数量: " + methodParamsSize);
        log.debug("该方法形参: " + parameters);
    }

    /**
     * 真正解析描述符方法
     * @return 解析完之后的列表
     * */
    private List<DescriptorInfo> doParse() {
        descriptor = descriptorInfo.getBytes();

        List<DescriptorInfo> parseResult = new ArrayList<>();

        for (; index < descriptor.length; index++) {
            byte b = descriptor[index];
            switch (b) {
                case BasicType.JVM_SIGNATURE_ARRAY: {
                    log.info("\t解析array类型");

                    DescriptorInfo array = parseArrayType();
                    log.info("\t\tT_ARRAY: arrayDimension: " + array.getArrayDimension() + ", typeDesc: " + array.getTypeDesc());

                    parseResult.add(array);
                    break;
                }
                case BasicType.JVM_SIGNATURE_CLASS: {
                    log.info("\t解析reference类型");

                    DescriptorInfo ref = parseReferenceType();
                    log.info("\t\tT_OBJECT: " + "typeDesc: " + ref.getTypeDesc());

                    parseResult.add(ref);
                    break;
                }
                case BasicType.JVM_SIGNATURE_BOOLEAN: {
                    log.info("\t解析boolean类型");

                    DescriptorInfo booleanType = parseBooleanType();

                    parseResult.add(booleanType);
                    break;
                }
                case BasicType.JVM_SIGNATURE_BYTE: {
                    log.info("\t解析byte类型");

                    DescriptorInfo byteType = parseByteType();

                    parseResult.add(byteType);
                    break;
                }
                case BasicType.JVM_SIGNATURE_CHAR: {
                    log.info("\t解析char类型");

                    DescriptorInfo charType = parseCharType();

                    parseResult.add(charType);
                    break;
                }
                case BasicType.JVM_SIGNATURE_SHORT: {
                    log.info("\t解析short类型");

                    DescriptorInfo shortType = parseShortType();

                    parseResult.add(shortType);
                    break;
                }
                case BasicType.JVM_SIGNATURE_INT: {
                    log.info("\t解析int类型");

                    DescriptorInfo intType = parseIntType();

                    parseResult.add(intType);
                    break;
                }
                case BasicType.JVM_SIGNATURE_FLOAT: {
                    log.info("\t解析float类型");

                    DescriptorInfo floatType = parseFloatType();

                    parseResult.add(floatType);
                    break;
                }
                case BasicType.JVM_SIGNATURE_LONG: {
                    log.info("\t解析long类型");

                    DescriptorInfo longType = parseLongType();

                    parseResult.add(longType);
                    break;
                }
                case BasicType.JVM_SIGNATURE_DOUBLE: {
                    log.info("\t解析double类型");

                    DescriptorInfo doubleType = parseDoubleType();

                    parseResult.add(doubleType);
                    break;
                }
                case BasicType.JVM_SIGNATURE_VOID: {
                    log.info("\t解析void类型");

                    DescriptorInfo voidType = parseVoidType();

                    parseResult.add(voidType);
                    break;
                }
                default: {
                    throw new Error("无法识别的元素类型");
                }
            }
        }
        return parseResult;
    }

    private DescriptorInfo parseShortType() {
        DescriptorInfo descriptorInfo = new DescriptorInfo();
        descriptorInfo.setType(BasicType.T_SHORT);
        descriptorInfo.setTypeDesc(DataTranslate.byteToString(BasicType.JVM_SIGNATURE_SHORT));
        descriptorInfo.setResolved(true);
        return descriptorInfo;
    }

    private DescriptorInfo parseBooleanType() {
        DescriptorInfo descriptorInfo = new DescriptorInfo();
        descriptorInfo.setType(BasicType.T_BOOLEAN);
        descriptorInfo.setTypeDesc(DataTranslate.byteToString(BasicType.JVM_SIGNATURE_BOOLEAN));
        descriptorInfo.setResolved(true);
        return descriptorInfo;
    }

    private DescriptorInfo parseByteType() {
        DescriptorInfo descriptorInfo = new DescriptorInfo();
        descriptorInfo.setType(BasicType.T_BYTE);
        descriptorInfo.setTypeDesc(DataTranslate.byteToString(BasicType.JVM_SIGNATURE_BYTE));
        descriptorInfo.setResolved(true);
        return descriptorInfo;
    }

    private DescriptorInfo parseCharType() {
        DescriptorInfo descriptorInfo = new DescriptorInfo();
        descriptorInfo.setType(BasicType.T_CHAR);
        descriptorInfo.setTypeDesc(DataTranslate.byteToString(BasicType.JVM_SIGNATURE_CHAR));
        descriptorInfo.setResolved(true);
        return descriptorInfo;
    }

    private DescriptorInfo parseIntType() {
        DescriptorInfo descriptorInfo = new DescriptorInfo();
        descriptorInfo.setType(BasicType.T_INT);
        descriptorInfo.setTypeDesc(DataTranslate.byteToString(BasicType.JVM_SIGNATURE_INT));
        descriptorInfo.setResolved(true);
        return descriptorInfo;
    }

    private DescriptorInfo parseFloatType() {
        DescriptorInfo descriptorInfo = new DescriptorInfo();
        descriptorInfo.setType(BasicType.T_FLOAT);
        descriptorInfo.setTypeDesc(DataTranslate.byteToString(BasicType.JVM_SIGNATURE_FLOAT));
        descriptorInfo.setResolved(true);
        return descriptorInfo;
    }

    private DescriptorInfo parseLongType() {
        DescriptorInfo descriptorInfo = new DescriptorInfo();
        descriptorInfo.setType(BasicType.T_LONG);
        descriptorInfo.setTypeDesc(DataTranslate.byteToString(BasicType.JVM_SIGNATURE_LONG));
        descriptorInfo.setResolved(true);
        return descriptorInfo;
    }

    private DescriptorInfo parseDoubleType() {
        DescriptorInfo descriptorInfo = new DescriptorInfo();
        descriptorInfo.setType(BasicType.T_DOUBLE);
        descriptorInfo.setTypeDesc(DataTranslate.byteToString(BasicType.JVM_SIGNATURE_DOUBLE));
        descriptorInfo.setResolved(true);
        return descriptorInfo;
    }

    private DescriptorInfo parseVoidType() {
        DescriptorInfo descriptorInfo = new DescriptorInfo();
        descriptorInfo.setType(BasicType.T_VOID);
        descriptorInfo.setTypeDesc(DataTranslate.byteToString(BasicType.JVM_SIGNATURE_VOID));
        descriptorInfo.setResolved(true);
        return descriptorInfo;
    }

    /**
     * 解析数组类型
     * @return 解析出来的数组类型的信息
     * */
    private DescriptorInfo parseArrayType() {
        DescriptorInfo descriptorInfo = new DescriptorInfo();
        descriptorInfo.setType(BasicType.T_ARRAY);
        descriptorInfo.incArrayDimension();
        // 读取索引+1，因为已经解析过一个'['了
        index++;

        /**
         * 用于判断是不是数组结尾的flag，如果是数组结尾，就退出遍历描述符字节流
         * 数据结尾有两种情况
         * 1.数组元素类型是基本数据类型，那就是最后一个'['之后的一个字符就是数组元素的类型，就到了数组的结尾
         * 2.数组元素类型是引用类型，那就是最后一个'['之后是'L'，直到';'结束，中间的是数组元素的类型，然后到了';'就到了数组结尾
         * */
        boolean flag = false;
        for (; index < descriptor.length; index++) {
            switch (descriptor[index]) {
                // 如果'['后面还是'['，数组维度+1
                case BasicType.JVM_SIGNATURE_ARRAY: {
                    descriptorInfo.incArrayDimension();
                    break;
                }
                // 如果'['后面是'L'，标识数组元素是应用类型
                case BasicType.JVM_SIGNATURE_CLASS: {
                    log.info("\t\t\t解析引用类型");
                    DescriptorInfo ref = parseReferenceType();
                    log.info("\t\t\t\tT_OBJECT: " + "typeDesc: " + ref.getTypeDesc());
                    descriptorInfo.setArrayElementType(ref);
                    flag = true;
                    break;
                }
                // 如果'['后面是'Z'，标识数组元素是boolean类型
                case BasicType.JVM_SIGNATURE_BOOLEAN: {
                    log.info("\t\t\t解析boolean类型");

                    DescriptorInfo booleanType = parseBooleanType();
                    descriptorInfo.setArrayElementType(booleanType);

                    flag = true;
                    break;
                }
                // 如果'['后面是'B'，标识数组元素是byte类型
                case BasicType.JVM_SIGNATURE_BYTE: {
                    log.info("\t\t解析byte类型");

                    DescriptorInfo byteType = parseByteType();
                    descriptorInfo.setArrayElementType(byteType);

                    flag = true;
                    break;
                }
                // 如果'['后面是'C'，标识数组元素是char类型
                case BasicType.JVM_SIGNATURE_CHAR: {
                    log.info("\t\t解析char类型");

                    DescriptorInfo charType = parseCharType();
                    descriptorInfo.setArrayElementType(charType);

                    flag = true;
                    break;
                }
                // 如果'['后面是'S'，标识数组元素是short类型
                case BasicType.JVM_SIGNATURE_SHORT: {
                    log.info("\t\t解析short类型");

                    DescriptorInfo shortType = parseShortType();
                    descriptorInfo.setArrayElementType(shortType);

                    flag = true;
                    break;
                }
                // 如果'['后面是'I'，标识数组元素是int类型
                case BasicType.JVM_SIGNATURE_INT: {
                    log.info("\t\t解析int类型");

                    DescriptorInfo intType = parseIntType();
                    descriptorInfo.setArrayElementType(intType);

                    flag = true;
                    break;
                }
                // 如果'['后面是'F'，标识数组元素是float类型
                case BasicType.JVM_SIGNATURE_FLOAT: {
                    log.info("\t\t解析float类型");

                    DescriptorInfo floatType = parseFloatType();
                    descriptorInfo.setArrayElementType(floatType);

                    flag = true;
                    break;
                }
                // 如果'['后面是'J'，标识数组元素是long类型
                case BasicType.JVM_SIGNATURE_LONG: {
                    log.info("\t\t解析long类型");

                    DescriptorInfo longType = parseLongType();
                    descriptorInfo.setArrayElementType(longType);

                    flag = true;
                    break;
                }
                // 如果'['后面是'D'，标识数组元素是double类型
                case BasicType.JVM_SIGNATURE_DOUBLE: {
                    log.info("\t\t解析double类型");

                    DescriptorInfo doubleType = parseDoubleType();
                    descriptorInfo.setArrayElementType(doubleType);

                    flag = true;
                    break;
                }
                default: {
                    throw new Error("无法识别的数组元素类型");
                }
            }

            // 如果是数组结尾，就退出遍历描述符字节流
            if(flag) {
                break;
            }
        }
        descriptorInfo.setResolved(true);

        return descriptorInfo;
    }

    /**
     * 解析引用类型
     * @return 解析出来的引用类型的信息
     * */
    private DescriptorInfo parseReferenceType() {
        DescriptorInfo descriptorInfo = new DescriptorInfo();
        descriptorInfo.setType(BasicType.T_OBJECT);
        // 前面已经解析过了'L'，所以继续往后解析，需要将index+1，后面才是真正的引用类型信息
        index++;
        StringBuilder reference = new StringBuilder();
        // 往后循环遍历，直到遇到';'，标识引用类型的结尾
        for (; index < descriptor.length; index++) {
            if (descriptor[index] != BasicType.JVM_SIGNATURE_END_CLASS) {
                reference.append(new String(new byte[]{descriptor[index]}));
            } else {
                break;
            }
        }
        descriptorInfo.setTypeDesc(reference.toString());
        descriptorInfo.setResolved(true);
        return descriptorInfo;
    }
}
