package com.ninjaone.dundie_awards.controller;

import com.ninjaone.dundie_awards.controller.dto.CreateEmployeeDto;
import com.ninjaone.dundie_awards.controller.dto.EmployeeDto;
import com.ninjaone.dundie_awards.controller.dto.UpdateEmployeeDto;
import com.ninjaone.dundie_awards.service.EmployeeService;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

//todo: add service layer
//todo: add dto to request only necessary fields
//todo: introduce domain related exceptions
//todo: add ControllerAdvice to handle exceptions
//todo: in delete endpoint substitute Map to a Record will be more readable, however a NO_CONTENT status code will be more appropriate

@Controller
@RequestMapping()
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // get all employees
    // todo: add dto to return only necessary fields
    @GetMapping("/employees")
    @ResponseBody
    public List<EmployeeDto> getAllEmployees() {
        return employeeService.findAll().stream().map(EmployeeDto::new).toList();
    }

    // create employee rest api
    // todo: add dto to request only necessary fields
    @PostMapping("/employees")
    @ResponseBody
    public EmployeeDto createEmployee(@RequestBody CreateEmployeeDto employee) {
        return new EmployeeDto(employeeService.save(employee));
    }

    // get employee by id rest api
    @GetMapping("/employees/{id}")
    @ResponseBody
    // todo: add dto to request only necessary fields
    public EmployeeDto getEmployeeById(@PathVariable Long id) {
        return new EmployeeDto(employeeService.findById(id));
    }

    // update employee rest api
    @PutMapping("/employees/{id}")
    @ResponseBody
    // todo: add dto to request only necessary fields
    public EmployeeDto updateEmployee(@PathVariable Long id, @RequestBody UpdateEmployeeDto employeeDetails) {
        return new EmployeeDto(employeeService.update(id, employeeDetails));
    }

    // delete employee rest api
    @DeleteMapping("/employees/{id}")
    @ResponseBody
    public DeleteStatus deleteEmployee(@PathVariable Long id) {
        employeeService.delete(id);
        return new DeleteStatus(true);
    }

    public record DeleteStatus(boolean deleted) {
    }
}