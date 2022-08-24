package arigato.onichaan.meherbaka.controller;

import arigato.onichaan.meherbaka.model.Employee;
import arigato.onichaan.meherbaka.service.EmployeeService;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/all")
    public List<Employee> listAllEmployees() throws SQLException, IOException, ClassNotFoundException, IllegalAccessException {
        return employeeService.findAllEmployees();
    }

    //add an employee by taking in json object as input
    @PostMapping(value = "/add")
    public int addEmployee(@RequestBody Employee employee) throws SQLException, IOException, ClassNotFoundException, IllegalAccessException, ParseException {
        return employeeService.createEmployee(employee);
    }


    //delete an employee from the database by id
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable int id) throws SQLException, IOException, ClassNotFoundException {
        employeeService.deleteEmployee(id);
        return new ResponseEntity<>("deleted", HttpStatus.OK);
    }


}
