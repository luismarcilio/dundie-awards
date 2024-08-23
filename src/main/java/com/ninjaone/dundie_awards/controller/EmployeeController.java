package com.ninjaone.dundie_awards.controller;

import com.ninjaone.dundie_awards.controller.dto.CreateEmployeeDto;
import com.ninjaone.dundie_awards.controller.dto.EmployeeDto;
import com.ninjaone.dundie_awards.controller.dto.UpdateEmployeeDto;
import com.ninjaone.dundie_awards.service.EmployeeService;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping

    public List<EmployeeDto> getAllEmployees() {
        return employeeService.findAll().stream().map(EmployeeDto::new).toList();
    }

    @PostMapping

    public EmployeeDto createEmployee(@RequestBody CreateEmployeeDto employee) {
        return new EmployeeDto(employeeService.save(employee));
    }

    @GetMapping("/{id}")

    public EmployeeDto getEmployeeById(@PathVariable Long id) {
        return new EmployeeDto(employeeService.findById(id));
    }

    @PutMapping("/{id}")

    public EmployeeDto updateEmployee(@PathVariable Long id, @RequestBody UpdateEmployeeDto employeeDetails) {
        return new EmployeeDto(employeeService.update(id, employeeDetails));
    }

    // delete employee rest api
    @DeleteMapping("/{id}")

    public DeleteStatus deleteEmployee(@PathVariable Long id) {
        employeeService.delete(id);
        return new DeleteStatus(true);
    }

    public record DeleteStatus(boolean deleted) {
    }
}