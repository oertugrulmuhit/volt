package com.oemspring.bookz.controllers;

import com.oemspring.bookz.responses.ProfitOfDayResponse;
import com.oemspring.bookz.services.ProfitOfDayService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController

@RequestMapping("/api/profitofday")
@SecurityRequirement(name = "bookzapi")

public class ProfitOfDayController {
    ProfitOfDayService profitOfDayService;

    public ProfitOfDayController(ProfitOfDayService profitOfDayService) {
        this.profitOfDayService = profitOfDayService;
    }

    @GetMapping("/{username}")
    public List<ProfitOfDayResponse> usersprofits(@PathVariable String username) {
        System.out.printf("get" + username + "'s profits ");
        try {
            return profitOfDayService.UsersProfitsByName(username);
        } catch (Exception e) {
            return null;
        }
        // return  orderService.getMyOrders(principal);
    }


}
