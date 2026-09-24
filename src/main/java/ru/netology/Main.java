package ru.netology;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};

        String fileNameCSV = "data.csv";
        List<Employee> list = parseCSV(columnMapping, fileNameCSV);
        String json = listToJson(list);
        writeString(json, "data1.json");

        String fileNameXML = "data.xml";
        List<Employee> listFromXml = parseXML(fileNameXML);
        String json1 = listToJson(listFromXml);
        writeString(json1, "data2.json");
    }

    private static void writeString(String json, String file) {
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(json);
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        return gson.toJson(list, listType);
    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> staff;
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            var strategy = new ColumnPositionMappingStrategy<Employee>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            staff = csv.parse();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return staff;
    }

    public static List<Employee> parseXML(String fileName) {
        List<Employee> employeeList = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = (Document) builder.parse(new File(fileName));
            Node root = ((org.w3c.dom.Document) doc).getDocumentElement();
            NodeList nodeList = root.getChildNodes();

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);

                if (Node.ELEMENT_NODE == node.getNodeType()) {
                    Element employee = (Element) node;

                    String idString = employee.getElementsByTagName("id").item(0).getTextContent();
                    long id = 0;
                    if (!idString.isEmpty()) {
                        id = Long.parseLong(idString);
                    }

                    String firstName = employee.getElementsByTagName("firstName").item(0).getTextContent();
                    String lastName = employee.getElementsByTagName("lastName").item(0).getTextContent();
                    String country = employee.getElementsByTagName("country").item(0).getTextContent();
                    int age = Integer.parseInt(employee.getElementsByTagName("age").item(0).getTextContent());
                    Employee employees = new Employee(id, firstName, lastName, country, age);
                    employeeList.add(employees);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return employeeList;
    }
}