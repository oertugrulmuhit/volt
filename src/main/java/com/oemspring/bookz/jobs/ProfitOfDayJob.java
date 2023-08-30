package com.oemspring.bookz.jobs;

import com.oemspring.bookz.SpringContext;
import com.oemspring.bookz.models.Order;
import com.oemspring.bookz.models.ProfitOfDay;
import com.oemspring.bookz.models.User;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfitOfDayJob implements Job {


    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        System.out.println("ProfitOfDayJob");
        Map<User, Integer> hm = new HashMap();

        List<Order> latestdaysdelivered = SpringContext.getOrderService().latestdaysdelivered();
        //insert or update

        for (Order o : latestdaysdelivered) {

            if (hm.containsKey(o.getProduct().getOwner())) {
                hm.put(o.getProduct().getOwner(),
                        hm.get(
                                o.getProduct().getOwner())
                                + o.getQuantity());
            } else {
                hm.put(o.getProduct().getOwner(), o.getQuantity());
            }

        }

        for (User u : hm.keySet()) {

            ProfitOfDay profitOfDay = new ProfitOfDay();
            profitOfDay.setUser(u);
            profitOfDay.setTotalProfitfortheday(Long.valueOf(hm.get(u)));
            SpringContext.getProfitOfDayService().save(profitOfDay);
        }

    }

}