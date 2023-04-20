package com.mycicd.deploy;


import javax.xml.stream.*;
import java.io.*;


class Main {

    public static long countRecordElements(String filePath) throws XMLStreamException, IOException {
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        FileInputStream fileInputStream = new FileInputStream(filePath);
        XMLStreamReader reader = inputFactory.createXMLStreamReader(fileInputStream);
        long count = 0;
        while (reader.hasNext()) {
            //<DATA_RECORD>
            if (reader.getEventType() == XMLStreamConstants.START_ELEMENT && reader.getLocalName().equals("DATA_RECORD")) {
                count++;
            }
            reader.next();
        }
        reader.close();
        fileInputStream.close();
        return count;
    }




    public static void main(String[] args) throws Exception {
        String filePath = "/Users/gmartirosyan/buckups/t_check_result/18Apr23_1/t_check_result_202304181505.xml";
        long recordCount = countRecordElements(filePath);
        System.out.println("There are " + recordCount + " <Record> elements in the XML file.");

    }

}