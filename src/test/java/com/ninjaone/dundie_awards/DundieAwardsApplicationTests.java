package com.ninjaone.dundie_awards;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ninjaone.dundie_awards.controller.dto.CreateEmployeeDto;
import com.ninjaone.dundie_awards.controller.dto.UpdateEmployeeDto;
import com.ninjaone.dundie_awards.model.Employee;
import com.ninjaone.dundie_awards.model.Organization;
import com.ninjaone.dundie_awards.repository.EmployeeRepository;
import com.ninjaone.dundie_awards.repository.OrganizationRepository;
import com.ninjaone.dundie_awards.service.EmployeeService;
import com.ninjaone.dundie_awards.utils.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DundieAwardsApplicationTests extends BaseIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetEmployees() throws Exception {
        mvc.perform(get("/employees"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$[0].firstName", is("John")))
            .andExpect(jsonPath("$[1].firstName", is("Jane")))
            .andExpect(jsonPath("$[2].firstName", is("Creed")));
    }

    @Test
    void testPostEmployees() throws Exception {

        CreateEmployeeDto newEmployee = getCreateEmployee("New", "Employee");

        String newEmployeeAsJson = objectMapper.writeValueAsString(newEmployee);

        mvc.perform(post("/employees").content(newEmployeeAsJson).contentType("application/json"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.firstName", is("New")))
            .andExpect(jsonPath("$.lastName", is("Employee")))
            .andExpect(jsonPath("$.id", isA(Integer.class)));
    }

    private CreateEmployeeDto getCreateEmployee(String firstName, String lastName) {
        Organization organization = organizationRepository.findAll().get(0);
        return new CreateEmployeeDto(firstName, lastName, organization.getId());
    }

    @Test
    void testGetOneEmployee() throws Exception {
        Employee employee = employeeRepository.findAll().get(0);

        mvc.perform(get("/employees/{id}", employee.getId()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.firstName", is(employee.getFirstName())))
            .andExpect(jsonPath("$.lastName", is(employee.getLastName())))
            .andExpect(jsonPath("$.id", is(equalTo((int) employee.getId()))));
    }

    @Test
    void testUpdateEmployee() throws Exception {
        CreateEmployeeDto newEmployee = getCreateEmployee("Another Employee", "To be Updated");
        Employee employee = employeeService.save(newEmployee);
        UpdateEmployeeDto updatedData = new UpdateEmployeeDto("Updated", "Employee");

        String updatedDataAsJson = objectMapper.writeValueAsString(updatedData);

        mvc.perform(put("/employees/{id}", employee.getId())
                        .content(updatedDataAsJson)
                        .contentType("application/json")
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.firstName", is(updatedData.firstName())))
            .andExpect(jsonPath("$.lastName", is(updatedData.lastName())))
            .andExpect(jsonPath("$.id", is(equalTo((int) employee.getId()))));
    }

    @Test
    void testDeleteEmployee() throws Exception {
        CreateEmployeeDto newEmployee = getCreateEmployee("Another Employee", "To be Updated");
        Employee employee = employeeService.save(newEmployee);
        mvc.perform(delete("/employees/{id}", employee.getId()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.deleted", is(equalTo(true))));
    }

}
