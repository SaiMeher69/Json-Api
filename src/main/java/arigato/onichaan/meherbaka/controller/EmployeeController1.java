package arigato.onichaan.meherbaka.controller;

import arigato.onichaan.meherbaka.service.EmployeeService;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.SQLException;

@RestController
@RequestMapping("/json")
public class EmployeeController1 {

    private final EmployeeService employeeService;

    public EmployeeController1(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PutMapping(value = "/add", consumes = "text/plain")
    public JSONObject addEmployee(@RequestBody String employee){
        JSONObject result = new JSONObject();
        try{
            result.put("index altered", employeeService.addJsonEmployee(employee));
        }catch (Exception e){
            result.put("error", e.getMessage());
        }
        return result;
    }

    @GetMapping("/csv")
    public void makeCSV() throws SQLException, IOException, ClassNotFoundException {
        employeeService.makeCSV();
    }
}
