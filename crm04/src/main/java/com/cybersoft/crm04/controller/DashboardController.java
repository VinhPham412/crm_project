package com.cybersoft.crm04.controller;

import com.cybersoft.crm04.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("")
public class DashboardController {

    @Autowired
    private TaskService taskService;

    @GetMapping({"/", "/dashboard"})
    public String showDashboard(Model model) {
        int amountOfUnimplementedTask = taskService.getAmountOfUnimplementedTask();
        model.addAttribute("amountOfUnimplementedTask", amountOfUnimplementedTask);
        int amountOfInProgressTask = taskService.getAmountOfInProgressTask();
        model.addAttribute("amountOfInProgressTask", amountOfInProgressTask);
        int amountOfCompletedTask = taskService.getAmountOfCompletedTask();
        model.addAttribute("amountOfCompletedTask", amountOfCompletedTask);

        return "index";
    }

}
