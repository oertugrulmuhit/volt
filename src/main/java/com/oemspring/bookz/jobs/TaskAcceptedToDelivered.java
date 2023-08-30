package com.oemspring.bookz.jobs;

import com.oemspring.bookz.SpringContext;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class TaskAcceptedToDelivered implements Job {


    public void execute(JobExecutionContext context) throws JobExecutionException {


        JobDataMap dataMap = context.getJobDetail().getJobDataMap();


        Long orderId = dataMap.getLong("orderId");
        String orderStatus = dataMap.getString("orderStatus");
        System.out.println("TaskAcceptedToDelivered");

        if (orderStatus.equals("DELIVERED"))
            SpringContext.getOrderService().updateOrderAcceptedToDeliveredBase(orderId);
    }
}