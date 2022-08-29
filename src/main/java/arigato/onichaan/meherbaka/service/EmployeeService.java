package arigato.onichaan.meherbaka.service;

import arigato.onichaan.meherbaka.model.Employee;
import arigato.onichaan.meherbaka.repository.EmployeeRepository;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<Employee> findAllEmployees() throws SQLException, IOException, ClassNotFoundException, IllegalAccessException {
        return employeeRepository.findAll();
    }

    public List<Employee> findEmployeeByName(String name) throws SQLException, IOException, ClassNotFoundException, IllegalAccessException {
        return employeeRepository.findEmployeeByName(name);
    }

    public List<Integer> addEmployee(Employee employee) throws SQLException, IOException, ClassNotFoundException, IllegalAccessException, ParseException {
        List<Employee> employeesWithId = employeeRepository.findEmployeesById(employee.getId());
        List<Integer> indexes = new ArrayList<>();
        if(employee.getId() == 0){
            indexes = employeeRepository.addEmployee(employee);
        } else if (!employeesWithId.isEmpty()) {
            employeeRepository.deleteEmployee(employee.getId());
            indexes = employeeRepository.addEmployee(employee);
        }else{
            throw new SQLException("Enter a valid employee id");
        }
        return indexes;
    }

    public void deleteEmployee(int id) throws SQLException, IOException, ClassNotFoundException {
        employeeRepository.deleteEmployee(id);
    }

    public void deleteAllEmployees() throws SQLException, IOException, ClassNotFoundException {
        employeeRepository.deleteAll();
    }


}
