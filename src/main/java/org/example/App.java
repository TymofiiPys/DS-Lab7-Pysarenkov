package org.example;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class App {

    private void saveToXML(String filename, List<Teacher> tlist, List<Subject> slist) {
        throw new UnsupportedOperationException();
    }

    private void saveToDB(String filename, List<Teacher> tlist, List<Subject> slist) {
        throw new UnsupportedOperationException();
    }

    private int getIDT(List<Teacher> list) {
        int i = 1;
        list.sort(new Comparator<Teacher>() {
            @Override
            public int compare(Teacher t1, Teacher t2) {
                return Integer.compare(t1.code, t2.code);
            }
        });
        for (Teacher t : list) {
            if (t.code != i) {
                return i;
            }
            i++;
        }
        return i;
    }

    private int getIDS(List<Subject> list) {
        int i = 1;
        list.sort(new Comparator<Subject>() {
            @Override
            public int compare(Subject t1, Subject t2) {
                return Integer.compare(t1.code, t2.code);
            }
        });
        for (Subject t : list) {
            if (t.code != i) {
                return i;
            }
            i++;
        }
        return i;
    }

    private Teacher getTeacher(List<Teacher> list, String name) {
        for (Teacher t : list) {
            if (t.name.equals(name)) {
                return t;
            }
        }
        return null;
    }

    private Subject getSubject(List<Subject> list, String name) {
        for (Subject t : list) {
            if (t.name.equals(name)) {
                return t;
            }
        }
        return null;
    }

    private void chooseSaveOptionT(List<Teacher> list) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("1 - запис отриманого списку в XML");
        System.out.println("2 - запис отриманого списку в БД");
        System.out.println("натисніть enter - нічого не робити");
        String opr = scanner.nextLine();
        String filenameToWrite;
        switch (opr) {
            case "1":
                System.out.print("Введіть назву файлу: ");
                filenameToWrite = scanner.nextLine();
                saveToXML(filenameToWrite, list, null);
                break;
            case "2":
                System.out.print("Введіть назву файлу: ");
                filenameToWrite = scanner.nextLine();
                saveToDB(filenameToWrite, list, null);
                break;
            default:
                break;
        }
    }

    private void chooseSaveOptionS(List<Subject> list) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("1 - запис отриманого списку в XML");
        System.out.println("2 - запис отриманого списку в БД");
        System.out.println("натисніть enter - нічого не робити");
        String opr = scanner.nextLine();
        String filenameToWrite;
        switch (opr) {
            case "1":
                System.out.print("Введіть назву файлу: ");
                filenameToWrite = scanner.nextLine();
                saveToXML(filenameToWrite, null, list);
                break;
            case "2":
                System.out.print("Введіть назву файлу: ");
                filenameToWrite = scanner.nextLine();
                saveToDB(filenameToWrite, null, list);
                break;
            default:
                break;
        }
    }

    private void printListT(List<Teacher> list) {
        for (Teacher t : list) {
            System.out.println("ID: " + t.code);
            System.out.println("ПІБ: " + t.name);
        }
    }

    private void printListS(List<Subject> list) {
        for (Subject t : list) {
            System.out.println("ID: " + t.code);
            System.out.println("Назва: " + t.name);
            System.out.println("ПІБ викладача: " + t.teacher.name);
        }
    }

    public int menu() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введіть назву файлу для зчитування: ");
        String filename = scanner.nextLine();
        AcDepDAO acdep = null;
        if (filename.endsWith(".xml")) {
            acdep = new AcDepXML(filename);
        } else if (filename.endsWith(".db")) {
            try {
                acdep = new AcDepDB(filename);
            } catch (SQLException e) {
                System.out.println("Невдале зчитування файлу");
                return 1;
            }
        }
        while (true) {
            System.out.println("readT - вивести усіх учителів");
            System.out.println("readS - вивести усі предмети та вчителів, що викладають їх");
            System.out.println("insT - додати вчителя");
            System.out.println("insS - додати предмет");
            System.out.println("delT - видалити вчителя");
            System.out.println("delS - видалити предмет");
            System.out.println("updT - змінити параметри вчителя");
            System.out.println("updS - змінити параметри предмета");
            System.out.println("readT1 - вивести учителів із id в діапазоні");
            System.out.println("readT2 - вивести учителів, ПІБ яких містить рядок");
            System.out.println("readS1 - вивести предмети із id в діапазоні");
            System.out.println("readS1 - вивести предмети, що викладають вчителі із id в діапазоні");
            System.out.println("exit - вихід");
            String op = scanner.nextLine();
            List<Teacher> teacherList;
            List<Subject> subjectList;
            switch (op) {
                case "readT" -> {
                    teacherList = acdep.readTeachers(null);
                    printListT(teacherList);
                    chooseSaveOptionT(teacherList);
                }
                case "readS" -> {
                    subjectList = acdep.readSubjects(null);
                    printListS(subjectList);
                    chooseSaveOptionS(subjectList);
                }
                case "insT" -> {
                    System.out.print("ПІБ вчителя: ");
                    String name = scanner.nextLine();
                    int id = getIDT(acdep.readTeachers(null));
                    acdep.createTeacher(new Teacher(id, name));
                }
                case "insS" -> {
                    System.out.print("Назва предмету: ");
                    String name = scanner.nextLine();
                    System.out.print("ПІБ учителя (залиште поле пустим, якщо викладача на предмет немає): ");
                    String teacherName = scanner.nextLine();
                    int id = getIDS(acdep.readSubjects(null));
                    acdep.createSubject(new Subject(id, name, getTeacher(acdep.readTeachers(null), teacherName)));
                }
                case "delT" -> {
                    System.out.print("ПІБ учителя: ");
                    String name = scanner.nextLine();
                    Teacher del = getTeacher(acdep.readTeachers(null), name);
                    if (del == null) {
                        System.out.println("Учителя із даним ПІБ не знайдено");
                        continue;
                    }
                    acdep.deleteTeacher(del);
                }
                case "delS" -> {
                    System.out.print("Назва предмету: ");
                    String name = scanner.nextLine();
                    Subject del = getSubject(acdep.readSubjects(null), name);
                    if (del == null) {
                        System.out.println("Предмет із даною назвою не знайдено");
                        continue;
                    }
                    acdep.deleteSubject(del);
                }
                case "updT" -> {
                    System.out.print("ПІБ учителя: ");
                    String name = scanner.nextLine();
                    Teacher upd = getTeacher(acdep.readTeachers(null), name);
                    if (upd == null) {
                        System.out.println("Учителя із даним ПІБ не знайдено");
                        continue;
                    }
                    System.out.print("Нове ПІБ (пусте поле - без змін): ");
                    String newName = scanner.nextLine();
                    if (!newName.isBlank())
                        upd.name = newName;
                    acdep.updateTeachers(upd);
                }
                case "updS" -> {
                    System.out.print("Назва предмету: ");
                    String name = scanner.nextLine();
                    Subject upd = getSubject(acdep.readSubjects(null), name);
                    if (upd == null) {
                        System.out.println("Предмет із даною назвою не знайдено");
                        continue;
                    }
                    System.out.print("Нова назва (пусте поле - без змін): ");
                    String newName = scanner.nextLine();
                    if (!newName.isBlank())
                        upd.name = newName;
                    System.out.print("Новий вчитель (пусте поле - предмет не викладає жоден учитель): ");
                    String newTeacherName = scanner.nextLine();
                    Teacher t = null;
                    if (!newTeacherName.isBlank()) {
                        t = getTeacher(acdep.readTeachers(null), name);
                        if (t == null) {
                            System.out.println("Учителя із даним ПІБ не знайдено");
                            continue;
                        }
                    }
                    upd.teacher = t;
                    acdep.updateSubjects(upd);
                }
                case "readT1" -> {
                    int leftMargin;
                    int rightMargin;
                    try {
                        System.out.print("Введіть ліву межу ID");
                        leftMargin = Integer.parseInt(scanner.nextLine());
                        System.out.print("Введіть праву межу ID");
                        rightMargin = Integer.parseInt(scanner.nextLine());
                    } catch (NumberFormatException e) {
                        continue;
                    }
                    teacherList = acdep.readTeachers("SELECT * FROM Учителі WHERE ID >= " + leftMargin + "AND ID <= " + rightMargin);
                    printListT(teacherList);
                    chooseSaveOptionT(teacherList);
                }
                case "readT2" -> {
                    System.out.print("Введіть рядок: ");
                    String substr = scanner.nextLine();
                    teacherList = acdep.readTeachers("SELECT * FROM Учителі WHERE ПІБ LIKE '%" + substr + "%'");
                    printListT(teacherList);
                    chooseSaveOptionT(teacherList);
                }
                case "readS1" -> {
                    int leftMargin;
                    int rightMargin;
                    try {
                        System.out.print("Введіть ліву межу ID");
                        leftMargin = Integer.parseInt(scanner.nextLine());
                        System.out.print("Введіть праву межу ID");
                        rightMargin = Integer.parseInt(scanner.nextLine());
                    } catch (NumberFormatException e) {
                        continue;
                    }
                    subjectList = acdep.readSubjects("SELECT * FROM Предмети WHERE ID >= " + leftMargin + "AND ID <= " + rightMargin);
                    printListS(subjectList);
                    chooseSaveOptionS(subjectList);
                }
                case "readS2" -> {
                    int leftMargin;
                    int rightMargin;
                    try {
                        System.out.print("Введіть ліву межу ID");
                        leftMargin = Integer.parseInt(scanner.nextLine());
                        System.out.print("Введіть праву межу ID");
                        rightMargin = Integer.parseInt(scanner.nextLine());
                    } catch (NumberFormatException e) {
                        continue;
                    }
                    subjectList = acdep.readSubjects("SELECT * FROM Предмети WHERE Викладач >= " + leftMargin + "AND Викладач <= " + rightMargin);
                    printListS(subjectList);
                    chooseSaveOptionS(subjectList);
                }
                case "exit" -> {
                    return 0;
                }
            }
        }
    }

    public static void main(String[] args) {
        App a = new App();
        int ret = a.menu();
        System.exit(ret);
    }
}
