package com.hr.reposetory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hr.entity.Employee;

@Repository
public interface EmployeeRepo extends JpaRepository<Employee, Integer>{

	public Employee findByIdAndPassword(int empId,String password);
	
	public Employee findByIdAndPasswordAndRole(int empId,String password,String role);
}
