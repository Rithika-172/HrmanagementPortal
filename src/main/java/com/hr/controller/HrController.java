package com.hr.controller;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hr.entity.Compose;
import com.hr.entity.CreatePost;
import com.hr.entity.Employee;
import com.hr.reposetory.ComposeRepo;
import com.hr.reposetory.CreatePostRepo;
import com.hr.reposetory.EmployeeRepo;
import com.hr.service.HrService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HrController {

    @Autowired
    private HrService service;

    @Autowired
    private CreatePostRepo createPostRepo;

    @Autowired 
    private EmployeeRepo employeeRepo;

    @Autowired
    private ComposeRepo composeRepo;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "forgot-password";
    }

    @GetMapping("/home")
    public String home(@RequestParam("username") String username,
                       @RequestParam("password") String password,
                       Model model,
                       HttpSession session) {

        System.out.println("User Name and Password: " + username + " - " + password);

        try {
            if (username.length() < 4) {
                model.addAttribute("error", true);
                return "login"; // Not enough characters to extract empId
            }

            String empIdStr = username.substring(3);
            System.out.println("empId:- " + empIdStr);

            int id = Integer.parseInt(empIdStr);
            Employee employee = employeeRepo.findByIdAndPassword(id, password);

            if (employee != null) {
                model.addAttribute("error", false);
                session.setAttribute("userId", id);
                session.setAttribute("name", employee.getEmployeeName());
                session.setAttribute("desg", employee.getDesignation());

                if (employee.getRole().equalsIgnoreCase("USER")) {
                    System.out.println("Enter in user " + empIdStr);
                    return "redirect:/user-dashboard";
                } else if (employee.getRole().equalsIgnoreCase("ADMIN")) {
                    List<Compose> findAll = composeRepo.findAll();
                    findAll.forEach(k -> {
                        int parentId = k.getParentUkid();
                        String designation = employeeRepo.findById(parentId).get().getDesignation();
                        k.setPosition(designation);
                    });

                    model.addAttribute("statusList", findAll);
                    return "dash-board";
                } else {
                    return "redirect:/login";
                }

            } else {
                model.addAttribute("error", true);
                return "login";
            }

        } catch (Exception e) {
            System.err.println("Login Error: " + e.getMessage());
            model.addAttribute("error", true);
            return "login";
        }
    }

    @GetMapping("/dash-board")
    public String dashBard() {
        return "dash-board";
    }

    @GetMapping("/add-employee")
    public String addEmployee(Model model) {
        model.addAttribute("employee", new Employee());
        return "add-employee";
    }

    @GetMapping("/all-employee")
    public String allEmployee(Model model) {
        List<Employee> allEmployee = service.getAllEmployee();
        model.addAttribute("allEmployee", allEmployee);
        return "all-employee";
    }

    @GetMapping("/create-post")
    public String createPost(Model model) {
        List<CreatePost> findAll = createPostRepo.findAll();
        model.addAttribute("post", findAll);
        return "create-post";
    }

    @GetMapping("/status")
    public String status(Model model) {
        List<Compose> findAll = composeRepo.findAll();
        findAll.forEach(k -> {
            int id = k.getParentUkid();
            String designation = employeeRepo.findById(id).get().getDesignation();
            k.setPosition(designation);
        });
        model.addAttribute("statusList", findAll);
        return "status";
    }

    @GetMapping("/my-profile")
    public String myProfile(HttpSession session, Model model) {
        Object attribute = session.getAttribute("userId");
        int userId = Integer.parseInt(attribute.toString());
        Employee employee = employeeRepo.findById(userId).get();
        model.addAttribute("employee", employee);
        return "my-profile";
    }

    @GetMapping("/setting")
    public String setting() {
        return "setting";
    }

    @PostMapping("/save-employee")
    public String saveEmployee(@Valid @ModelAttribute Employee employee, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("employee", employee);
            return "add-employee";
        }
        employee.setPassword(employee.getDateOfBirth());
        service.addEmaployee(employee);
        return "redirect:/all-employee";
    }

    @PostMapping("/save-post")
    public String savePost(@ModelAttribute CreatePost createPost) {
        createPost.setAddedDate(new Date().toString());
        service.addPost(createPost);
        return "redirect:/create-post";
    }

    @PostMapping("/update-password")
    public String updatePassword(@RequestParam("password") String password,
                                 @RequestParam("newPassword1") String newPassword1,
                                 @RequestParam("newPassword2") String newPassword2,
                                 HttpSession session,
                                 Model model) {

        Object attribute = session.getAttribute("userId");
        int userId = Integer.parseInt(attribute.toString());

        Employee employee = employeeRepo.findByIdAndPassword(userId, password);

        if (employee != null && newPassword1.equals(newPassword2)) {
            employee.setPassword(newPassword2);
            employeeRepo.save(employee);
            model.addAttribute("error", false);
        } else {
            model.addAttribute("error", true);
            return "setting";
        }

        return "redirect:/login";
    }

    @GetMapping("/edit-record")
    public String editRecord(@RequestParam("id") int id, Model model) {
        Employee employee = employeeRepo.findById(id).get();
        model.addAttribute("employee", employee);
        return "edit-record";
    }

    @PostMapping("/edit-employee")
    public String updateRecord(@ModelAttribute Employee employee) {
        int id = employee.getId();
        Employee getEmp = employeeRepo.findById(id).get();
        if (getEmp != null) {
            employeeRepo.save(employee);
        }
        return "redirect:/all-employee";
    }

    @GetMapping("/deleteRecord-byId")
    public String deleteRecordById(@RequestParam("id") int id) {
        employeeRepo.deleteById(id);
        return "redirect:/all-employee";
    }

    @GetMapping("/user-dashboard")
    public String usertDashBord(Model model, HttpSession session) {
        Object attribute = session.getAttribute("userId");
        int userId = Integer.parseInt(attribute.toString());

        List<Compose> findAll = composeRepo.findByParentUkid(userId);
        findAll.forEach(k -> {
            int id = k.getParentUkid();
            String designation = employeeRepo.findById(id).get().getDesignation();
            k.setPosition(designation);
        });

        model.addAttribute("statusList", findAll);
        return "user-dash-board";
    }

    @GetMapping("/user-profile")
    public String userProfile(HttpSession session, Model model) {
        Object attribute = session.getAttribute("userId");
        int userId = Integer.parseInt(attribute.toString());
        Employee employee = employeeRepo.findById(userId).get();
        model.addAttribute("employee", employee);
        return "user-profile";
    }

    @GetMapping("/user-setting")
    public String userSetting() {
        return "user-setting";
    }

    @GetMapping("/user-compose")
    public String compose() {
        return "user-compose";
    }

    @PostMapping("/compose")
    public String addCompose(@RequestParam("subject") String subject,
                              @RequestParam("text") String text,
                              HttpSession session) {
        try {
            Object attribute = session.getAttribute("userId");
            int userId = Integer.parseInt(attribute.toString());
            Employee employee = employeeRepo.findById(userId).get();

            Compose com = new Compose();
            com.setEmpName(employee.getEmployeeName());
            com.setSubject(subject);
            com.setText(text);
            com.setParentUkid(userId);
            com.setAddedDate(new Date().toString());
            com.setStatus("PENDING");

            composeRepo.save(com);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return "redirect:/user-compose";
    }

    @GetMapping("/approve-byId")
    public String approve(@RequestParam("id") int id,
                          @RequestParam("type") String type) {
        Compose compose = composeRepo.findById(id).get();
        compose.setStatus(type);
        composeRepo.save(compose);
        return "redirect:/status";
    }

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("employee", new Employee());
        return "signup";
    }

    @PostMapping("/signup")
    public String signupSubmit(@ModelAttribute Employee employee, Model model) {
        if (employeeRepo.existsById(employee.getId())) {
            model.addAttribute("error", "User already exists with ID: " + employee.getId());
            return "signup";
        }

        employee.setPassword(employee.getDateOfBirth());

        if (employee.getRole() == null) {
            employee.setRole("USER");
        }

        employeeRepo.save(employee);
        model.addAttribute("message", "User registered successfully. Please login.");
        return "redirect:/login";
    }
}












