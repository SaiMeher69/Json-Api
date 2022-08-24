package arigato.onichaan.meherbaka.repository;

import arigato.onichaan.meherbaka.annotations.NameToName;
import arigato.onichaan.meherbaka.model.Employee;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Repository;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
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

    //parse the json string to object
    public Employee parseJson(String jsonString) throws IllegalAccessException, ParseException {
        Employee employee = new Employee();
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(jsonString);
        Class<?> clapp = Employee.class;
        for (Field field : clapp.getDeclaredFields()) {
            field.setAccessible(true);
            Class<?> type = field.getType();
            String fieldName;
            if (field.isAnnotationPresent(NameToName.class)) {
                fieldName = field.getAnnotation(NameToName.class).value();
            } else {
                fieldName = field.getName();
            }
            if (type.equals(int.class)) {
                field.set(employee, (int) (long) jsonObject.get(fieldName));
            } else if (type.equals(Double.class)) {
                field.set(employee, (double) jsonObject.get(fieldName));
            } else {
                field.set(employee, jsonObject.get(fieldName));
            }
        }
        return employee;
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

    //Add a new employee into the database and get back the employee added
    public int addEmployee(Employee employee) throws SQLException, IOException, ClassNotFoundException, IllegalAccessException, ParseException {
        //add the employee using insert first
        PreparedStatement preparedStatement;
        Connection connection = createConnection();
        Class<?> clap = Employee.class;
        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO employeetable (");
        for (Field field : clap.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.getName().equals("id")) {
                continue;
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
            if (field.getName().equals("id")) {
                continue;
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
        StringBuilder query1 = new StringBuilder().append("SELECT * FROM employeetable WHERE ");
        for(Field field: clap.getDeclaredFields()){
            field.setAccessible(true);
            if(field.isAnnotationPresent(NameToName.class)){
                query1.append(field.getAnnotation(NameToName.class).value()).append(" = ").append("'").append(field.get(employee)).append("'").append(" AND ");
            }
        }
        query1.delete(query1.length()-5,query1.length()-1);
        query1.append(";");
        //System.out.println(query1);
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query1.toString());
        resultSet.next();

        int id = resultSet.getInt("id");
        //closing connections and statements
        statement.close();
        resultSet.close();
        connection.close();
        return id;
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
}