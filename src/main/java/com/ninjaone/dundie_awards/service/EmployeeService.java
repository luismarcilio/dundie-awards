package com.ninjaone.dundie_awards.service;

import com.ninjaone.dundie_awards.AwardsCache;
import com.ninjaone.dundie_awards.MessageBroker;
import com.ninjaone.dundie_awards.controller.dto.CreateEmployeeDto;
import com.ninjaone.dundie_awards.controller.dto.UpdateEmployeeDto;
import com.ninjaone.dundie_awards.exception.InvalidArgumentsException;
import com.ninjaone.dundie_awards.exception.NoDataFoundException;
import com.ninjaone.dundie_awards.model.Activity;
import com.ninjaone.dundie_awards.model.Employee;
import com.ninjaone.dundie_awards.model.Organization;
import com.ninjaone.dundie_awards.repository.ActivityRepository;
import com.ninjaone.dundie_awards.repository.EmployeeRepository;
import com.ninjaone.dundie_awards.repository.OrganizationRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final ActivityRepository activityRepository;
    private final MessageBroker messageBroker;
    private final AwardsCache awardsCache;
    private final OrganizationRepository organizationRepository;

    public EmployeeService(
        EmployeeRepository employeeRepository,
        ActivityRepository activityRepository,
        MessageBroker messageBroker,
        AwardsCache awardsCache,
        OrganizationRepository organizationRepository
    ) {
        this.employeeRepository = employeeRepository;
        this.activityRepository = activityRepository;
        this.messageBroker = messageBroker;
        this.awardsCache = awardsCache;
        this.organizationRepository = organizationRepository;
    }

    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    public Employee save(CreateEmployeeDto employee) {
        Organization organization = organizationRepository.findById(employee.organizationId()).orElseThrow(
            () -> new InvalidArgumentsException("Organization with id " + employee.organizationId() + " does not exist")
        );
        Employee newEmployee = new Employee(employee.firstName(), employee.lastName(), organization);
        return employeeRepository.save(newEmployee);
    }

    public Employee findById(long id) {
        return employeeRepository.findById(id).orElseThrow(() -> new NoDataFoundException("Employee with id " + id + " does not exist"));
    }

    public Employee update(long id, UpdateEmployeeDto employeeDetails) {
        Employee currentEmployee = findById(id);
        currentEmployee.setFirstName(employeeDetails.firstName());
        currentEmployee.setLastName(employeeDetails.lastName());
        return employeeRepository.save(currentEmployee);
    }

    @Transactional
    public void addAward(long organizationId, LocalDateTime occurredAt) {
        Organization organization = organizationRepository
                                        .findById(organizationId)
                                        .orElseThrow(() -> new NoDataFoundException("Organization with id " + organizationId + " does not exist"));
        List<Employee> employeeList = findAllByOrganization(organization)
                                          .stream().map(this::incrementAward)
                                          .toList();
        employeeRepository.saveAll(employeeList);
        awardsCache.addOneAward();
        Activity activityLog = new Activity(occurredAt, activityMessage(organization));
        activityRepository.save(activityLog);
        messageBroker.addMessage(activityLog);
    }

    private String activityMessage(Organization organization) {
        return String.format("New award given to company %s", organization.getName());
    }

    private Employee incrementAward(Employee employee) {
        int currentAward = employee.getDundieAwards() == null ? 0 : 1;
        employee.setDundieAwards(currentAward + 1);
        return employee;
    }

    public void delete(Long id) {
        Employee currentEmployee = findById(id);
        employeeRepository.delete(currentEmployee);
    }

    public List<Employee> findAllByOrganization(Organization organization) {
        return employeeRepository.findAllByOrganization(organization);
    }
}
