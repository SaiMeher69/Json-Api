package arigato.onichaan.meherbaka.service;

import arigato.onichaan.meherbaka.model.Employee;
import arigato.onichaan.meherbaka.repository.EmployeeRepository;
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

    public List<Employee> findEmployeeByName(String name) throws SQLException, IOException, ClassNotFoundException, IllegalAccessException {
        return employeeRepository.findEmployeeByName(name);
    }

    public int addEmployee(Employee employee) throws Exception {
        if(employee.getId() == 0){
            return employeeRepository.addEmployee(employee);
        } else if (employeeRepository.findEmployeesById(employee.getId()) != -1) {
            employeeRepository.updateEmployee(employee);
            return employee.getId();
        }else{
            throw new Exception("enter a valid employee id");
        }
    }

    public void deleteEmployee(int id) throws SQLException, IOException, ClassNotFoundException {
        employeeRepository.deleteEmployee(id);
    }

    public void deleteAllEmployees() throws SQLException, IOException, ClassNotFoundException {
        employeeRepository.deleteAll();
    }


}
