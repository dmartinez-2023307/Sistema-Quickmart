package org.kinalquickmart.system.service;

import org.kinalquickmart.system.dao.EmployeeDAO;
import org.kinalquickmart.system.model.Employee;
import org.kinalquickmart.system.utils.PasswordUtil;
import java.util.List;

public class EmployeeService {
    
    private EmployeeDAO employeeDAO;

    public EmployeeService() {
        this.employeeDAO = new EmployeeDAO();
    }

    public List<Employee> getAllEmployees() {
        return employeeDAO.getAllEmployees();
    }

    public List<Employee> searchEmployees(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return getAllEmployees();
        }
        return employeeDAO.searchEmployees(texto);
    }

    public boolean registerEmployee(Employee employee) {
        if (employeeDAO.emailExists(employee.getEmail())) {
            return false; 
        }
        return employeeDAO.saveEmployee(employee);
    }

    public boolean updateEmployee(Employee employee) {
        return employeeDAO.updateEmployee(employee);
    }

    public boolean updateEmployeeStatus(Employee employee) {
        return employeeDAO.updateEmployeeStatus(employee);
    }

    public boolean deleteEmployee(int id) {
        return employeeDAO.deleteEmployee(id);
    }

    public Employee login(String email, String password) {
        Employee emp = employeeDAO.getEmployeeByEmail(email);
        if (emp != null && PasswordUtil.verificar(password, emp.getPassword()) && emp.isActive()) {
            return emp; 
        }
        return null; 
    }
}