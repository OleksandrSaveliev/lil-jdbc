package com.my;

import java.sql.SQLException;

public class LilJdbcApplication {
    public static void main(String[] args) {
        DatabaseConnectionManager dbcm = new DatabaseConnectionManager(
                "localhost", "hplussport", "postgres", "password"
        );

        try {
            var connection = dbcm.getConnection();
                var statement = connection.createStatement();
                var resultSet = statement.executeQuery("SELECT COUNT(*) FROM customer");

                while (resultSet.next()) {
                    System.out.println(resultSet.getInt(1));
                }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
