import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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
        String csvFileName = "data.csv";
        String xmlFileName = "data.xml";
        List<Employee> listCSV = parseCSV(columnMapping, csvFileName);
        List<Employee> listXML = parseXML(xmlFileName);
        writeString(listToJson(listCSV), "dataCSV.json");
        writeString(listToJson(listXML), "dataXML.json");
    }

    static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (CSVReader reader = new CSVReader(new FileReader(fileName));) {
            ColumnPositionMappingStrategy mappingStrategy = new ColumnPositionMappingStrategy();
            mappingStrategy.setType(Employee.class);
            mappingStrategy.setColumnMapping(columnMapping);
            CsvToBeanBuilder csvBuilder = new CsvToBeanBuilder(reader)
                    .withMappingStrategy(mappingStrategy);
            CsvToBean csvToBean = csvBuilder.build();
            return csvToBean.parse();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    static String listToJson(List<Employee> list) {
        Type listType = new TypeToken<List<Employee>>() {}.getType();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String json = gson.toJson(list, listType);
        return json;
    }

    static void writeString (String jsonString, String fileName) {
        try (FileWriter file = new FileWriter(fileName)) {
            file.write(jsonString);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static List<Employee> parseXML(String fileName) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        List<Employee> list = new ArrayList<>();
        try {
            builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(fileName));
            NodeList nodeList = doc.getDocumentElement().getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (Node.ELEMENT_NODE == node.getNodeType()) {
                    Element element = (Element) node;
                    list.add(new Employee(Long.valueOf(element.getElementsByTagName("id").item(0).getTextContent()),
                            element.getElementsByTagName("firstName").item(0).getTextContent(),
                            element.getElementsByTagName("lastName").item(0).getTextContent(),
                            element.getElementsByTagName("country").item(0).getTextContent(),
                            Integer.valueOf(element.getElementsByTagName("age").item(0).getTextContent())));
                }
            }
            return list;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (SAXException e) {
            e.printStackTrace();
            return null;
        }
    }
}
