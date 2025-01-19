package com.hr.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.hr.entity.Employee;
import com.hr.reposetory.ComposeRepo;
import com.hr.reposetory.CreatePostRepo;
import com.hr.reposetory.EmployeeRepo;
import com.hr.service.HrService;

@WebMvcTest(HrController.class)
public class HrControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private HrService service;
	
	@MockBean
	private CreatePostRepo createPostRepo;
	
	@MockBean
	private EmployeeRepo employeeRepo;
	
	@MockBean
	private ComposeRepo composeRepo;
	
	@InjectMocks
	private HrController hrController;
	
	@BeforeEach
	private void setUp() {
		
		MockitoAnnotations.openMocks(this);
		this.mockMvc=MockMvcBuilders.standaloneSetup(hrController).build();
	}
	
	@Test
    public void testHomePage_ValidCredentials() throws Exception {
        Employee employee = new Employee();
        employee.setId(101);
        employee.setEmployeeName("John Doe");
        employee.setDesignation("Developer");
        employee.setRole("USER");

        when(employeeRepo.findByIdAndPassword(101, "password123")).thenReturn(employee);

        mockMvc.perform(get("/home")
            .param("username", "emp101")
            .param("password", "password123"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/user-dashboard"));
    }
	
	@Test
	public void testSavePost() throws Exception{
		
		mockMvc.perform(post("/save-post")
				.param("title", "New Title")
				.param("comment", "This is a comment")
				)
		.andExpect(status().is3xxRedirection())
		.andExpect(redirectedUrl("/create-post"));
	}
}







