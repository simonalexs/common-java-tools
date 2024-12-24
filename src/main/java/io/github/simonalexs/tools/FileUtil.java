package io.github.simonalexs.tools;

import io.github.simonalexs.annotation.Func;
import io.github.simonalexs.annotation.Param;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.SQLException;

public class FileUtil {
    public static String read(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return "";
            }
            // 帮我读取文件的内容
            return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void write(String filePath, String content) {
        write(filePath, content, false);
    }

    public static void write(String filePath, String content, boolean append) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWritter = new FileWriter(file, append);
            fileWritter.write(content);
            fileWritter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Func
    public static String getContentInResourceOrSamePath(@Param(tip = "path of file starting with '/'") String path) throws Exception {
        path = path.replaceAll("\r|\n|\r\n", "");
        try {
            return getContentInResource(path);
        } catch (RuntimeException e) {
            try {
                return getContentInSamePath(path);
            } catch (RuntimeException e2) {
                throw new Exception("配置文件读取失败：" + e2.getMessage());
            }
        }
    }

    /**
     * 获取Resources目录下的文件内容（无论是本项目内使用，还是本项目打成jar包后在其他项目处调用，都能正确读取到配置文件的内容）
     * @param path 路径
     * @return 文件内容
     * @throws Exception 异常
     */
    public static String getContentInResource(String path) throws Exception {
        InputStream is = FileUtil.class.getResourceAsStream(path);
        if (is == null) {
            throw new SQLException("未找到文件：" + path);
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        return doGetBufferString(in);
    }

    /**
     * 读取“和本驱动打包后的jar包同级目录下的路径path”的文件内容
     */
    private static String getContentInSamePath(String path) throws Exception {
        String base = getJarPath();
        String decodeBase = null;
        try {
            decodeBase = URLDecoder.decode(base, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedEncodingException("URL解码失败【" + base + "】：" + e.getMessage());
        }
        File file = new File(decodeBase, path);
        if (!file.exists()) {
            throw new FileNotFoundException("未找到文件：【" + decodeBase + path + "】");
        }
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            return doGetBufferString(br);
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("未找到文件：【" + path + "】");
        }
    }

    private static String doGetBufferString(BufferedReader in) throws IOException {
        StringBuilder buffer = new StringBuilder();
        String line = "";
        while(true) {
            line = in.readLine();
            if (line == null) {
                break;
            }
            if (buffer.length() != 0) {
                buffer.append(System.lineSeparator());
            }
            buffer.append(line);
        }
        return buffer.toString();
    }

    public static void writeContentInSamePath(String path, String content) throws Exception {
        writeContentInSamePath(path, content, false);
    }

    public static void writeContentInSamePath(String path, String content, boolean append) throws Exception {
        path = path.replaceAll("\r|\n|\r\n", "");
        try {
            String base = getJarPath();
            String decodeBase = null;
            try {
                decodeBase = URLDecoder.decode(base, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new UnsupportedEncodingException("URL解码失败【" + base + "】：" + e.getMessage());
            }
            File file = new File(decodeBase, path);
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    throw new Exception("文件创建失败：" + path);
                }
            }
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, append));
            if (append) {
                bufferedWriter.newLine();
            }
            bufferedWriter.write(content);
            bufferedWriter.close();
        } catch (Exception e2) {
            throw new Exception("写入文件内容失败：" + e2.getMessage());
        }
    }

    /**
     * 获取项目加载类的根路径
     */
    private static String getJarPath() {
        String path = "";
        //jar 中没有目录的概念
        //获得当前的URL
        URL location = FileUtil.class.getProtectionDomain().getCodeSource().getLocation();
        //构建指向当前URL的文件描述符
        File file = new File(location.getPath());
        //如果是目录,指向的是包所在路径，而不是文件所在路径
        if (file.isDirectory()) {
            //直接返回绝对路径
            path = file.getAbsolutePath();
        } else {
            //如果是文件,这个文件指定的是jar所在的路径(注意如果是作为依赖包，这个路径是jvm启动加载的jar文件名)
            //返回jar所在的父路径
            path = file.getParent();
        }
        return path;
    }
}
