package com.oemspring.bookz.responses;

import com.oemspring.bookz.models.ProfitOfDay;
import lombok.Data;

import java.util.Date;

@Data
public class ProfitOfDayResponse {


    private Long userId;
    private Long totalProfitfortheday;
    private Date date;


    public ProfitOfDayResponse(ProfitOfDay profitOfDay) {
        this.userId = profitOfDay.getUser().getId();
        this.totalProfitfortheday = profitOfDay.getTotalProfitfortheday();
        this.date = profitOfDay.getDate();

    }
}
