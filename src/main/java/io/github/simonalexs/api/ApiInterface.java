package io.github.simonalexs.api;

/**
 * 用于为其他项目、其他桌面软件提供全自动api。
 * 所有实现该接口的api都可以被其他项目扫描并作为他们的方法进行自动调用。
 * 这样可以实现此工具包添加新的工具方法或者工具类后，只要其他项目重新引用了最新的包，就可以不用修改那些项目的代码，自动添加上这些api的使用列表。
 */
public interface ApiInterface {
//    String call(List<ParamView> params);
}
