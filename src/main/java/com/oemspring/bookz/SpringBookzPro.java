package com.oemspring.bookz;

import com.oemspring.bookz.jobs.ProfitOfDayJob;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@SecurityScheme(name = "bookzapi", scheme = "bearer", type = SecuritySchemeType.HTTP, in = SecuritySchemeIn.HEADER)

public class SpringBookzPro {
    public static Logger logger = LoggerFactory.getLogger(SpringBookzPro.class);
    public static SchedulerFactory schedFact = new StdSchedulerFactory();


    public static void main(String[] args) {

        try {
            Scheduler sched = schedFact.getScheduler();

            logger.info("Günlük Kazanç Hesaplama çizelgelendi.");
            JobDetail profitOfDayJob = JobBuilder.newJob(ProfitOfDayJob.class).withIdentity("ProfitOfDayJob", "group1").build();
// 0 0 * * *
            CronTrigger profitOfDayTrigger = TriggerBuilder.newTrigger()
                    .withIdentity("profitOfDayTrigger", "group1")
                    .withSchedule(CronScheduleBuilder.cronSchedule("0 0 12 * * ?"))
                    .build();
            sched.scheduleJob(profitOfDayJob, profitOfDayTrigger);

            sched.start();

        } catch (SchedulerException e) {
            e.printStackTrace();
        }

        SpringApplication.run(SpringBookzPro.class, args);

    }

}


