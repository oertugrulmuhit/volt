package com.oemspring.bookz.jobs;

import com.oemspring.bookz.SpringBookzPro;
import com.oemspring.bookz.models.Order;
import org.apache.commons.lang3.time.DateUtils;
import org.quartz.*;

import java.util.Date;
import java.util.Random;
import java.util.UUID;

public class JobCreatorAcceptedtoDelivered {
    public static void jobCreator(Order order) throws SchedulerException {


        String jobName = "accepted->" + order.getId() + "delivered->" + UUID.randomUUID();
        String randomlyAcceptedorDelivered = new Random().nextBoolean() ? "DELIVERED" : "ACCEPTED";
        int randomly2to15 = new Random().nextInt(2, 15);
        SpringBookzPro.logger.info(randomlyAcceptedorDelivered + randomly2to15);
        JobDetail jobAcceptedToDelivered2to15 = JobBuilder.newJob(TaskAcceptedToDelivered.class).withIdentity(jobName + "job2to15", "group1").usingJobData("orderStatus", randomlyAcceptedorDelivered).usingJobData("orderId", order.getId()).build();
        JobDetail jobAcceptedToDelivered15 = JobBuilder.newJob(TaskAcceptedToDelivered.class).withIdentity(jobName + "job15", "group1").usingJobData("orderStatus", "DELIVERED").usingJobData("orderId", order.getId()).build();

//            Trigger trigger = TriggerBuilder.newTrigger().withIdentity(jobName+"trigger", "group1").startNow().withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(40).repeatForever()).build();
        Trigger trigger2to15seconds = TriggerBuilder.newTrigger().withIdentity(jobName + "trigger2to15", "group1").startAt(DateUtils.addSeconds(new Date(), randomly2to15))
                .build();
        Trigger trigger15seconds = TriggerBuilder.newTrigger().withIdentity(jobName + "trigger15", "group1").startAt(DateUtils.addSeconds(new Date(), 15))
                .build();
        SpringBookzPro.schedFact.getScheduler().scheduleJob(jobAcceptedToDelivered2to15, trigger2to15seconds);
        SpringBookzPro.schedFact.getScheduler().scheduleJob(jobAcceptedToDelivered15, trigger15seconds);

    }

}
