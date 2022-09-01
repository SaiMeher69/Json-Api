package arigato.onichaan.meherbaka.repository;

import arigato.onichaan.meherbaka.annotations.AutoIncrement;
import arigato.onichaan.meherbaka.annotations.NameToName;
import arigato.onichaan.meherbaka.model.Employee;
import com.opencsv.CSVWriter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

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
    public int findEmployeesById(int id, String table) throws SQLException, IOException, ClassNotFoundException {
        StringBuilder query = new StringBuilder().append("Select * FROM ").append(table).append("WHERE id = ").append(id);
        Connection connection = createConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query.toString());
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

    public int addJsonEmployee(String employee) throws SQLException, IOException, ClassNotFoundException {
        Connection connection = createConnection();
        StringBuilder query = new StringBuilder().append("Insert into employee (employee) values ('").append(employee).append("');");
        //System.out.println(query);
        PreparedStatement preparedStatement = connection.prepareStatement(query.toString());
        preparedStatement.executeUpdate();
        preparedStatement.close();

        StringBuilder query1 = new StringBuilder().append("Select * from employee where employee = '").append(employee).append("';");
        System.out.println(query1);
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query1.toString());
        List<Integer> intList = new ArrayList<>();
        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            intList.add(id);
        }
        int result = Collections.max(intList);
        resultSet.close();
        statement.close();
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

    //write into a CSV file
    public void makeCsv() throws SQLException, IOException, ClassNotFoundException {
        try (Connection connection = createConnection();Statement statement = connection.createStatement();CSVWriter writer = new CSVWriter(new FileWriter("src/main/resources/employeeData.csv"))) {
            Map<Integer, JSONObject> data = new HashMap<>();
            String query = "Select * from employee";
            ResultSet resultSet = statement.executeQuery(query);
            List<String> keys = new ArrayList<>();
            while(resultSet.next()){
                JSONParser parser = new JSONParser();
                JSONObject jsonObject = (JSONObject) parser.parse(resultSet.getString("employee"));
                data.put(resultSet.getInt("id"), jsonObject);
                for(Object key: jsonObject.keySet()){
                    if(!keys.contains((String) key)){
                        keys.add((String) key);
                    }
                }
            }
            System.out.println(keys);
            //writing the headers into the csv file
            String[] header = keys.toArray(new String[0]);
            writer.writeNext(header);
            data.forEach((k,v)->writer.writeNext(makeCsvEntry(v,keys)));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public String[] makeCsvEntry(JSONObject jsonObject, List<String> keys){
        List<String> values = new ArrayList<>();
        for(String key: keys){
            if(jsonObject.containsKey(key)){
                values.add(String.valueOf(jsonObject.get(key)));
            }else{
                values.add("null");
            }
        }
        return values.toArray(new String[0]);
    }
}