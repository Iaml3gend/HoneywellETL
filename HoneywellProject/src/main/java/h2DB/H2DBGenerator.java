package com.albertsons.catalog.mc.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.SparkSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static utils.constants.PASSWORD;

@Slf4j
public class H2DBGenerator {
    public static void executeSqlScript(SparkSession spark) {
        try {
            Class.forName("org.h2.Driver");
            Connection connection = DriverManager.getConnection("jdbc:h2:./data", "user", PASSWORD);
            InputStream inputStream = H2DBGenerator.class.getResourceAsStream("/schema.sql");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sqlScript = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sqlScript.append(line).append("\n");
            }
            Statement statement = connection.createStatement();
            statement.execute(sqlScript.toString());
            System.out.println("SQL script executed successfully.");
            connection.close();
        } catch (ClassNotFoundException | SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}
