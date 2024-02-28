package io.github.simonalexs.tools.entertainment;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public class DouYuMsgUtil {
    public static byte[] makeSendBytes(String msg) throws IOException {
        ByteArrayOutputStream outBytes = new ByteArrayOutputStream(getPacketSize(msg));
        outBytes.write(intToggle(getPacketSize(msg)));
        outBytes.write(intToggle(getPacketSize(msg)));
        outBytes.write(shortToggle((short) 689));
        outBytes.write(0);
        outBytes.write(0);
        outBytes.write(msg.getBytes());
        outBytes.write(0);
        return outBytes.toByteArray();
    }
    // TODO 正确解析字节流，使Arrays.copyOfRange不报错outOfMemmory
    public static List<String> getReceivedMsg(byte[] byteArray){
        List<String> res = new ArrayList<String>();

        // 不按规则解析字节流
        try {
            String msg = new String(byteArray, "utf-8");
            String[] msgs = msg.substring(msg.indexOf("type@=")).split("type@=");
            int sizes = msgs.length;
            for(int i = 0; i < sizes; i++){
                msgs[i] = "type@=" + msgs[i];
            }
            res.addAll(Arrays.asList(msgs));
            res.remove(0);//移除第一个空白的元素
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            res.add("");
        }
        // 按规则解析字节流
        /*for(int i = 0; i < byteArray.length; i++){
            int contentLength = bytesToIntLittle(Arrays.copyOfRange(byteArray, i, i + 4));
            String mm = "";
            try {
                //byte[] msgByteArray = new byte[contentLength];
                //msgByteArray = Arrays.copyOfRange(byteArray, i + 12, i + 12 + contentLength);
                mm = new String(Arrays.copyOfRange(byteArray, i + 12, i + 12 + contentLength));
            } catch (Exception e) {
                //e.printStackTrace();
                mm = "字符串错误";
            }
            res.add(mm);
            i += 12 + contentLength;
        }*/
        return res;
    }
    public static int getPacketSize(String msg) {
        return 4 + 4 + msg.length() + 1;
    }
    public static byte[] intToggle(int value) {
        byte[] result = new byte[4];
        result[3] = (byte) ((value >> 24) & 0xFF);
        result[2] = (byte) ((value >> 16) & 0xFF);
        result[1] = (byte) ((value >> 8) & 0xFF);
        result[0] = (byte) (value & 0xFF);
        return result;
    }
    public static byte[] shortToggle(short value) {
        byte[] result = new byte[2];
        result[1] = (byte) ((value >> 8) & 0xFF);
        result[0] = (byte) (value & 0xFF);
        return result;
    }
    public static int bytesToIntLittle(byte[] src) {
        int value;
        value = (int) ((src[0] & 0xFF)
                | ((src[1] & 0xFF) << 8)
                | ((src[2] & 0xFF) << 16)
                | ((src[3] & 0xFF) << 24));
        return value;
    }

    //接收到的消息转化为对应的类
    public static Map<String, String> msgToMap(String message) {
        Map<String, String> rMap = new Hashtable<>();
        String[] messages = message.split("/");
        for (String item : messages) {
            String value = "";
            String[] items = item.split("@=");
            if (items[0] == null || "".equals(items[0])) break;
            if (items.length == 2) value = items[1];
            if (!"".equals(value)) {
                value = value.replaceAll("@S", "/").replaceAll("@A", "@");
            }
            rMap.put(items[0], value);
        }
        return rMap;
    }

    /**
    * Map转成实体对象
    * @param map map实体对象包含属性
    * @param clazz 实体对象类型
    * @return 实体对象
    */
    public static Object mapToObject(Map<String, String> map, Class<?> clazz) {
        if (map == null) {
            return null;
        }
        Object obj = null;
        try {
            obj = clazz.getDeclaredConstructor().newInstance();
            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                int mod = field.getModifiers();
                if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                    continue;
                }
                field.setAccessible(true);
                field.set(obj, map.get(field.getName()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }
    public static String getProperty(Properties properties, String name) throws UnsupportedEncodingException {
        return new String(properties.getProperty(name).getBytes("iso-8859-1"), "gbk");
    }
}
