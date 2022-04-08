package com.dbschema.mongo;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * SQL格式化工具
 * <p>
 * 感谢Hutool提供的字符串格式化方法，移植自Hotool-Core:5.7.22
 * 感谢部分工具类的作者 Looly、xiaoleilu
 *
 * @author spencer
 * @see <a href="https://github.com/dromara/hutool">Hutool</a>
 */
public class StatementFormatter {

    /**
     * null值参数使用该字符串
     **/
    private static final String NULL = "null";
    /**
     * 转义
     **/
    private static final char BACKSLASH = '\\';
    /**
     * 占位符
     **/
    private static final String PLACE_HOLDER = "?";

    private StatementFormatter() {
    }

    public static int getParamCount(String sql) {
        return StringUtils.countMatches(sql, PLACE_HOLDER);
    }

    // ---------------------------------------- 以下是来自Hutool的字符串格式化方案 ----------------------------------------

    /**
     * 格式化文本, ? 表示占位符<br>
     * 此方法只是简单将占位符 ? 按照顺序替换为参数<br>
     * 如果想输出 ? 使用 \\转义 { 即可，如果想输出 ? 之前的 \ 使用双转义符 \\\\ 即可<br>
     * 例：<br>
     * 通常使用：format("this is ? for ?", "a", "b") =》 this is a for b<br>
     * 转义?： format("this is \\? for ?", "a", "b") =》 this is \? for a<br>
     * 转义\： format("this is \\\\? for ?", "a", "b") =》 this is \a for b<br>
     *
     * @param template 文本模板，被替换的部分用 ? 表示，如果模板为null，返回"null"
     * @param params   参数值
     * @return 格式化后的文本，如果模板为null，返回"null"
     */
    public static String format(CharSequence template, Object... params) {
        if (null == template) {
            return NULL;
        }
        if (ArrayUtils.isEmpty(params) || StringUtils.isBlank(template)) {
            return template.toString();
        }
        return format(template.toString(), params);
    }

    /**
     * 格式化字符串<br>
     * 此方法只是简单将占位符 ? 按照顺序替换为参数<br>
     * 如果想输出 ? 使用 \\转义 { 即可，如果想输出 ? 之前的 \ 使用双转义符 \\\\ 即可<br>
     * 例：<br>
     * 通常使用：format("this is ? for ?", "a", "b") =》 this is a for b<br>
     * 转义?： format("this is \\? for ?", "a", "b") =》 this is \? for a<br>
     * 转义\： format("this is \\\\? for ?", "a", "b") =》 this is \a for b<br>
     *
     * @param strPattern 字符串模板
     * @param argArray   参数列表
     * @return 结果
     */
    private static String format(String strPattern, Object... argArray) {
        return formatWith(strPattern, PLACE_HOLDER, argArray);
    }

    /**
     * 格式化字符串<br>
     * 此方法只是简单将指定占位符 按照顺序替换为参数<br>
     * 如果想输出占位符使用 \\转义即可，如果想输出占位符之前的 \ 使用双转义符 \\\\ 即可<br>
     * 例：<br>
     * 通常使用：format("this is ? for ?", "?", "a", "b") =》 this is a for b<br>
     * 转义?： format("this is \\? for ?", "?", "a", "b") =》 this is ? for a<br>
     * 转义\： format("this is \\\\? for ?", "?", "a", "b") =》 this is \a for b<br>
     *
     * @param strPattern  字符串模板
     * @param placeHolder 占位符，例如?
     * @param argArray    参数列表
     * @return 结果
     * @since 5.7.14
     */
    public static String formatWith(String strPattern, String placeHolder, Object... argArray) {
        if (StringUtils.isBlank(strPattern) || StringUtils.isBlank(placeHolder) || ArrayUtils.isEmpty(argArray)) {
            return strPattern;
        }
        final int strPatternLength = strPattern.length();
        final int placeHolderLength = placeHolder.length();

        // 初始化定义好的长度以获得更好的性能
        final StringBuilder sbuf = new StringBuilder(strPatternLength + 50);

        // 记录已经处理到的位置
        int handledPosition = 0;
        // 占位符所在位置
        int delimIndex;
        for (int argIndex = 0; argIndex < argArray.length; argIndex++) {
            delimIndex = strPattern.indexOf(placeHolder, handledPosition);
            // 剩余部分无占位符
            if (delimIndex == -1) {
                // 不带占位符的模板直接返回
                if (handledPosition == 0) {
                    return strPattern;
                }
                // 字符串模板剩余部分不再包含占位符，加入剩余部分后返回结果
                sbuf.append(strPattern, handledPosition, strPatternLength);
                return sbuf.toString();
            }

            // 转义符
            if (delimIndex > 0 && strPattern.charAt(delimIndex - 1) == BACKSLASH) {
                // 双转义符
                if (delimIndex > 1 && strPattern.charAt(delimIndex - 2) == BACKSLASH) {
                    // 转义符之前还有一个转义符，占位符依旧有效
                    sbuf.append(strPattern, handledPosition, delimIndex - 1);
                    sbuf.append(utf8Str(argArray[argIndex]));
                    handledPosition = delimIndex + placeHolderLength;
                } else {
                    // 占位符被转义
                    argIndex--;
                    sbuf.append(strPattern, handledPosition, delimIndex - 1);
                    sbuf.append(placeHolder.charAt(0));
                    handledPosition = delimIndex + 1;
                }
            } else {// 正常占位符
                sbuf.append(strPattern, handledPosition, delimIndex);
                sbuf.append(utf8Str(argArray[argIndex]));
                handledPosition = delimIndex + placeHolderLength;
            }
        }

        // 加入最后一个占位符后所有的字符
        sbuf.append(strPattern, handledPosition, strPatternLength);

        return sbuf.toString();
    }

    /**
     * 将对象转为字符串<br>
     *
     * <pre>
     * 1、Byte数组和ByteBuffer会被转换为对应字符串的数组
     * 2、对象数组会调用Arrays.toString方法
     * </pre>
     *
     * @param obj 对象
     * @return 字符串
     */
    public static String utf8Str(Object obj) {
        return str(obj, StandardCharsets.UTF_8);
    }

    /**
     * 将对象转为字符串
     * <pre>
     * 	 1、Byte数组和ByteBuffer会被转换为对应字符串的数组
     * 	 2、对象数组会调用Arrays.toString方法
     * </pre>
     *
     * @param obj     对象
     * @param charset 字符集
     * @return 字符串
     */
    public static String str(Object obj, Charset charset) {
        if (null == obj) {
            return null;
        }

        if (obj instanceof String) {
            return (String)obj;
        } else if (obj instanceof byte[]) {
            return str((byte[])obj, charset);
        } else if (obj instanceof Byte[]) {
            return str((Byte[])obj, charset);
        } else if (obj instanceof ByteBuffer) {
            return str((ByteBuffer)obj, charset);
        } else if (obj.getClass()
                      .isArray()) {
            return ArrayUtils.toString(obj);
        }

        return obj.toString();
    }

    /**
     * 解码字节码
     *
     * @param data    字符串
     * @param charset 字符集，如果此字段为空，则解码的结果取决于平台
     * @return 解码后的字符串
     */
    public static String str(byte[] data, Charset charset) {
        if (data == null) {
            return null;
        }

        if (null == charset) {
            return new String(data);
        }
        return new String(data, charset);
    }

    /**
     * 解码字节码
     *
     * @param data    字符串
     * @param charset 字符集，如果此字段为空，则解码的结果取决于平台
     * @return 解码后的字符串
     */
    public static String str(Byte[] data, Charset charset) {
        if (data == null) {
            return null;
        }

        byte[] bytes = new byte[data.length];
        Byte dataByte;
        for (int i = 0; i < data.length; i++) {
            dataByte = data[i];
            bytes[i] = (null == dataByte) ? -1 : dataByte;
        }

        return str(bytes, charset);
    }

    /**
     * 将编码的byteBuffer数据转换为字符串
     *
     * @param data    数据
     * @param charset 字符集，如果为空使用当前系统字符集
     * @return 字符串
     */
    public static String str(ByteBuffer data, Charset charset) {
        if (null == charset) {
            charset = Charset.defaultCharset();
        }
        return charset.decode(data)
                      .toString();
    }
}
