package io.github.simonalexs.tools.test;

import io.github.simonalexs.tools.FileUtil;
import io.github.simonalexs.tools.WinUtil;

public class FileUtilTest {

    public static void main(String[] args) throws Exception {
        String data = FileUtil.getContentInResourceOrSamePath("/data");

        StringBuilder builder = new StringBuilder();
        for (String table : data.split("\r\n")) {
//            builder.append(table + "\t" + "select\n")
//                    .append(table + "\t" + "insert\n")
//                    .append(table + "\t" + "update\n")
//                    .append(table + "\t" + "delete\n");
            builder.append("-- ").append(table).append("\n").append("\n")
                    .append("select * from " + table + ";").append("\n")
                    .append("insert into " + table + " values(1, 2);").append("\n")
                    .append("update " + table + " set =1 where pid=1387;").append("\n")
                    .append("delete from " + table + " where pid=1388;").append("\n")
                    .append("\n");
        }
        System.out.println(builder.toString());
        WinUtil.copy(builder.toString());
    }
}
