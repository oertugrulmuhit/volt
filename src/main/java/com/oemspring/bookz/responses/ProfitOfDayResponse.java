package com.oemspring.bookz.responses;

import com.oemspring.bookz.models.ProfitOfDay;
import lombok.Data;

import java.util.Date;

@Data
public class ProfitOfDayResponse {


    private Long userId;
    private Long totalProfitfortheday;
    private Date date;
    private String message = "";

    public ProfitOfDayResponse(ProfitOfDay profitOfDay, String message) {
        this.userId = profitOfDay.getUser().getId();
        this.totalProfitfortheday = profitOfDay.getTotalProfitfortheday();
        this.date = profitOfDay.getDate();
        this.message = message;
    }

}
