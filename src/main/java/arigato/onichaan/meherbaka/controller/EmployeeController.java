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
    @PutMapping(value = "/add")
    public List<Integer> addEmployee(@RequestBody Employee employee) throws SQLException, IOException, ClassNotFoundException, IllegalAccessException, ParseException {
        //System.out.println(employee);
        return employeeService.addEmployee(employee);
    }

    @GetMapping(value = "/find/{name}")
    public List<Employee> findEmployeeByName(@PathVariable String name) throws SQLException, IOException, ClassNotFoundException, IllegalAccessException {
        return employeeService.findEmployeeByName(name);
    }

    //delete an employee from the database by id
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable int id) throws SQLException, IOException, ClassNotFoundException {
        employeeService.deleteEmployee(id);
        return new ResponseEntity<>("deleted", HttpStatus.OK);
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<String> deleteAllEmployeesFromDB() throws SQLException, IOException, ClassNotFoundException {
        employeeService.deleteAllEmployees();
        return new ResponseEntity<>("deleted all the employees in the table and restarted identity", HttpStatus.OK);
    }


}
