package arigato.onichaan.meherbaka.repository;

import arigato.onichaan.meherbaka.annotations.AutoIncrement;
import arigato.onichaan.meherbaka.annotations.NameToName;
import arigato.onichaan.meherbaka.model.Employee;
import org.springframework.stereotype.Repository;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

@Repository
public class EmployeeRepository {
    List<Employee> employeeList = new ArrayList<>();

    public Connection createConnection() throws IOException, SQLException, ClassNotFoundException {
        Properties properties = new Properties();
        properties.load(new FileReader("src/main/resources/application.properties"));
        Class.forName("org.postgresql.Driver");
        return DriverManager.getConnection(properties.getProperty("spring.datasource.url"), properties.getProperty("spring.datasource.username"), properties.getProperty("spring.datasource.password"));
    }

    //Find all the employees in the database
    public List<Employee> findAll() throws SQLException, IOException, ClassNotFoundException, IllegalAccessException {
        String query;
        Statement selectStmt;
        Connection connection = createConnection();
        query = "Select * from employeetable";
        selectStmt = connection.createStatement();
        try {
            ResultSet rs = selectStmt.executeQuery(query);
            employeeList = new ArrayList<>();
            while (rs.next()) {
                Employee employee = new Employee();
                Class<?> clapp = Employee.class;
                for (Field field : clapp.getDeclaredFields()) {
                    field.setAccessible(true);
                    String searchValue;
                    if (field.isAnnotationPresent(NameToName.class)) {
                        searchValue = field.getAnnotation(NameToName.class).value();
                    } else {
                        searchValue = field.getName();
                    }
                    Class<?> type = field.getType();
                    if (type.equals(int.class)) {
                        field.set(employee, rs.getInt(searchValue));
                    } else if (type.equals(double.class)) {
                        field.set(employee, rs.getDouble(searchValue));
                    } else {
                        field.set(employee, rs.getString(searchValue));
                    }
                }
                employeeList.add(employee);
                //System.out.println(employee);
            }
            rs.close();
        } finally {
            connection.close();
            selectStmt.close();
        }
        //System.out.println(employeeList);
        return employeeList;
    }

    //find employeeById
    public int findEmployeesById(int id) throws SQLException, IOException, ClassNotFoundException {
        String query = "Select * FROM employeetable WHERE id = " + id + ";";
        Connection connection = createConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        int result = -1;
        if(resultSet.next()){
            result = resultSet.getInt("id");
        }
        statement.close();
        resultSet.close();
        connection.close();
        return result;
    }

    //find employee By name
    public List<Employee> findEmployeeByName(String fullName) throws SQLException, IOException, ClassNotFoundException, IllegalAccessException {
        List<Employee> employeesWithName = new ArrayList<>();
        String[] name = fullName.trim().split(" ");
        String query = "Select * From employeetable where first_name = '" + name[0] + "' AND last_name = '" + name[1] + "';";
        //System.out.println(query);
        Connection connection = createConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        if (!resultSet.wasNull()) {
            while (resultSet.next()) {
                Employee employee = new Employee();
                Class<?> clapp = Employee.class;
                for (Field field : clapp.getDeclaredFields()) {
                    field.setAccessible(true);
                    String searchValue;
                    if (field.isAnnotationPresent(NameToName.class)) {
                        searchValue = field.getAnnotation(NameToName.class).value();
                    } else {
                        searchValue = field.getName();
                    }
                    Class<?> type = field.getType();
                    if (type.equals(int.class)) {
                        field.set(employee, resultSet.getInt(searchValue));
                    } else if (type.equals(double.class)) {
                        field.set(employee, resultSet.getDouble(searchValue));
                    } else {
                        field.set(employee, resultSet.getString(searchValue));
                    }
                }
                employeesWithName.add(employee);
                //System.out.println(employee);
            }

        }
        connection.close();
        return employeesWithName;
    }

    //Add a new employee into the database and get back the employee added
    public int addEmployee(Employee employee) throws SQLException, IOException, ClassNotFoundException, IllegalAccessException {
        //add the employee using insert first
        //INSERT INTO EMPLOYEES (first_name, ...) values (hgdv,sfh,gab);
        PreparedStatement preparedStatement;
        Connection connection = createConnection();
        Class<?> clap = Employee.class;
        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO employeetable (");
        for (Field field : clap.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(AutoIncrement.class)) {
                if (employee.getId() == 0) {
                    continue;
                }
            }
            if (field.isAnnotationPresent(NameToName.class)) {
                query.append(field.getAnnotation(NameToName.class).value()).append(",");
            } else {
                query.append(field.getName()).append(",");
            }
        }
        query.deleteCharAt(query.length() - 1);
        query.append(") VALUES (");
        for (Field field : clap.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(AutoIncrement.class)) {
                if (employee.getId() == 0) {
                    continue;
                }
            }
            query.append("'").append(field.get(employee)).append("'").append(",");
        }
        query.deleteCharAt(query.length() - 1);
        query.append(");");
        //System.out.println(query);

        preparedStatement = connection.prepareStatement(query.toString());
        preparedStatement.executeUpdate();
        preparedStatement.close();
        //get the newly entered employee
        //select * from employeetable where first_name = employee.getFirstName();
        StringBuilder query1 = new StringBuilder().append("SELECT id FROM employeetable WHERE ");
        for (Field field : clap.getDeclaredFields()) {
            field.setAccessible(true);
            if (!field.getName().equals("id")) {
                if (field.isAnnotationPresent(NameToName.class)) {
                    query1.append(field.getAnnotation(NameToName.class).value());
                } else {
                    query1.append(field.getName());
                }
                query1.append(" = ");
                Class<?> type = field.getType();
                if (type.equals(int.class) || type.equals(double.class) || type.equals(float.class)) {
                    query1.append(field.get(employee)).append(" AND ");
                } else {
                    query1.append("'").append(field.get(employee)).append("' AND ");
                }
            }
        }
        query1.delete(query1.length() - 5, query1.length() - 1);
        query1.append(";");
        //System.out.println(query1);
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query1.toString());
        List<Integer> intList = new ArrayList<>();
        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            intList.add(id);
        }
        int result = Collections.max(intList);
        //closing connections and statements
        statement.close();
        resultSet.close();
        connection.close();
        return result;
    }

    public void updateEmployee(Employee employee) throws SQLException, IOException, ClassNotFoundException, IllegalAccessException {
        System.out.println(employee);
        Connection connection = createConnection();
        StringBuilder query = new StringBuilder().append("UPDATE employeetable SET ");
        Class<?> clap = Employee.class;
        for(Field field: clap.getDeclaredFields()){
            field.setAccessible(true);
            if(field.isAnnotationPresent(NameToName.class)){
                query.append(field.getAnnotation(NameToName.class).value()).append(" = ");
            }else{
                query.append(field.getName()).append(" = ");
            }
            Class<?> type = field.getType();
            if(type.equals(int.class) || type.equals(double.class) || type.equals(float.class)){
                query.append(field.get(employee)).append(", ");
            } else {
                query.append("'").append(field.get(employee)).append("', ");
            }
        }
        query.delete(query.length()-2, query.length());
        query.append("WHERE id = ").append(employee.getId());
        System.out.println(query);
        PreparedStatement preparedStatement = connection.prepareStatement(query.toString());
        preparedStatement.executeUpdate();
        preparedStatement.close();
        connection.close();
    }

    //delete employee
    public void deleteEmployee(int id) throws SQLException, IOException, ClassNotFoundException {
        //Delete from employeeTable where id = id
        StringBuilder query = new StringBuilder();
        PreparedStatement preparedStatement;
        try (Connection connection = createConnection()) {
            query.append("DELETE FROM employeeTable WHERE id=").append(id);
            preparedStatement = connection.prepareStatement(query.toString());
            preparedStatement.executeUpdate();
            preparedStatement.close();
        }
    }

    //truncate table and restart id
    public void deleteAll() throws SQLException, IOException, ClassNotFoundException {
        String query = "TRUNCATE employeetable RESTART IDENTITY";
        Connection connection = createConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.executeUpdate();
        preparedStatement.close();
        connection.close();
    }
}