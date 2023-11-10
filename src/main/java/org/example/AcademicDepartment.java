package org.example;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.util.ArrayList;
import javax.xml.XMLConstants;
import javax.xml.parsers.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.*;

/*
    Варіант 17

    Предметна область:      Кафедра університету

    Об'єкти:                Викладачі, Дисципліни

    Примітка:               На кафедрі існує множина викладачів.
                            Для кожного викладача задано множину дисциплін.

    Перевірка структури     XSD
    документа XML:
 */

public class AcademicDepartment {
    private ArrayList<Teacher> teachers = new ArrayList<>();
    private ArrayList<Subject> subjects = new ArrayList<>();

    public Subject getSubject(int code) {
        for (Subject s : subjects) {
            if (s.code == code)
                return s;
        }
        return null;
    }

    public Subject getSubjectInd(int index) {
        if (index >= 0 && index < subjects.size())
            return subjects.get(index);
        return null;
    }

    public int countSubjects() {
        return subjects.size();
    }

    public void saveToFile(String filename) {
        DocumentBuilder db = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        Document doc = db.newDocument();

        Element root = doc.createElement("acdep");
        doc.appendChild(root);

        for (Teacher t : teachers) {
            Element teacher = doc.createElement("teacher");
            root.appendChild(teacher);
            teacher.setAttribute("id", String.valueOf(t.code));
            teacher.setIdAttribute("id", true);

            teacher.setAttribute("name", t.name);
        }

        for (Subject s : subjects) {
            Element subject = doc.createElement("subject");
            subject.setAttribute("id", String.valueOf(s.code));
            subject.setAttribute("name", s.name);
            Element teacherNode = doc.getElementById(String.valueOf(s.teacher.code));
            teacherNode.appendChild(subject);
        }

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

    public void loadFromFile(String filename) {
        //Зчитування документа у колекцію об'єктів
        DocumentBuilder db = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        Document doc = null;
        try {
            doc = db.parse(new File(filename));
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }



        Element root = doc.getDocumentElement();

        //Перевірка схеми
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        try {
            String xsdName = root.getAttribute("xsi:noNamespaceSchemaLocation");
            Source schemaFile = new StreamSource(new File(xsdName));
            Schema schema = factory.newSchema(schemaFile);
            schema.newValidator()
                    .validate(new StreamSource(new File(filename)));
        } catch (SAXException | IOException | NullPointerException e) {
            e.printStackTrace();
            return;
        }

        if (root.getTagName().equals("acdep")) {
            NodeList listTeachers = root.getElementsByTagName("teacher");
            for (int i = 0; i < listTeachers.getLength(); i++) {
                Element teacher = (Element) listTeachers.item(i);
                int teacherCode = Integer.parseInt(teacher.getAttribute("id").substring(1));
                String teacherName = teacher.getAttribute("name");
                NodeList listSubjects = teacher.getElementsByTagName("subject");
                Teacher t = new Teacher(teacherCode, teacherName);
                for (int j = 0; j < listSubjects.getLength(); j++) {
                    Element subject = (Element) listSubjects.item(j);
                    int subjectCode = Integer.parseInt(subject.getAttribute("id").substring(1));
                    String subjectName = subject.getAttribute("name");
                    Subject s = new Subject(subjectCode, subjectName, t);
                    addSubject(s);
                }
                addTeacher(t);
            }
        }
    }

    public void addSubject(Subject subject) {
        for (Subject s : subjects) {
            if (s.code == subject.code) {
//                System.out.println("Предмет із цим кодом уже присутній у списку");
                return;
            }
        }
        subjects.add(subject);
    }

    public void deleteSubject(int code) {
        for (Subject s : subjects) {
            if (s.code == code) {
                subjects.remove(s);
            }
        }
        System.out.println("Предмет із цим кодом відсутній у списку");
    }

    public void addTeacher(Teacher teacher) {
        for (Teacher t : teachers) {
            if (t.code == teacher.code) {
//                System.out.println("Учитель із цим кодом уже присутній у списку");
                return;
            }
        }
        teachers.add(teacher);
    }

    public void fillTestData() {
//        subjects = new ArrayList<>();
//        addSubject(1, "Інформатика");
//        addSubject(2, "Геометрія");
//        addSubject(3, "Алгебра");
//        addSubject(4, "Українська мова");
//
//        teachers = new ArrayList<>();
//        var kit1 = new ArrayList<Subject>();
//        kit1.add(subjects.get(0));
//        kit1.add(subjects.get(2));
//        var kit2 = new ArrayList<Subject>();
//        kit2.add(subjects.get(1));
//        kit2.add(subjects.get(3));
//        var kit3 = new ArrayList<Subject>();
//        kit3.add(subjects.get(0));
//        kit3.add(subjects.get(3));

        teachers = new ArrayList<>();
        Teacher t1 = new Teacher(1, "Марія Іванівна");
        Teacher t2 = new Teacher(2, "Олег Михайлович");
        Teacher t3 = new Teacher(3, "Ніна Олегівна");
        addTeacher(t1);
        addTeacher(t2);
        addTeacher(t3);

        subjects = new ArrayList<>();
        Subject s1 = new Subject(1, "Інформатика", t1);
        Subject s2 = new Subject(2, "Геометрія", t1);
        Subject s3 = new Subject(3, "Алгебра", t2);
        Subject s4 = new Subject(4, "Українська мова", t2);
        Subject s5 = new Subject(5, "Фізкультура", t3);
        Subject s6 = new Subject(6, "Трудове навчання", t3);
        Subject s7 = new Subject(7, "Я і Україна", t3);

        addSubject(s1);
        addSubject(s2);
        addSubject(s3);
        addSubject(s4);
        addSubject(s5);
        addSubject(s6);
        addSubject(s7);
    }

    public static void main(String[] args) {
        AcademicDepartment acdep = new AcademicDepartment();
//        acdep.fillTestData();
        acdep.loadFromFile("acdep1.xml");
        acdep.saveToFile("acdep2.xml");
    }
}