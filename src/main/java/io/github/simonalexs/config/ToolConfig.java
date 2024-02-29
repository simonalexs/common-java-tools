package io.github.simonalexs.config;


import io.github.simonalexs.tools.FileUtil;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.Properties;

/**
 * 读取用户的配置信息
 */
public class ToolConfig {
    private static final Yaml yaml = new Yaml();
    private static final LinkedHashMap<String, String> USER_CONFIG_MAP = new LinkedHashMap<>();
    private static final String TOOL_PROPERTY_FILE_NAME = "/tool.properties";
    public static final String PROPERTY_FILE_NAME;
    public static final String PROPERTY_FILE_TEMPLATE_CONTENT;

    static  {
        String separator = "/";

        try {
            Properties properties = new Properties();
            InputStream inputStream = ToolConfig.class.getResourceAsStream(TOOL_PROPERTY_FILE_NAME);
            properties.load(inputStream);
            String userConfigFileTemplateName = properties.get("userConfigTemplateFileName").toString();
            PROPERTY_FILE_TEMPLATE_CONTENT = FileUtil.getContentInResourceOrSamePath(separator + userConfigFileTemplateName);

            PROPERTY_FILE_NAME = properties.get("userConfigFileName").toString();
            String userConfigStr = null;
            try {
                userConfigStr = FileUtil.getContentInResourceOrSamePath(separator + PROPERTY_FILE_NAME);
            } catch (Exception e) {
                // 默认无参数
                userConfigStr = "";
            }
            try {
                LinkedHashMap<?, ?> map = parse(userConfigStr, LinkedHashMap.class);
                map.forEach((k, v) -> {
                    USER_CONFIG_MAP.put(k.toString(), v == null ? null : v.toString());
                });
            } catch (Exception e) {
                throw new RuntimeException("配置文件内容有误，请检查文件" + PROPERTY_FILE_NAME + "中的内容 " + e.getMessage());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static<T> T getParam(String key, Class<T> clazz) {
        Optional<String> keyOptional =
                USER_CONFIG_MAP.keySet().stream().filter(t -> t.equalsIgnoreCase(key)).findFirst();
        if (!keyOptional.isPresent()) {
            return null;
        }
        String valueStr = USER_CONFIG_MAP.get(keyOptional.get());
        if (valueStr == null || valueStr.trim().isEmpty()) {
            return null;
        }
        return parse(valueStr, clazz);
    }

    public static<T> T getParamAndCheck(String key, Class<T> clazz) {
        T value = getParam(key, clazz);
        if (value == null || value.toString().trim().isEmpty()) {
            throw new RuntimeException(getParamErrorInfo(key));
        }
        return value;
    }

    private static<T> T parse(String str, Class<T> clazz) {
        try {
            return yaml.loadAs(str, clazz);
        } catch (Exception e) {
            throw new RuntimeException("配置文件格式有误，请检查文件" + PROPERTY_FILE_NAME + "中的内容[" + str + "] " + e.getMessage());
        }
    }

    public static String getParamErrorInfo(String key) {
        return "Param [" + key + "] doesn't exist or is empty, please check the file '" + PROPERTY_FILE_NAME + "', " +
                "which is the same path with the jar file or in resources.The template content of the file is:"
                + System.lineSeparator()
                + PROPERTY_FILE_TEMPLATE_CONTENT;
    }
}
