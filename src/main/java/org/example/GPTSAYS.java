package org.example;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

public class GPTSAYS {
    public class XMLUtils {
        public static void writeDocumentToFile(Document document, String fileName) {
            try {
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(document);

                File file = new File(fileName);
                OutputStream outputStream = new FileOutputStream(file);

                StreamResult result = new StreamResult(outputStream);

                // Output to console for testing
                // StreamResult result = new StreamResult(System.out);

                transformer.transform(source, result);

                outputStream.close();

            } catch (TransformerException | IOException e) {
                e.printStackTrace();
            }
        }
    }
    static class MyObject{
        public String name;
        public List<String> innerList;
        public MyObject(String name, List<String> innerList){
            this.name = name;
            this.innerList = innerList;
        }
    }

    public void doShit(){

    }

    public static void main(String[] args) {
        // Create a list of objects (each object contains an ArrayList)
        List<MyObject> myObjectList = new ArrayList<>();
        myObjectList.add(new MyObject("Object1", createInnerList(1, 3)));
        myObjectList.add(new MyObject("Object2", createInnerList(4, 6)));

        // Write the list to an XML file
        writeArrayListToXML(myObjectList, "output.xml");
    }

    private static List<String> createInnerList(int start, int end) {
        List<String> innerList = new ArrayList<>();
        for (int i = start; i <= end; i++) {
            innerList.add("Element" + i);
        }
        return innerList;
    }

    private static void writeArrayListToXML(List<MyObject> myObjectList, String fileName) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // Create a new document
            Document doc = docBuilder.newDocument();

            // Create the root element
            Element rootElement = doc.createElement("MyObjects");
            doc.appendChild(rootElement);

            // Loop through each object in the list
            for (MyObject myObject : myObjectList) {
                Element objectElement = doc.createElement("Object");
                rootElement.appendChild(objectElement);

                // Set attribute for the object
                objectElement.setAttribute("name", myObject.name);

                // Create inner list element
                Element innerListElement = doc.createElement("InnerList");
                objectElement.appendChild(innerListElement);

                // Loop through elements in the inner list
                for (String element : myObject.innerList) {
                    Element elementElement = doc.createElement("Element");
                    elementElement.appendChild(doc.createTextNode(element));
                    innerListElement.appendChild(elementElement);
                }
            }

            // Write the content into an XML file
            XMLUtils.writeDocumentToFile(doc, fileName);

            System.out.println("XML file written successfully!");

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }
}

