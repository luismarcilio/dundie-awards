package com.ninjaone.dundie_awards.controller.dto;

import com.ninjaone.dundie_awards.model.Employee;

public record EmployeeDto(
    long id,
    String firstName,
    String lastName,
    Integer dundieAwards

) {
    public EmployeeDto(Employee employee) {
        this(employee.getId(), employee.getFirstName(), employee.getLastName(), employee.getDundieAwards());
    }
}
