package lms_pages;

import org.testng.annotations.DataProvider;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static lms_pages.BaseHelper.logger;

public class DataProviderClass {

    public static Object[][] readDataFromCSV(String fileName) {
        String csvFile = "./src/test/resources/csv/" + fileName;
        String line = "";
        String cvsSplitBy = ",";
        List<Object[]> records = new ArrayList<Object[]>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(cvsSplitBy);
                records.add(fields);
            }
        } catch (IOException e) {
            logger.error("Error reading CSV file", e);
        }

        Object[][] result = new Object[records.size()][];
        for (int i = 0; i < records.size(); i++) {
            result[i] = records.get(i);
        }
        return result;
    }

    @DataProvider(name = "invalidLoginData")
    public Object[][] getInvalidLoginData() {
        return readDataFromCSV("email_password_invalid.csv");
    }
}
