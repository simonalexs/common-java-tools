package io.github.simonalexs.tools.other;

public class MdBuilder {
    private static final String LINE_BREAK = "<br/>";
    // 不加此符号无法正常显示无序列表
    private static final String UNORDERED_LIST_REPAIR_CHAR = "\n";
    private static final int INITIAL_ORDER = 1;

    private String result = "";
    private int currentOrder = 1;

    public static MdBuilder getBuilder() {
        return getBuilder("");
    }

    public static MdBuilder getBuilder(String text) {
        return new MdBuilder(text);
    }

    public MdBuilder(String text) {
        this.result = text;
    }

    /**
     * 加粗字体
     * @param text 1
     * @return 2
     */
    public MdBuilder blod(String text) {
        result += "**" + text + "**" + " ";
        return this;
    }

    /**
     * 加粗字体
     * @param text 1
     * @return 2
     */
    public MdBuilder text(String text) {
        result += text;
        return this;
    }

    /**
     * 链接
     * @param url 1
     * @return 1
     */
    public MdBuilder link(String url) {
        result += "[" + url + "]" + "(" + url + ")";
        return this;
    }

    /**
     * 链接
     * @param title 1
     * @param url 1
     * @return 2
     */
    public MdBuilder link(String title, String url) {
        result += "[" + title + "]" + "(" + url + ")";
        return this;
    }

    /**
     * 多行代码块
     * @param text 1
     * @return 1
     */
    public MdBuilder codeMutilLine(String text) {
        result += "```" + text + "```" + "<br/>";
        return this;
    }

    /**
     * 单行代码块
     * @param text 1
     * @return 1
     */
    public MdBuilder codeSingleLine(String text) {
        result += "`" + text + "`";
        return this;
    }

    /**
     * 有序列表准备工作
     * @return 1
     */
    public MdBuilder prepareForListWithOrdered() {
        currentOrder = INITIAL_ORDER;
        return this;
    }

    /**
     * 有序列表
     * @return 1
     */
    public MdBuilder listWithOrdered() {
        return listWithOrdered("");
    }

    /**
     * 有序列表
     * @param text 1
     * @return 1
     */
    public MdBuilder listWithOrdered(String text) {
        result += currentOrder++ + ". " + text;
        return this;
    }

    /**
     * 无序列表准备工作
     * @return 1
     */
    public MdBuilder prepareForListWithUnordered() {
        result += UNORDERED_LIST_REPAIR_CHAR;
        return this;
    }

    /**
     * 无序列表
     * @return 1
     */
    public MdBuilder listWithUnordered() {
        return listWithUnordered("");
    }

    /**
     * 无序列表
     * @param text 1
     * @return 1
     */
    public MdBuilder listWithUnordered(String text) {
        result += UNORDERED_LIST_REPAIR_CHAR + "*  " + text;
        return this;
    }

    /**
     * 换行
     * @return 1
     */
    public MdBuilder lineBreak() {
        result += LINE_BREAK;
        return this;
    }

    public String build() {
        return result;
    }
}