package arigato.onichaan.meherbaka.service;

import arigato.onichaan.meherbaka.model.Employee;
import arigato.onichaan.meherbaka.repository.EmployeeRepository;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.SQLException;
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

    public int createEmployee(Employee employee) throws SQLException, IOException, ClassNotFoundException, IllegalAccessException, ParseException {
        return employeeRepository.addEmployee(employee);
    }

    public void deleteEmployee(int id) throws SQLException, IOException, ClassNotFoundException {
        employeeRepository.deleteEmployee(id);
    }


}
