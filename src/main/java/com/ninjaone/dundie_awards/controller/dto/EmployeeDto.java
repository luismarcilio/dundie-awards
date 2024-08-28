package com.ninjaone.dundie_awards.controller.dto;

import com.ninjaone.dundie_awards.model.Employee;
import com.ninjaone.dundie_awards.model.Organization;

public record EmployeeDto(
    long id,
    String firstName,
    String lastName,
    Integer dundieAwards,
    OrganizationDto organization

) {
    public EmployeeDto(Employee employee) {
        this(employee.getId(), employee.getFirstName(), employee.getLastName(), employee.getDundieAwards(), new OrganizationDto(employee.getOrganization()));
    }

    public record OrganizationDto(long id, String name) {
        public OrganizationDto(Organization organization) {
            this(organization.getId(), organization.getName());
        }
    }
}
