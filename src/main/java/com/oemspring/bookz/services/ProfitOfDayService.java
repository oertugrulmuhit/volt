package com.oemspring.bookz.services;

import com.oemspring.bookz.models.ProfitOfDay;
import com.oemspring.bookz.models.User;
import com.oemspring.bookz.repos.ProfitOfDayRepository;
import com.oemspring.bookz.responses.ProfitOfDayResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfitOfDayService {


    ProfitOfDayRepository profitOfDayRepository;
    UserService userService;

    public ProfitOfDayService(ProfitOfDayRepository profitOfDayRepository, UserService userService) {
        this.profitOfDayRepository = profitOfDayRepository;
        this.userService = userService;
    }

    public void save(ProfitOfDay profitOfDay) {

        profitOfDayRepository.save(profitOfDay);
    }

    public List<ProfitOfDayResponse> UsersProfitsByName(String username) {
        User u = userService.findByUsername(username).get();
        return profitOfDayRepository.findByUser(u).stream().map(p -> new ProfitOfDayResponse(p, "OK.")).toList();
    }
}
