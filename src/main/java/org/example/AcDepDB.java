package org.example;

import org.sqlite.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AcDepDB implements AcDepDAO {
    private final Statement stmt;

    public AcDepDB(String DBName) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DBName);
        stmt = conn.createStatement();
    }

    @Override
    public void createTeacher(Teacher t) {
        String sql = "INSERT INTO Учителі (ID, ПІБ) " +
                "VALUES (" + t.code + ", '" + t.name + "')";
        try {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createSubject(Subject s) {
        String sql;
        if (s.teacher == null) {
            sql = "INSERT INTO Предмети (ID, Назва, Викладач) " +
                    "VALUES (" + s.code + ", '" + s.name + "', " + Types.NULL + ")";
        } else {
            sql = "INSERT INTO Предмети (ID, Назва, Викладач) " +
                    "VALUES (" + s.code + ", '" + s.name + "', " + s.teacher.code + ")";
        }

        try {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Teacher> readTeachers(String query) {
        String sql;
        if (query == null) {
            sql = "SELECT * FROM Учителі";
        } else {
            sql = query;
        }
        List<Teacher> ret = new ArrayList<>();
        try {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                int id = rs.getInt("ID");
                String name = rs.getString("ПІБ");
                ret.add(new Teacher(id, name));
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println("Помилка виконання зчитування");
            e.printStackTrace();
        }
        return ret;
    }

    @Override
    public List<Subject> readSubjects(String query) {
        String sql;
        if (query == null) {
            sql = "SELECT * FROM Предмети";
        } else {
            sql = query;
        }
        List<Subject> ret = new ArrayList<>();
        try {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                int id = rs.getInt("ID");
                String name = rs.getString("Назва");
                int idTeacher = rs.getInt("Викладач");
                ret.add(new Subject(id, name, new Teacher(idTeacher, "")));
            }
            rs.close();
            for (Subject s : ret) {
                List<Teacher> queryForId = readTeachers("SELECT * FROM Учителі WHERE ID = " + s.teacher.code);
                Teacher t = null;
                if (!queryForId.isEmpty()) {
                    t = queryForId.get(0);
                }
                s.teacher = t;
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println("Помилка виконання зчитування");
            e.printStackTrace();
        }
        return ret;
    }

    @Override
    public void updateTeachers(Teacher t) {
        String sql = "UPDATE Учителі SET (ID, ПІБ) = " +
                "(" + t.code + ", '" + t.name + "') WHERE ID = " + t.code;
        try {
            int c = stmt.executeUpdate(sql);
            if (c == 0) {
                System.out.println("Помилка при виконанні оновлення");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateSubjects(Subject s) {
        String sql = "UPDATE Предмети SET (ID, Назва, Викладач) = " +
                "(" + s.code + ", '" + s.name + "', " + s.teacher.code + ") WHERE ID = " + s.code;
        try {
            int c = stmt.executeUpdate(sql);
            if (c == 0) {
                System.out.println("Помилка при виконанні оновлення");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteTeacher(Teacher t) {
        String sql = "DELETE FROM Учителі WHERE ID =" + t.code;
        try {
            int c = stmt.executeUpdate(sql);
            if (c == 0) {
                System.out.println("Помилка видалення");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteSubject(Subject s) {
        String sql = "DELETE FROM Предмети WHERE ID =" + s.code;
        try {
            int c = stmt.executeUpdate(sql);
            if(c == 0){
                System.out.println("Помилка видалення");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        AcDepDAO acdep = null;
        try {
            acdep = new AcDepDB("bob");
        } catch (SQLException e) {
            System.out.println("Помилка з'єднання із БД");
            System.exit(1);
        }
    }
}
