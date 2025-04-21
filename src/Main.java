/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nEMPLOYEE PAYROLL SYSTEM");
            System.out.println("1. Add Employee");
            System.out.println("2. View Employees");
            System.out.println("3. Search Employees by Department");
            System.out.println("4. Update Employee Details");
            System.out.println("5. Delete Employee");
            System.out.println("6. Exit");
            System.out.print("Enter choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); 

            switch (choice) {
                case 1:
                    System.out.print("Enter Name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter Department: ");
                    String department = scanner.nextLine();
                    System.out.print("Enter Salary: ");
                    double salary = scanner.nextDouble();
                    EmployeeManager.addEmployee(name, department, salary);
                    break;
                case 2:
                    List<Employee> employees = EmployeeManager.viewEmployees();
                    employees.forEach(System.out::println);
                    break;
                case 3:
                    System.out.print("Enter Department to Search: ");
                    String searchDept = scanner.nextLine();
                    Employee emp = EmployeeManager.searchByDepartment(searchDept);
                    System.out.println(emp != null ? emp : "No employees found.");
                    break;
                case 4:
                    System.out.print("Enter Employee ID to Update: ");
                    int updateId = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Enter New Department: ");
                    String newDept = scanner.nextLine();
                    System.out.print("Enter New Salary: ");
                    double newSalary = scanner.nextDouble();
                    EmployeeManager.updateEmployee(updateId, newDept, newSalary);
                    break;
                case 5:
                    System.out.print("Enter Employee ID to Delete: ");
                    int deleteId = scanner.nextInt();
                    EmployeeManager.deleteEmployee(deleteId);
                    break;
                case 6:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }
}
