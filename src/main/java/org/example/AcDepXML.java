package org.example;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AcDepXML implements AcDepDAO {

    private Document doc;
    private Element root;
    private String filename;

    private void setID(){
        NodeList teacher = root.getElementsByTagName("teacher");
        for (int i = 0; i < teacher.getLength(); i++) {
            Element node = (Element) teacher.item(i);
            node.setIdAttribute("id", true);
            NodeList childNodes = node.getElementsByTagName("subject");
            for (int j = 0; j < childNodes.getLength(); j++) {
                Element childNode = (Element) childNodes.item(i);
                childNode.setIdAttribute("id", true);
            }
        }
        NodeList subjects = root.getElementsByTagName("subject");
        for (int j = 0; j < subjects.getLength(); j++) {
            Element childNode = (Element) subjects.item(j);
            childNode.setIdAttribute("id", true);
        }
    }

    public AcDepXML(String filename) {
        this.filename = filename;
        //Зчитування документа у колекцію об'єктів
        DocumentBuilder db = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        doc = null;
        try {
            doc = db.parse(new File(filename));
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        root = doc.getDocumentElement();

        //Перевірка схеми
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        try {
            String xsdName = root.getAttribute("xsi:noNamespaceSchemaLocation");
            Source schemaFile = new StreamSource(new File(xsdName));
            Schema schema = factory.newSchema(schemaFile);
            schema.newValidator()
                    .validate(new StreamSource(new File(filename)));
            dbf.setSchema(schema);
        } catch (SAXException | IOException | NullPointerException e) {
            e.printStackTrace();
        }

        setID();
    }

    private void saveChanges() {
        doc.normalize();
        Source domSource = new DOMSource(doc);
        Result fileResult = new StreamResult(new File(filename));
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = factory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "WINDOWS-1251");
            transformer.transform(domSource, fileResult);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createTeacher(Teacher t) {
        Element teacher = doc.createElement("teacher");
        root.appendChild(teacher);
        teacher.setAttribute("id", "T" + t.code);
        teacher.setIdAttribute("id", true);
        teacher.setAttribute("name", t.name);
        saveChanges();
    }

    @Override
    public void createSubject(Subject s) {
        Element subject = doc.createElement("subject");
        subject.setAttribute("id", "S" + s.code);
        subject.setIdAttribute("id", true);
        subject.setAttribute("name", s.name);
        Element teacher = doc.getElementById("T" + s.teacher.code);
        if(teacher == null){
            root.appendChild(subject);
        } else {
            teacher.appendChild(subject);
        }
        saveChanges();
    }

    @Override
    public List<Teacher> readTeachers(String query) {
        List<Teacher> ret = new ArrayList<>();

        if (query == null) {
            NodeList listTeachers = root.getElementsByTagName("teacher");
            for (int i = 0; i < listTeachers.getLength(); i++) {
                Element teacher = (Element) listTeachers.item(i);

                int teacherCode = Integer.parseInt(teacher.getAttribute("id").substring(1));
                String teacherName = teacher.getAttribute("name");
                Teacher t = new Teacher(teacherCode, teacherName);
                ret.add(t);
            }
        } else if (query.contains("teacher.id")) {
            Element teacher = doc.getElementById("T" + query.substring("teacher.id = ".length()));
            if (teacher == null)
            {
                return ret;
            }
            int teacherCode = Integer.parseInt(teacher.getAttribute("id").substring(1));
            String teacherName = teacher.getAttribute("name");
            Teacher t = new Teacher(teacherCode, teacherName);
            ret.add(t);
        }
        return ret;
    }

    @Override
    public List<Subject> readSubjects(String query) {
        List<Subject> ret = new ArrayList<>();
        if (query == null) {
            NodeList subjects = root.getElementsByTagName("subject");
            for (int i = 0; i < subjects.getLength(); i++) {
                Element subject = (Element) subjects.item(i);
                int subjectCode = Integer.parseInt(subject.getAttribute("id").substring(1));
                String subjectName = subject.getAttribute("name");
                Element teacher = (Element) subject.getParentNode();
                Teacher t = null;
                if (teacher != null) {
                    int teacherCode = Integer.parseInt(teacher.getAttribute("id").substring(1));
                    String teacherName = teacher.getAttribute("name");
                    t = new Teacher(teacherCode, teacherName);
                }
                Subject s = new Subject(subjectCode, subjectName, t);
                ret.add(s);
            }
        } else {

        }
        return ret;
    }

    @Override
    public void updateTeachers(Teacher t) {
        Element teacher = doc.getElementById("T" + t.code);
        if(teacher == null){
            System.out.println("Вчителя з цим id не знайдено. Хочете додати - оберіть відповідну команду!");
            return;
        }
        teacher.setAttribute("name", t.name);
        saveChanges();
    }

    @Override
    public void updateSubjects(Subject s) {
        Element subject = doc.getElementById("S" + s.code);
        if(subject == null){
            System.out.println("Предмет із цим id не знайдено. Хочете додати - оберіть відповідну команду!");
            return;
        }
        subject.setAttribute("name", s.name);

        Element teacherOld = (Element) subject.getParentNode();

        if(teacherOld != null) {
            teacherOld.removeChild(subject);
            if(s.teacher == null){
                root.appendChild(subject);
            } else {
                Element teacherNew = doc.getElementById("T" + s.teacher.code);
                teacherNew.appendChild(subject);
            }
        } else {
            if(s.teacher != null) {
                Element teacherNew = doc.getElementById("T" + s.teacher.code);
                if(teacherNew == null){
                    System.out.println("Вчителя із цим ID не існує.");
                    return;
                }
                teacherNew.appendChild(subject);
            }
        }

        if(s.teacher != null) {

        } else {
            teacherOld = (Element) subject.getParentNode();
            if (teacherOld != null) {
                teacherOld.removeChild(subject);
            }
            root.appendChild(subject);
        }
        saveChanges();
    }

    @Override
    public void deleteTeacher(Teacher t) {
        Element teacher = doc.getElementById("T" + t.code);
        if(teacher == null){
            System.out.println("Учителя із цим id не знайдено.");
            return;
        }
        NodeList listSubjects = teacher.getElementsByTagName("subject");
        for (int i = 0; i < listSubjects.getLength(); i++) {
            Element subject = (Element) listSubjects.item(i);
            Element subjectNew = doc.createElement("subject");
            subjectNew.setAttribute("id", subject.getAttribute("id"));
            subjectNew.setIdAttribute("id", true);
            subjectNew.setAttribute("name", subject.getAttribute("name"));
            root.appendChild(subjectNew);
        }
        root.removeChild(teacher);
        saveChanges();
    }

    @Override
    public void deleteSubject(Subject s) {
        Element subject = doc.getElementById("S" + s.code);
        if(subject == null){
            System.out.println("Предмет із цим id не знайдено.");
            return;
        }
        Element teacherOld = (Element) subject.getParentNode();
        if (teacherOld != null) {
            teacherOld.removeChild(subject);
        }
        saveChanges();
    }

    public static void main(String[] args) {
        AcDepDAO acdep = new AcDepXML("acdep1.xml");
        acdep.deleteSubject(new Subject(4, "", null));
    }
}
