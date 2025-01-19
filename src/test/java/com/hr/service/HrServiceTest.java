package com.hr.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.theInstance;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.hr.entity.Employee;
import com.hr.reposetory.CreatePostRepo;
import com.hr.reposetory.EmployeeRepo;

public class HrServiceTest {

	@Mock
	private EmployeeRepo employeeRepo;
	
	@Mock
	private CreatePostRepo createPostRepo;
	
	@InjectMocks
	private HrService hrService;
	
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}
	
	@Test
	void testAddEmaployee() {
		
		Employee employee=new Employee();
		employee.setId(1);
		employee.setEmployeeName("Sonu Kumar");
		employee.setEmail("soft123@gmail.cpm");
		
		
		when(employeeRepo.save(employee)).thenReturn(employee);
		
		Employee saveEmaployee = hrService.addEmaployee(employee);
		
		verify(employeeRepo).save(employee);
		
		assertEquals(employee,saveEmaployee);
		
		
	}
	
	@Test
	void testGetAllEmployee() {
		
		List<Employee> employeeList=new ArrayList<>();
		
		Employee employee=new Employee();
		employee.setId(1);
		employee.setEmployeeName("Sonu Kumar");
		employee.setEmail("soft123@gmail.cpm");
		
		Employee employee2=new Employee();
		employee2.setId(2);
		employee2.setEmployeeName("Ramesh Kumar");
		employee2.setEmail("rama123@gmail.cpm");
		
		employeeList.add(employee);
		employeeList.add(employee2);
		
		when(employeeRepo.findAll()).thenReturn(employeeList);
		
		List<Employee> result=hrService.getAllEmployee();
		
		assertEquals(employeeList, result);
	}
}

























