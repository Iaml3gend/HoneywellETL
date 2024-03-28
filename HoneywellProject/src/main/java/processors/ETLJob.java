package processors;

import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;

import java.io.Serializable;
import java.util.Date;
import java.util.Properties;

import static utils.constants.APP_NAME;
import static utils.constants.PASSWORD;

/**
 * <b>ETLjob<b/>
 * <p>Takes data from different sources and consolidats in to 1 target system(DB)</p>
 */
@Slf4j
public class ETLjob implements Serializable {
    private final static String PROCESSOR_NAME = "ETLjob";


    public static void main(String[] args) {

        log.info("-------- STARTING PROCESSOR:" + PROCESSOR_NAME + "-------------------");
        try {
            ApplicationContext context =
                    new AnnotationConfigApplicationContext(ETLjob.class);
            ETLjob ETLjob = context.getBean(ETLjob.class);
            Properties defaultProperties = ETLjob.getDefaultProperties();
            ETLjob.invokeDependencies(defaultProperties);

        } catch (Exception e) {
            log.error("Error in processor: " + PROCESSOR_NAME + ": ", e);
        }
        log.info("--------  ENDED PROCESSOR:" + PROCESSOR_NAME + "------------------------------");
    }


    public void invokeDependencies(Properties defaultProperties) {

        ETL(defaultProperties);

    }

    private void ETL(Properties defaultProperties) {
        long sourceCount = 0;
        SparkSession spark = null;
        try {
            log.info("initializing spark");

            spark = SparkSession.builder()
                    .appName(APP_NAME)
                    .config("spark.master", "local")
                    .getOrCreate();
            com.albertsons.catalog.mc.utils.H2DBGenerator.executeSqlScript(spark);

            log.info("Loading data from Different sources");

            Dataset<Row> h2Data = spark.read()
                    .format("jdbc")
                    .option("url", "jdbc:h2:./data/")
                    .option("user", "user")
                    .option("password", PASSWORD)
                    .load();

            Dataset<Row> csvData = spark.read().format("csv")
                    .option("header", true)
                    .option("inferSchema", true)
                    .load("./resources/customerCsv.csv");

            Dataset<Row> jsonData = spark.read().format("json")
                    .load("./resources/customerJson.json");
//combining data
            Dataset<Row> consolidatedData = csvData.union(jsonData).dropDuplicates();

            consolidatedData = consolidatedData.na().fill("");
            consolidatedData = consolidatedData.withColumnRenamed("DOB", "date_of_birth"); // Rename column to make consistenci
//using encrytpion to make passwords encrypted in teh final dbase
            consolidatedData = consolidatedData.withColumn("password", functions.sha2(consolidatedData.col("password"), 256));
            consolidatedData = consolidatedData.union(h2Data);

            log.info("Write consolidated data to H2 database");
            consolidatedData.write().format("jdbc")
                    .option("url", "jdbc:h2:./data")
                    .option("dbtable", "targetTable")
                    .option("user", "aravind")
                    .option("password", "KNEF^&#JNFkdf")
                    .mode(SaveMode.Append)
                    .save();

        } finally {
            spark.close();
        }
    }

    private Properties getDefaultProperties() {
        Properties properties = new Properties();
        properties.setProperty("time", Date.from(new Date().toInstant()).toString());
        return properties;
    }

}
