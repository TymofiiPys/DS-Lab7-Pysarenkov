package org.example;

import java.util.List;

public interface AcDepDAO {
    void createTeacher(Teacher t);
    void createSubject(Subject s);
    List<Teacher> readTeachers(String query);
    List<Subject> readSubjects(String query);
    void updateTeachers(Teacher t);
    void updateSubjects(Subject s);
    void deleteTeacher(Teacher t);
    void deleteSubject(Subject s);
}
