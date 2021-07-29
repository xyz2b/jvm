package org.xyz.jvm.hotspot.src.share.vm.classfile;

import lombok.extern.slf4j.Slf4j;
import org.xyz.jvm.hotspot.src.share.tools.DataTranslate;
import org.xyz.jvm.hotspot.src.share.tools.Stream;
import org.xyz.jvm.hotspot.src.share.vm.oops.*;
import org.xyz.jvm.hotspot.src.share.vm.oops.attribute.*;
import org.xyz.jvm.hotspot.src.share.vm.intepreter.ByteCodeStream;
import org.xyz.jvm.hotspot.src.share.vm.utilities.AccessFlags;

import java.util.Map;

import static org.xyz.jvm.hotspot.src.share.vm.oops.Attribute.*;

@Slf4j
public class ClassFileParser {
    /**
     * @param content 字节码文件的字节流
     * @return 解析生成的 InstanceKlass 实例
     * */
    public static InstanceKlass parseClassFile(byte[] content) {
        // 解析时对应到字节流中目前解析位置的索引，简称当前解析索引
        int index = 0;
        // 解析生成的 InstanceKlass 实例
        InstanceKlass klass = new InstanceKlass();

        // 中转字节数组，可复用
        byte[] u2Arr = new byte[2];
        byte[] u4Arr = new byte[4];

        // 魔数   u4
        Stream.readU4Simple(content, index, klass.getMagic());
        index += 4;

        // 次版本号 u2
        Stream.readU2Simple(content, index, klass.getMinionVersion());
        index += 2;

        // 主版本号 u2
        Stream.readU2Simple(content, index, klass.getMajorVersion());
        index += 2;

        // 常量池大小 u2
        Stream.readU2Simple(content, index, u2Arr);
        index += 2;
        klass.getConstantPool().setLength(DataTranslate.byteToUnsignedShort(u2Arr));
        klass.getConstantPool().initContainer();

        // 常量池 N字节
        index = parseConstantPool(content, klass, index);

        // 类的访问权限及属性    u2
        Stream.readU2Simple(content, index, u2Arr);
        index += 2;
        klass.setAccessFlags(DataTranslate.byteToUnsignedShort(u2Arr));

        // 类名       u2
        Stream.readU2Simple(content, index, u2Arr);
        index += 2;
        klass.setThisClass(DataTranslate.byteToUnsignedShort(u2Arr));
        log.info("类名: " + klass.getConstantPool().getClassName(klass.getThisClass()));

        // 父类名  u2
        Stream.readU2Simple(content, index, u2Arr);
        index += 2;
        klass.setSuperClass(DataTranslate.byteToUnsignedShort(u2Arr));
        log.info("父类名: " + klass.getConstantPool().getClassName(klass.getSuperClass()));

        // 实现的接口数量  u2
        Stream.readU2Simple(content, index, u2Arr);
        index += 2;
        klass.setInterfacesCount(DataTranslate.byteToUnsignedShort(u2Arr));

        // 实现的接口列表
        if (klass.getInterfacesCount() != 0) {
            log.info("开始解析实现的接口信息: ");
            klass.initInterfaceContainer();

            index = parseInterface(content, klass, index);
        }

        // 成员字段数量  u2
        Stream.readU2Simple(content, index, u2Arr);
        index += 2;
        klass.setFieldsCount(DataTranslate.byteToUnsignedShort(u2Arr));

        // 成员字段列表
        if (klass.getFieldsCount() != 0) {
            log.info("开始解析成员字段信息: ");

            klass.initFieldContainer();
            index = parseFiled(content, klass, index);
        }

        // 成员方法数量   u2
        Stream.readU2Simple(content, index, u2Arr);
        index += 2;
        klass.setMethodsCount(DataTranslate.byteToUnsignedShort(u2Arr));
        klass.initMethodContainer();

        log.info("开始解析成员方法信息: ");
        // 成员方法列表
        index = parseMethod(content, klass, index);

        // 类属性数量   u2
        Stream.readU2Simple(content, index, u2Arr);
        index += 2;
        klass.setAttributesCount(DataTranslate.byteToUnsignedShort(u2Arr));

        if (klass.getAttributesCount() != 0) {
            log.info("开始解析类的属性信息: ");
            klass.initAttributeContainer();

            index = parseAttribute(content, klass.getAttributesCount(), klass, index, klass.getAttributes(), null);
        }

        return klass;
    }

    /**
     * 解析成员方法
     * @param content 字节流
     * @param klass 解析成的instanceKlass实例
     * @param index 当前解析索引
     * @return 解析完成之后的当前解析索引
     * */
    private static int parseMethod(byte[] content, InstanceKlass klass, int index) {
        log.info("解析成员方法:");

        // 中转字节数组，可复用
        byte[] u2Arr = new byte[2];

        // 遍历成员方法列表
        for (int i = 0; i < klass.getMethodsCount(); i++) {
            MethodInfo methodInfo = new MethodInfo();
            methodInfo.setBelongKlass(klass);

            klass.getMethods().add(methodInfo);

            // access_flag  u2
            Stream.readU2Simple(content, index, u2Arr);
            index += 2;
            methodInfo.setAccessFlags(new AccessFlags(DataTranslate.byteToUnsignedShort(u2Arr)));

            // name_index   u2
            Stream.readU2Simple(content, index, u2Arr);
            index += 2;
            methodInfo.setNameIndex(DataTranslate.byteToUnsignedShort(u2Arr));
            log.info("读取常量池获取方法名称，constant pool index: " + methodInfo.getNameIndex());
            methodInfo.setMethodName(methodInfo.getBelongKlass().getConstantPool().getUtf8(methodInfo.getNameIndex()));
            log.info("解析方法: " + methodInfo.getMethodName());

            // descriptor_index     u2
            Stream.readU2Simple(content, index, u2Arr);
            index += 2;
            methodInfo.setDescriptorIndex(DataTranslate.byteToUnsignedShort(u2Arr));

            // attribute_count  u2
            Stream.readU2Simple(content, index, u2Arr);
            index += 2;
            methodInfo.setAttributesCount(DataTranslate.byteToUnsignedShort(u2Arr));

            log.info("\t第 " + i + " 个方法: access flag: " + methodInfo.getAccessFlags()
                    + ", name index: " + methodInfo.getNameIndex()
                    + ", descriptor index: " + methodInfo.getDescriptorIndex()
                    + ", attribute count: " + methodInfo.getAttributesCount()
            );

            log.info("开始解析成员方法的属性信息: ");
            // 成员方法的属性表
            methodInfo.initAttributeContainer();
            index = parseAttribute(content, methodInfo.getAttributesCount(), klass, index, methodInfo.getAttributes(), methodInfo);
        }

        return index;
    }

    /**
     * 解析成员字段
     * @param content 字节流
     * @param klass 解析成的instanceKlass实例
     * @param index 当前解析索引
     * @return 解析完成之后的当前解析索引
     * */
    private static int parseFiled(byte[] content, InstanceKlass klass, int index) {
        log.info("解析成员字段:");

        // 中转字节数组，可复用
        byte[] u2Arr = new byte[2];

        // 遍历成员字段列表
        for (int i = 0; i < klass.getFieldsCount(); i++) {
            FiledInfo filedInfo = new FiledInfo();
            klass.getFields().add(filedInfo);

            // access_flag  u2
            Stream.readU2Simple(content, index, u2Arr);
            index += 2;
            filedInfo.setAccessFlag(DataTranslate.byteToUnsignedShort(u2Arr));

            // name_index   u2
            Stream.readU2Simple(content, index, u2Arr);
            index += 2;
            filedInfo.setNameIndex(DataTranslate.byteToUnsignedShort(u2Arr));

            // descriptor_index     u2
            Stream.readU2Simple(content, index, u2Arr);
            index += 2;
            filedInfo.setDescriptorIndex(DataTranslate.byteToUnsignedShort(u2Arr));

            // attribute_count  u2
            Stream.readU2Simple(content, index, u2Arr);
            index += 2;
            filedInfo.setAttributesCount(DataTranslate.byteToUnsignedShort(u2Arr));

            log.info("\t第 " + i + " 个字段: " +
                    "字段名: " + klass.getConstantPool().getUtf8(filedInfo.getNameIndex()) +
                    ", 字段描述符: " + klass.getConstantPool().getUtf8(filedInfo.getDescriptorIndex()) +
                    ", 字段属性数量: " + filedInfo.getAttributesCount()
            );

            // 成员字段的属性表
            if (filedInfo.getAttributesCount() != 0) {
                log.info("开始解析成员字段的属性信息: ");

                filedInfo.initAttributeContainer();
                index = parseAttribute(content, filedInfo.getAttributesCount(), klass, index, filedInfo.getAttributes(), null);
            }
        }

        return index;
    }

    /**
     * 解析属性信息
     * @param content 字节流
     * @param attributesCount 属性的数量
     * @param klass 属性所属的Klass，用于获取常量池信息
     * @param index 当前解析索引
     * @param attributes 存放解析出来属性的容器
     * @param methodInfo 属性所属的方法信息，只有在解析方法的属性时才需要传入，其他情况传入null即可
     * @return 解析完成之后的当前解析索引
     * */
    private static int parseAttribute(byte[] content, int attributesCount, InstanceKlass klass, int index, Map<String, Attribute> attributes, MethodInfo methodInfo) {
        // 中转字节数组，可复用
        byte[] u2Arr = new byte[2];
        byte[] u4Arr = new byte[4];

        // 遍历属性列表
        for (int i = 0; i < attributesCount; i++) {
            // attribute_name_index     u2
            Stream.readU2Simple(content, index, u2Arr);
            index += 2;
            int attributeNameIndex = DataTranslate.byteToUnsignedShort(u2Arr);

            // attribute_length     u4
            Stream.readU4Simple(content, index, u4Arr);
            index += 4;
            int attributeLength = DataTranslate.byteToInt(u4Arr);

            // attributeName
            String attributeName = klass.getConstantPool().getUtf8(attributeNameIndex);

            log.info("\t\t第 " + i + " 个属性: " + "属性名: " + attributeName);

            switch (attributeName) {
                case JVM_ATTRIBUTE_ConstantValue: {
                    index = parseConstantValueAttribute(content, index, attributeNameIndex, attributeLength, attributeName, attributes);
                    break;
                }
                case JVM_ATTRIBUTE_Code: {
                    index = parseCodeAttribute(content, index, attributeNameIndex, attributeLength, attributeName, attributes, klass, methodInfo);
                    break;
                }
                case JVM_ATTRIBUTE_Exception: {
                    index = parseExceptionAttribute(content, index, attributeNameIndex, attributeLength, attributeName, attributes);
                    break;
                }
                case JVM_ATTRIBUTE_LineNumberTable: {
                    index = parseLineNumberTable(content, index, attributeNameIndex, attributeLength, attributeName, attributes);
                    break;
                }
                case JVM_ATTRIBUTE_LocalVariableTable: {
                    index = parseLocalVariableTable(content, index, attributeNameIndex, attributeLength, attributeName, attributes);
                    break;
                }
                case JVM_ATTRIBUTE_SourceFile: {
                    index = parseSourceFile(content, index, attributeNameIndex, attributeLength, attributeName, attributes);
                    break;
                }
                case JVM_ATTRIBUTE_StackMapTable: {
                    index = parseStackMapTable(content, index, attributeNameIndex, attributeLength, attributeName, attributes);
                    break;
                }
                default:
                    throw new Error("无法识别的属性项: " + attributeName);
            }
        }

        return index;
    }

    /**
     * 解析 StackMapTable 属性
     * @param content 字节流
     * @param index 当前解析索引
     * @param attributeNameIndex    属性名在常量池中的索引
     * @param attributeLength       属性长度(Byte)
     * @param attributeName     属性名
     * @param attributes        解析出来属性的存储容器
     * @return 解析完成之后的当前解析索引
     * */
    private static int parseStackMapTable(byte[] content, int index, int attributeNameIndex, int attributeLength, String attributeName, Map<String, Attribute> attributes) {
        StackMapTableAttribute stackMapTableAttribute = new StackMapTableAttribute();
        stackMapTableAttribute.setAttributeNameIndex(attributeNameIndex);
        stackMapTableAttribute.setAttributeLength(attributeLength);

        // 直接跳过后面的数据，不做解析
        index += stackMapTableAttribute.getAttributeLength();

        log.info("\t\t\t stackMapTable: "
                + ", name index: " + stackMapTableAttribute.getAttributeNameIndex()
                + ", attr len: " + stackMapTableAttribute.getAttributeLength()
        );

        return index;
    }

    /**
     * 解析 SourceFile 属性
     * @param content 字节流
     * @param index 当前解析索引
     * @param attributeNameIndex    属性名在常量池中的索引
     * @param attributeLength       属性长度(Byte)
     * @param attributeName     属性名
     * @param attributes        解析出来属性的存储容器
     * @return 解析完成之后的当前解析索引
     * */
    private static int parseSourceFile(byte[] content, int index, int attributeNameIndex, int attributeLength, String attributeName, Map<String, Attribute> attributes) {
        // 中转字节数组，可复用
        byte[] u2Arr = new byte[2];

        SourceFileAttribute sourceFileAttribute = new SourceFileAttribute();
        sourceFileAttribute.setAttributeNameIndex(attributeNameIndex);
        sourceFileAttribute.setAttributeLength(attributeLength);

        // sourceFileIndex     u2
        Stream.readU2Simple(content, index, u2Arr);
        index += 2;
        sourceFileAttribute.setSourceFileIndex(DataTranslate.byteToUnsignedShort(u2Arr));

        attributes.put(attributeName, sourceFileAttribute);
        log.info("\t\t\tSourceFile: "
                + ", source file index: " + sourceFileAttribute.getSourceFileIndex()
        );
        return index;
    }

    /**
     * 解析 LocalVariableTable 属性
     * @param content 字节流
     * @param index 当前解析索引
     * @param attributeNameIndex    属性名在常量池中的索引
     * @param attributeLength       属性长度(Byte)
     * @param attributeName     属性名
     * @param attributes        解析出来属性的存储容器
     * @return 解析完成之后的当前解析索引
     * */
    private static int parseLocalVariableTable(byte[] content, int index, int attributeNameIndex, int attributeLength, String attributeName, Map<String, Attribute> attributes) {
        // 中转字节数组，可复用
        byte[] u2Arr = new byte[2];

        LocalVariableTableAttribute localVariableTableAttribute = new LocalVariableTableAttribute();
        localVariableTableAttribute.setAttributeNameIndex(attributeNameIndex);
        localVariableTableAttribute.setAttributeLength(attributeLength);

        // local_variable_table_length     u2
        Stream.readU2Simple(content, index, u2Arr);
        index += 2;
        localVariableTableAttribute.setLocalVariableTableLength(DataTranslate.byteToUnsignedShort(u2Arr));

        log.info("\t\t\tLocalVariableTable: "
                + ", table len: " + localVariableTableAttribute.getLocalVariableTableLength()
        );

        if (localVariableTableAttribute.getLocalVariableTableLength() != 0) {
            localVariableTableAttribute.initLocalVariableTable();

            for (int i = 0; i < localVariableTableAttribute.getLocalVariableTableLength(); i++) {
                LocalVariableTableAttribute.LocalVariable localVariable = new LocalVariableTableAttribute.LocalVariable();

                // start_pc  u2
                Stream.readU2Simple(content, index, u2Arr);
                index += 2;
                localVariable.setStartPc(DataTranslate.byteToUnsignedShort(u2Arr));

                // length  u2
                Stream.readU2Simple(content, index, u2Arr);
                index += 2;
                localVariable.setLength(DataTranslate.byteToUnsignedShort(u2Arr));

                // name_index  u2
                Stream.readU2Simple(content, index, u2Arr);
                index += 2;
                localVariable.setNameIndex(DataTranslate.byteToUnsignedShort(u2Arr));

                // descriptor_index  u2
                Stream.readU2Simple(content, index, u2Arr);
                index += 2;
                localVariable.setDescriptorIndex(DataTranslate.byteToUnsignedShort(u2Arr));

                // index  u2
                Stream.readU2Simple(content, index, u2Arr);
                index += 2;
                localVariable.setIndex(DataTranslate.byteToUnsignedShort(u2Arr));

                localVariableTableAttribute.getLocalVariableTable().add(localVariable);

                log.info("\t\t\t\tLocalVariable: "
                        + ", start pc: " + localVariable.getStartPc()
                        + ", length: " + localVariable.getLength()
                        + ", name index: " + localVariable.getNameIndex()
                        + ", descriptor index: " + localVariable.getDescriptorIndex()
                        + ", index: " + localVariable.getIndex()
                );
            }
        }

        attributes.put(attributeName, localVariableTableAttribute);
        return index;
    }

    /**
     * 解析 LineNumberTable 属性
     * @param content 字节流
     * @param index 当前解析索引
     * @param attributeNameIndex    属性名在常量池中的索引
     * @param attributeLength       属性长度(Byte)
     * @param attributeName     属性名
     * @param attributes        解析出来属性的存储容器
     * @return 解析完成之后的当前解析索引
     * */
    private static int parseLineNumberTable(byte[] content, int index, int attributeNameIndex, int attributeLength, String attributeName, Map<String, Attribute> attributes) {
        // 中转字节数组，可复用
        byte[] u2Arr = new byte[2];

        LineNumberTableAttribute lineNumberTableAttribute = new LineNumberTableAttribute();
        lineNumberTableAttribute.setAttributeNameIndex(attributeNameIndex);
        lineNumberTableAttribute.setAttributeLength(attributeLength);

        // line_number_table_length     u2
        Stream.readU2Simple(content, index, u2Arr);
        index += 2;
        lineNumberTableAttribute.setLineNumberTableLength(DataTranslate.byteToUnsignedShort(u2Arr));

        log.info("\t\t\tlineNumberTable: "
                + ", table len: " + lineNumberTableAttribute.getLineNumberTableLength()
        );

        if (lineNumberTableAttribute.getLineNumberTableLength() != 0) {
            lineNumberTableAttribute.initLineNumberTables();

            for (int i = 0; i < lineNumberTableAttribute.getLineNumberTableLength(); i++) {
                LineNumberTableAttribute.LineNumber lineNumber = new LineNumberTableAttribute.LineNumber();

                // start_pc  u2
                Stream.readU2Simple(content, index, u2Arr);
                index += 2;
                lineNumber.setStartPc(DataTranslate.byteToUnsignedShort(u2Arr));

                // line_number u2
                Stream.readU2Simple(content, index, u2Arr);
                index += 2;
                lineNumber.setLineNumber(DataTranslate.byteToUnsignedShort(u2Arr));

                lineNumberTableAttribute.getLineNumberTables().add(lineNumber);

                log.info("\t\t\t\tlineNumber: "
                        + ", start pc: " + lineNumber.getStartPc()
                        + ", line number: " + lineNumber.getLineNumber()
                );
            }
        }

        attributes.put(attributeName, lineNumberTableAttribute);
        return index;
    }

    /**
     * 解析 Exception 属性
     * @param content 字节流
     * @param index 当前解析索引
     * @param attributeNameIndex    属性名在常量池中的索引
     * @param attributeLength       属性长度(Byte)
     * @param attributeName     属性名
     * @param attributes        解析出来属性的存储容器
     * @return 解析完成之后的当前解析索引
     * */
    private static int parseExceptionAttribute(byte[] content, int index, int attributeNameIndex, int attributeLength, String attributeName, Map<String, Attribute> attributes) {
        // 中转字节数组，可复用
        byte[] u2Arr = new byte[2];

        ExceptionAttribute exceptionAttribute = new ExceptionAttribute();
        exceptionAttribute.setAttributeNameIndex(attributeNameIndex);
        exceptionAttribute.setAttributeLength(attributeLength);

        // numberOfExceptions  u2
        Stream.readU2Simple(content, index, u2Arr);
        index += 2;
        exceptionAttribute.setNumberOfExceptions(DataTranslate.byteToInt(u2Arr));

        log.info("\t\t\tException: "
                + ", table len: " + exceptionAttribute.getNumberOfExceptions()
        );

        if (exceptionAttribute.getNumberOfExceptions() != 0) {
            exceptionAttribute.initExceptionIndexTable();
            // exceptionIndexTable
            for (int i = 0; i < exceptionAttribute.getNumberOfExceptions(); i++) {
                Stream.readU2Simple(content, index, u2Arr);
                index += 2;
                exceptionAttribute.getExceptionIndexTable().add(DataTranslate.byteToUnsignedShort(u2Arr));

                log.info("\t\t\t\texception class: "
                        + ", exception class index: " + DataTranslate.byteToUnsignedShort(u2Arr)
                );
            }
        }

        attributes.put(attributeName, exceptionAttribute);
        return index;
    }

    /**
     * 解析 Code 属性
     * @param content 字节流
     * @param index 当前解析索引
     * @param attributeNameIndex    属性名在常量池中的索引
     * @param attributeLength       属性长度(Byte)
     * @param attributeName     属性名
     * @param attributes        解析出来属性的存储容器
     * @param klass             Code属性所属方法所属的类信息
     * @param methodInfo        Code属性所属方法信息
     * @return 解析完成之后的当前解析索引
     * */
    private static int parseCodeAttribute(byte[] content, int index, int attributeNameIndex, int attributeLength, String attributeName, Map<String, Attribute> attributes, InstanceKlass klass, MethodInfo methodInfo) {
        // 中转字节数组，可复用
        byte[] u2Arr = new byte[2];
        byte[] u4Arr = new byte[4];

        CodeAttribute codeAttribute = new CodeAttribute();
        codeAttribute.setAttributeNameIndex(attributeNameIndex);
        codeAttribute.setAttributeLength(attributeLength);

        // max_stack        u2
        Stream.readU2Simple(content, index, u2Arr);
        index += 2;
        codeAttribute.setMaxStack(DataTranslate.byteToUnsignedShort(u2Arr));

        // max_locals       u2
        Stream.readU2Simple(content, index, u2Arr);
        index += 2;
        codeAttribute.setMaxLocals(DataTranslate.byteToUnsignedShort(u2Arr));

        // code_length  u4
        Stream.readU4Simple(content, index, u4Arr);
        index += 4;
        codeAttribute.setCodeLength(DataTranslate.byteToInt(u4Arr));

        // code     code_length个字节
        ByteCodeStream bytecodeStream = new ByteCodeStream(methodInfo, codeAttribute);
        codeAttribute.setCode(bytecodeStream);

        Stream.readSimple(content, index, codeAttribute.getCodeLength(), bytecodeStream.getCodes());
        index += codeAttribute.getCodeLength();

        log.info("\t\t\tCode 属性:"
                + ", name index: " + codeAttribute.getAttributeNameIndex()
                + ", stack: " + codeAttribute.getMaxStack()
                + ", locals: " + codeAttribute.getMaxLocals()
                + ", code len: " + codeAttribute.getCodeLength()
        );

        // exception_table_length   u2
        Stream.readU2Simple(content, index, u2Arr);
        index += 2;
        codeAttribute.setExceptionTableLength(DataTranslate.byteToUnsignedShort(u2Arr));

        if (codeAttribute.getExceptionTableLength() != 0) {
            log.info("开始解析成员字段的Code属性的异常处理器列表: ");
            codeAttribute.initExceptionTables();

            // exception_table  N个字节
            for (int i = 0; i < codeAttribute.getExceptionTableLength(); i++) {
                CodeAttribute.ExceptionHandler exceptionHandler = new CodeAttribute.ExceptionHandler();

                // start_pc u2
                Stream.readU2Simple(content, index, u2Arr);
                index += 2;
                exceptionHandler.setStartPc(DataTranslate.byteToUnsignedShort(u2Arr));

                // end_pc u2
                Stream.readU2Simple(content, index, u2Arr);
                index += 2;
                exceptionHandler.setEndPc(DataTranslate.byteToUnsignedShort(u2Arr));

                // handler_pc u2
                Stream.readU2Simple(content, index, u2Arr);
                index += 2;
                exceptionHandler.setHandlerPc(DataTranslate.byteToUnsignedShort(u2Arr));

                // catch_type 捕获异常的类型(常量池中Class的索引) u2
                Stream.readU2Simple(content, index, u2Arr);
                index += 2;
                exceptionHandler.setCatchType(DataTranslate.byteToUnsignedShort(u2Arr));

                codeAttribute.getExceptionTables().add(exceptionHandler);

                log.info("\t\t\t\t ExceptionHandler: "
                        + ", start pc: " + exceptionHandler.getStartPc()
                        + ", end pc: " + exceptionHandler.getEndPc()
                );
            }
        }

        // attributes_count     u2
        Stream.readU2Simple(content, index, u2Arr);
        index += 2;
        codeAttribute.setAttributesCount(DataTranslate.byteToUnsignedShort(u2Arr));

        if (codeAttribute.getAttributesCount() != 0) {
            log.info("开始解析成员字段的Code属性的属性列表: ");
            codeAttribute.initAttributes();

            // attributes    N个字节
            index = parseAttribute(content, codeAttribute.getAttributesCount(), klass, index, codeAttribute.getAttributes(), methodInfo);
        }

        attributes.put(attributeName, codeAttribute);
        return index;
    }

    /**
     * 解析 ConstantValue 属性
     * @param content 字节流
     * @param index 当前解析索引
     * @param attributeNameIndex    属性名在常量池中的索引
     * @param attributeLength       属性长度(Byte)
     * @param attributeName     属性名
     * @param attributes        解析出来属性的存储容器
     * @return 解析完成之后的当前解析索引
     * */
    private static int parseConstantValueAttribute(byte[] content, int index, int attributeNameIndex, int attributeLength, String attributeName, Map<String, Attribute> attributes) {
        // 中转字节数组，可复用
        byte[] u2Arr = new byte[2];

        ConstantValueAttribute constantValueAttribute = new ConstantValueAttribute();
        constantValueAttribute.setAttributeNameIndex(attributeNameIndex);
        constantValueAttribute.setAttributeLength(attributeLength);

        // constant_value_index     u2
        Stream.readU2Simple(content, index, u2Arr);
        index += 2;
        constantValueAttribute.setConstantValueIndex(DataTranslate.byteToUnsignedShort(u2Arr));

        attributes.put(attributeName, constantValueAttribute);
        log.info("\t\t\t ConstantValue: "
                + ", constant value index: " + constantValueAttribute.getConstantValueIndex()
        );
        return index;
    }

    /**
     * 解析实现的接口列表
     * @param content 字节流
     * @param klass 解析成的instanceKlass实例
     * @param index 当前解析索引
     * @return 解析完成之后的当前解析索引
     * */
    private static int parseInterface(byte[] content, InstanceKlass klass, int index) {
        // 中转字节数组，可复用
        byte[] u2Arr = new byte[2];

        // 遍历实现的接口列表
        // 列表中的每个成员都是常量池的索引 u2
        for (int i = 0; i < klass.getInterfacesCount(); i++) {
            Stream.readU2Simple(content, index, u2Arr);
            index += 2;

            int val = DataTranslate.byteToUnsignedShort(u2Arr);
            String name = klass.getConstantPool().getClassName(val);

            InterfaceInfo interfaceInfo = new InterfaceInfo(val, name);
            klass.getInterfaces().add(interfaceInfo);

            log.info("\t 第 " + (i + 1) + " 个接口: " + name);
        }

        return index;
    }

    /**
     * 解析常量池
     * @param content 字节流
     * @param klass 解析成的instanceKlass实例
     * @param index 当前解析索引
     * @return 解析完成之后的当前解析索引
     * */
    private static int parseConstantPool(byte[] content, InstanceKlass klass, int index) {
        log.info("解析常量池");

        // 遍历常量池
        for (int i = 1; i < klass.getConstantPool().getLength(); i++) {
            int tag = Stream.readU1Simple(content, index);
            index += 1;

            switch (tag) {
                case ConstantPool.JVM_CONSTANT_Utf8: {
                    index = parseJvmConstantUtf8(content, klass, index, i);
                    break;
                }
                case ConstantPool.JVM_CONSTANT_Integer: {
                    index = parseJvmConstantInteger(content, klass, index, i);
                    break;
                }
                case ConstantPool.JVM_CONSTANT_Float: {
                    index = parseJvmConstantFloat(content, klass, index, i);
                    break;
                }
                case ConstantPool.JVM_CONSTANT_Long: {
                    index = parseJvmConstantLong(content, klass, index, i);
                    // 因为long在常量池中占两个槽位，上面解析是一次解析合并完成的，所以之后需要将遍历索引自增1
                    i++;
                    break;
                }
                case ConstantPool.JVM_CONSTANT_Double: {
                    index = parseJvmConstantDouble(content, klass, index, i);
                    // 因为double在常量池中占两个槽位，上面解析是一次解析合并完成的，所以之后需要将遍历索引自增1
                    i++;
                    break;
                }
                case ConstantPool.JVM_CONSTANT_String: {
                    index = parseJvmConstantString(content, klass, index, i);
                    break;
                }
                case ConstantPool.JVM_CONSTANT_Class: {
                    index = parseJvmConstantClass(content, klass, index, i);
                    break;
                }
                case ConstantPool.JVM_CONSTANT_Fieldref: {
                    index = parseJvmConstantField(content, klass, index, i);
                    break;
                }
                case ConstantPool.JVM_CONSTANT_Methodref: {
                    index = parseJvmConstantMethod(content, klass, index, i);
                    break;
                }
                case ConstantPool.JVM_CONSTANT_InterfaceMethodref: {
                    index = parseJvmConstantInterfaceMethod(content, klass, index, i);
                    break;
                }
                case ConstantPool.JVM_CONSTANT_NameAndType: {
                    index = parseJvmConstantNameAndType(content, klass, index, i);
                    break;
                }
                default:
                    throw new Error("无法识别的常量池项: " + tag);
            }
        }

        return index;
    }

    /**
     * 解析常量池 JVM_CONSTANT_NameAndType 结构
     * @param content 字节流
     * @param klass 解析成的instanceKlass实例
     * @param index 当前解析索引
     * @param constantPoolIndex 该结构在常量池中的索引
     * @return 解析完成之后的当前解析索引
     * */
    private static int parseJvmConstantNameAndType(byte[] content, InstanceKlass klass, int index, int constantPoolIndex) {
        // 中转字节数组，可复用
        byte[] u2Arr = new byte[2];

        // 将 index-->tag 的映射关系写入常量池
        klass.getConstantPool().getTag()[constantPoolIndex] = ConstantPool.JVM_CONSTANT_NameAndType;

        // name_index
        Stream.readU2Simple(content, index, u2Arr);
        index += 2;
        int nameIndex = DataTranslate.byteToUnsignedShort(u2Arr);

        // descriptor_index
        Stream.readU2Simple(content, index, u2Arr);
        index += 2;
        int descriptorIndex = DataTranslate.byteToUnsignedShort(u2Arr);

        // 将解析出来的内容存到ConstantPool中
        // 将nameIndex(u2)与descriptorIndex(u2)拼成一个int(u4)，前十六位是nameIndex，后十六位是descriptorIndex
        klass.getConstantPool().getDataMap().put(constantPoolIndex, nameIndex << 16 | descriptorIndex);

        log.info("\t第 " + constantPoolIndex + " 个: 类型: NameAndType，值: 0x" + Integer.toHexString((int) klass.getConstantPool().getDataMap().get(constantPoolIndex)));
        return index;
    }

    /**
     * 解析常量池 JVM_CONSTANT_InterfaceMethod 结构
     * @param content 字节流
     * @param klass 解析成的instanceKlass实例
     * @param index 当前解析索引
     * @param constantPoolIndex 该结构在常量池中的索引
     * @return 解析完成之后的当前解析索引
     * */
    private static int parseJvmConstantInterfaceMethod(byte[] content, InstanceKlass klass, int index, int constantPoolIndex) {
        // 中转字节数组，可复用
        byte[] u2Arr = new byte[2];

        // 将 index-->tag 的映射关系写入常量池
        klass.getConstantPool().getTag()[constantPoolIndex] = ConstantPool.JVM_CONSTANT_InterfaceMethodref;

        // class_index
        Stream.readU2Simple(content, index, u2Arr);
        index += 2;
        int classIndex = DataTranslate.byteToUnsignedShort(u2Arr);

        // name_and_type_index
        Stream.readU2Simple(content, index, u2Arr);
        index += 2;
        int nameAndTypeIndex = DataTranslate.byteToUnsignedShort(u2Arr);

        // 将解析出来的内容存到ConstantPool中
        // 将classIndex(u2)与nameAndTypeIndex(u2)拼成一个int(u4)，前十六位是classIndex，后十六位是nameAndTypeIndex
        klass.getConstantPool().getDataMap().put(constantPoolIndex, classIndex << 16 | nameAndTypeIndex);

        log.info("\t第 " + constantPoolIndex + " 个: 类型: InterfaceMethod，值: 0x" + Integer.toHexString((int) klass.getConstantPool().getDataMap().get(constantPoolIndex)));
        return index;
    }

    /**
     * 解析常量池 JVM_CONSTANT_Method 结构
     * @param content 字节流
     * @param klass 解析成的instanceKlass实例
     * @param index 当前解析索引
     * @param constantPoolIndex 该结构在常量池中的索引
     * @return 解析完成之后的当前解析索引
     * */
    private static int parseJvmConstantMethod(byte[] content, InstanceKlass klass, int index, int constantPoolIndex) {
        // 中转字节数组，可复用
        byte[] u2Arr = new byte[2];

        // 将 index-->tag 的映射关系写入常量池
        klass.getConstantPool().getTag()[constantPoolIndex] = ConstantPool.JVM_CONSTANT_Methodref;

        // class_index
        Stream.readU2Simple(content, index, u2Arr);
        index += 2;
        int classIndex = DataTranslate.byteToUnsignedShort(u2Arr);

        // name_and_type_index
        Stream.readU2Simple(content, index, u2Arr);
        index += 2;
        int nameAndTypeIndex = DataTranslate.byteToUnsignedShort(u2Arr);

        // 将解析出来的内容存到ConstantPool中
        // 将classIndex(u2)与nameAndTypeIndex(u2)拼成一个int(u4)，前十六位是classIndex，后十六位是nameAndTypeIndex
        klass.getConstantPool().getDataMap().put(constantPoolIndex, classIndex << 16 | nameAndTypeIndex);

        log.info("\t第 " + constantPoolIndex + " 个: 类型: Method，值: 0x" + Integer.toHexString((int) klass.getConstantPool().getDataMap().get(constantPoolIndex)));
        return index;
    }

    /**
     * 解析常量池 JVM_CONSTANT_Field 结构
     * @param content 字节流
     * @param klass 解析成的instanceKlass实例
     * @param index 当前解析索引
     * @param constantPoolIndex 该结构在常量池中的索引
     * @return 解析完成之后的当前解析索引
     * */
    private static int parseJvmConstantField(byte[] content, InstanceKlass klass, int index, int constantPoolIndex) {
        // 中转字节数组，可复用
        byte[] u2Arr = new byte[2];

        // 将 index-->tag 的映射关系写入常量池
        klass.getConstantPool().getTag()[constantPoolIndex] = ConstantPool.JVM_CONSTANT_Fieldref;

        // class_index
        Stream.readU2Simple(content, index, u2Arr);
        index += 2;
        int classIndex = DataTranslate.byteToUnsignedShort(u2Arr);

        // name_and_type_index
        Stream.readU2Simple(content, index, u2Arr);
        index += 2;
        int nameAndTypeIndex = DataTranslate.byteToUnsignedShort(u2Arr);

        // 将解析出来的内容存到ConstantPool中
        // 将classIndex(u2)与nameAndTypeIndex(u2)拼成一个int(u4)，前十六位是classIndex，后十六位是nameAndTypeIndex
        klass.getConstantPool().getDataMap().put(constantPoolIndex, classIndex << 16 | nameAndTypeIndex);

        log.info("\t第 " + constantPoolIndex + " 个: 类型: Field，值: 0x" + Integer.toHexString((int) klass.getConstantPool().getDataMap().get(constantPoolIndex)));
        return index;
    }


    /**
     * 解析常量池 JVM_CONSTANT_Class 结构
     * @param content 字节流
     * @param klass 解析成的instanceKlass实例
     * @param index 当前解析索引
     * @param constantPoolIndex 该结构在常量池中的索引
     * @return 解析完成之后的当前解析索引
     * */
    private static int parseJvmConstantClass(byte[] content, InstanceKlass klass, int index, int constantPoolIndex) {
        // 中转字节数组，可复用
        byte[] u2Arr = new byte[2];

        // 将 index-->tag 的映射关系写入常量池
        klass.getConstantPool().getTag()[constantPoolIndex] = ConstantPool.JVM_CONSTANT_Class;

        // name_index
        Stream.readU2Simple(content, index, u2Arr);
        index += 2;

        // 将解析出来的内容存到ConstantPool中
        klass.getConstantPool().getDataMap().put(constantPoolIndex, DataTranslate.byteToUnsignedShort(u2Arr));

        log.info("\t第 " + constantPoolIndex + " 个: 类型: Class，值: " + klass.getConstantPool().getDataMap().get(constantPoolIndex));
        return index;
    }


    /**
     * 解析常量池 JVM_CONSTANT_String 结构
     * @param content 字节流
     * @param klass 解析成的instanceKlass实例
     * @param index 当前解析索引
     * @param constantPoolIndex 该结构在常量池中的索引
     * @return 解析完成之后的当前解析索引
     * */
    private static int parseJvmConstantString(byte[] content, InstanceKlass klass, int index, int constantPoolIndex) {
        // 中转字节数组，可复用
        byte[] u2Arr = new byte[2];

        // 将 index-->tag 的映射关系写入常量池
        klass.getConstantPool().getTag()[constantPoolIndex] = ConstantPool.JVM_CONSTANT_String;

        // string_index
        Stream.readU2Simple(content, index, u2Arr);
        index += 2;

        // 将解析出来的内容存到ConstantPool中
        klass.getConstantPool().getDataMap().put(constantPoolIndex, DataTranslate.byteToUnsignedShort(u2Arr));

        log.info("\t第 " + constantPoolIndex + " 个: 类型: String，值无法获取，因为字符串的内容还未解析到");
        return index;
    }

    /**
     * 解析常量池 JVM_CONSTANT_Double 结构
     * @param content 字节流
     * @param klass 解析成的instanceKlass实例
     * @param index 当前解析索引
     * @param constantPoolIndex 该结构在常量池中的索引
     * @return 解析完成之后的当前解析索引
     * */
    private static int parseJvmConstantDouble(byte[] content, InstanceKlass klass, int index, int constantPoolIndex) {
        // 中转字节数组，可复用
        byte[] u8Arr = new byte[8];

        // 将 index-->tag 的映射关系写入常量池
        klass.getConstantPool().getTag()[constantPoolIndex] = ConstantPool.JVM_CONSTANT_Double;
        // Double
        Stream.readU8Simple(content, index, u8Arr);
        index += 8;
        // 将解析出来的内容存到ConstantPool中
        klass.getConstantPool().getDataMap().put(constantPoolIndex, DataTranslate.byteToDouble(u8Arr));
        log.info("\t第 " + constantPoolIndex + " 个: 类型: Double，值: " + klass.getConstantPool().getDataMap().get(constantPoolIndex));

        // 因为一个Double在常量池中需要两个表项来存储
        klass.getConstantPool().getTag()[++constantPoolIndex] = ConstantPool.JVM_CONSTANT_Double;
        klass.getConstantPool().getDataMap().put(constantPoolIndex, DataTranslate.byteToDouble(u8Arr));

        log.info("\t第 " + constantPoolIndex + " 个: 类型: Double，值: " + klass.getConstantPool().getDataMap().get(constantPoolIndex));
        return index;
    }

    /**
     * 解析常量池 JVM_CONSTANT_Long 结构
     * @param content 字节流
     * @param klass 解析成的instanceKlass实例
     * @param index 当前解析索引
     * @param constantPoolIndex 该结构在常量池中的索引
     * @return 解析完成之后的当前解析索引
     * */
    private static int parseJvmConstantLong(byte[] content, InstanceKlass klass, int index, int constantPoolIndex) {
        // 中转字节数组，可复用
        byte[] u8Arr = new byte[8];

        // 将 index-->tag 的映射关系写入常量池
        klass.getConstantPool().getTag()[constantPoolIndex] = ConstantPool.JVM_CONSTANT_Long;
        // Long
        Stream.readU8Simple(content, index, u8Arr);
        index += 8;
        // 将解析出来的内容存到ConstantPool中
        klass.getConstantPool().getDataMap().put(constantPoolIndex, DataTranslate.byteToLong(u8Arr));
        log.info("\t第 " + constantPoolIndex + " 个: 类型: Long，值: " + klass.getConstantPool().getDataMap().get(constantPoolIndex));


        // 因为一个Long在常量池中需要两个表项来存储
        klass.getConstantPool().getTag()[++constantPoolIndex] = ConstantPool.JVM_CONSTANT_Long;
        klass.getConstantPool().getDataMap().put(constantPoolIndex, DataTranslate.byteToLong(u8Arr));

        log.info("\t第 " + constantPoolIndex + " 个: 类型: Long，值: " + klass.getConstantPool().getDataMap().get(constantPoolIndex));
        return index;
    }

    /**
     * 解析常量池 JVM_CONSTANT_Float 结构
     * @param content 字节流
     * @param klass 解析成的instanceKlass实例
     * @param index 当前解析索引
     * @param constantPoolIndex 该结构在常量池中的索引
     * @return 解析完成之后的当前解析索引
     * */
    private static int parseJvmConstantFloat(byte[] content, InstanceKlass klass, int index, int constantPoolIndex) {
        // 中转字节数组，可复用
        byte[] u4Arr = new byte[4];

        // 将 index-->tag 的映射关系写入常量池
        klass.getConstantPool().getTag()[constantPoolIndex] = ConstantPool.JVM_CONSTANT_Float;

        // Float
        Stream.readU4Simple(content, index, u4Arr);
        index += 4;

        // 将解析出来的内容存到ConstantPool中
        klass.getConstantPool().getDataMap().put(constantPoolIndex, DataTranslate.byteToFloat(u4Arr));

        log.info("\t第 " + constantPoolIndex + " 个: 类型: Float，值: " + klass.getConstantPool().getDataMap().get(constantPoolIndex));
        return index;
    }

    /**
     * 解析常量池 JVM_CONSTANT_Integer 结构
     * @param content 字节流
     * @param klass 解析成的instanceKlass实例
     * @param index 当前解析索引
     * @param constantPoolIndex 该结构在常量池中的索引
     * @return 解析完成之后的当前解析索引
     * */
    private static int parseJvmConstantInteger(byte[] content, InstanceKlass klass, int index, int constantPoolIndex) {
        // 中转字节数组，可复用
        byte[] u4Arr = new byte[4];

        // 将 index-->tag 的映射关系写入常量池
        klass.getConstantPool().getTag()[constantPoolIndex] = ConstantPool.JVM_CONSTANT_Integer;

        // Integer
        Stream.readU4Simple(content, index, u4Arr);
        index += 4;

        // 将解析出来的内容存到ConstantPool中
        klass.getConstantPool().getDataMap().put(constantPoolIndex, DataTranslate.byteToInt(u4Arr));

        log.info("\t第 " + constantPoolIndex + " 个: 类型: Integer，值: " + klass.getConstantPool().getDataMap().get(constantPoolIndex));
        return index;
    }

    /**
     * 解析常量池 JVM_CONSTANT_Utf8 结构
     * @param content 字节流
     * @param klass 解析成的instanceKlass实例
     * @param index 当前解析索引
     * @param constantPoolIndex 该结构在常量池中的索引
     * @return 解析完成之后的当前解析索引
     * */
    private static int parseJvmConstantUtf8(byte[] content, InstanceKlass klass, int index, int constantPoolIndex) {
        // 中转字节数组，可复用
        byte[] u2Arr = new byte[2];

        // 将 index-->tag 的映射关系写入常量池
        klass.getConstantPool().getTag()[constantPoolIndex] = ConstantPool.JVM_CONSTANT_Utf8;

        // 字符串长度
        Stream.readU2Simple(content, index, u2Arr);
        index += 2;
        int length = DataTranslate.byteToUnsignedShort(u2Arr);

        // 字符串内容
        byte[] string = new byte[length];
        Stream.readSimple(content, index, length, string);
        index += length;

        // 将解析出来的内容存到ConstantPool中
        klass.getConstantPool().getDataMap().put(constantPoolIndex, new String(string));

        log.info("\t第 " + constantPoolIndex + " 个: 类型: utf8，值: " + klass.getConstantPool().getDataMap().get(constantPoolIndex));

        return index;
    }
}
