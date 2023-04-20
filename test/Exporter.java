package com.mycicd.deploy.controller;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.io.*;
import javax.xml.stream.*;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Exporter {


    public static void main(String[] args) throws Exception {
        String url = "jdbc:postgresql://pdka00209-nat.bmwgroup.net:40099/mdbi_vck_tp33";
        String user = "qqc0c13";
        String password = "mdbint0_vcktp33adm";
        int chunkSize = 1000;
        int offset = 0;

        Connection connection = DriverManager.getConnection(url, user, password);
        Statement statement = connection.createStatement();

        while (true) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM t_datasheet ORDER BY id LIMIT " + chunkSize + " OFFSET " + offset);
            if (!resultSet.next()) {
                break;
            }
            String filename = "/Users/gmartirosyan/buckups/t_datasheet/19Apr23_1/" + offset + ".xml";
            FileWriter fileWriter = new FileWriter(filename);
            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
            XMLStreamWriter writer = outputFactory.createXMLStreamWriter(fileWriter);
            writer.writeStartDocument();
            writer.writeStartElement("data");
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (resultSet.next()) {
                writer.writeStartElement("row");
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    String columnValue = resultSet.getString(i);
                    writer.writeStartElement(columnName);
                    writer.writeCharacters(columnValue);
                    writer.writeEndElement();
                }
                writer.writeEndElement();
            }
            writer.writeEndElement();
            writer.writeEndDocument();
            writer.flush();
            writer.close();
            fileWriter.close();
            offset += chunkSize;
        }

        statement.close();
        connection.close();
    }


}


class CombineXmls {
    public static void main(String[] args) throws Exception {
        String exportDir = "/Users/gmartirosyan/buckups/t_datasheet/19Apr23_1/";
        String outputFileName = "/path/to/export/directory/all_t_datasheet.xml";

        // Get a list of all XML files in the export directory
        File exportDirFile = new File(exportDir);
        File[] xmlFiles = exportDirFile.listFiles((dir, name) -> name.endsWith(".xml"));
        ArrayList<Document> documents = new ArrayList<>();

        // Parse each XML file into a DOM Document object and add it to the list
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        assert xmlFiles != null;
        for (File xmlFile : xmlFiles) {
            Document doc = db.parse(xmlFile);
            documents.add(doc);
        }

        // Create a new DOM Document object for the combined XML
        Document combinedDoc = db.newDocument();
        Element rootElement = combinedDoc.createElement("data");
        combinedDoc.appendChild(rootElement);

        // Copy the contents of each document to the combined document
        for (Document doc : documents) {
            Element docElement = doc.getDocumentElement();
            for (int i = 0; i < docElement.getChildNodes().getLength(); i++) {
                Element row = (Element) docElement.getChildNodes().item(i);
                Element newRow = combinedDoc.createElement("row");
                rootElement.appendChild(newRow);
                for (int j = 0; j < row.getChildNodes().getLength(); j++) {
                    Element column = (Element) row.getChildNodes().item(j);
                    Element newColumn = combinedDoc.createElement(column.getTagName());
                    newColumn.setTextContent(column.getTextContent());
                    newRow.appendChild(newColumn);
                }
            }
        }

        // Write the combined document to a file
        OutputStream outputStream = Files.newOutputStream(Paths.get(outputFileName));
        javax.xml.transform.Transformer transformer = javax.xml.transform.TransformerFactory.newInstance().newTransformer();
        transformer.transform(new javax.xml.transform.dom.DOMSource(combinedDoc), new javax.xml.transform.stream.StreamResult(outputStream));
    }
}
