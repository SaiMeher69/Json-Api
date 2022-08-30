package arigato.onichaan.meherbaka.controller;

import arigato.onichaan.meherbaka.model.Employee;
import arigato.onichaan.meherbaka.service.EmployeeService;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/json")
public class EmployeeController1 {

    private final EmployeeService employeeService;

    public EmployeeController1(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PutMapping("/add")
    public JSONObject addEmployee(@RequestBody Employee employee){
        JSONObject result = new JSONObject();
        try{
            result.put("index altered", employeeService.addJsonEmployee(employee));
        }catch (Exception e){
            result.put("error", e.getMessage());
        }
        return result;
    }
}
