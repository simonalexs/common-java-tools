package io.github.simonalexs.tools.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 对原生jdbc操作数据库的简单封装
 */
public class JDBCManager {
    // TODO-high：原生jdbc方式操作数据库，增删改查数据，适配入参和结果集为“实体类和Map”
    //  设置参数：字段名是否区分大小写，是否自动下划线转驼峰。2024/03/27 08:43:45
    //  适配多个数据源：sqlLite、mysql、oracle等（通过枚举进行区分）

    private final Connection connection;

    public static JDBCManager connect(DbType dbType, String url) throws SQLException {
        return connect(dbType.getClassName(), url);
    }

    public static JDBCManager connect(String driverName, String url) throws SQLException {
        try {
            Class.forName(driverName);
        } catch (ClassNotFoundException e) {
            throw new SQLException(e.getMessage());
        }
        return new JDBCManager(DriverManager.getConnection(url));
    }

    private JDBCManager(Connection connection) {
        this.connection = connection;
    }

    public List<Map<String, Object>> query(String sql) throws SQLException {
        return query(sql, Integer.MAX_VALUE);
    }

    public List<Map<String, Object>> query(String sql, int maxQueryNum) throws SQLException {
        try {
            List<Map<String, Object>> rowList = new ArrayList<>();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            int count = 0;
            while (resultSet.next() && count < maxQueryNum) {
                Map<String, Object> row = new LinkedHashMap<>();
                // TODO-high：解析 result 数据。2024/03/27 09:09:04

                rowList.add(row);
            }
            resultSet.close();
            statement.close();
            return rowList;
        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }
    }

    public int update(String sql) throws SQLException {
        try {
            Statement statement = connection.createStatement();
            int updateCount = statement.executeUpdate(sql);
            statement.close();
            return updateCount;
        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }
    }

    public void disconnect() throws SQLException {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }
    }

    public enum DbType {
        SQLITE("org.sqlite.JDBC"),
        POSTGRE_SQL("org.postgresql.Driver"),
        MYSQL("com.mysql.jdbc.Driver"),
        ORACLE("oracle.jdbc.driver.OracleDriver");

        private final String className;

        DbType(String className) {
            this.className = className;
        }

        public String getClassName() {
            return className;
        }
    }
}
