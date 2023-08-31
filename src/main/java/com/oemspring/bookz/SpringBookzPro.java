package com.oemspring.bookz;

import com.oemspring.bookz.jobs.ProfitOfDayJob;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static com.oemspring.bookz.jobs.JobCreator.jobCreatorProfitOfDay;

@SpringBootApplication
@SecurityScheme(name = "bookzapi", scheme = "bearer", type = SecuritySchemeType.HTTP, in = SecuritySchemeIn.HEADER)

public class SpringBookzPro {
    public static Logger logger = LoggerFactory.getLogger(SpringBookzPro.class);
    public static SchedulerFactory schedFact = new StdSchedulerFactory();


    public static void main(String[] args) {

        jobCreatorProfitOfDay();

        SpringApplication.run(SpringBookzPro.class, args);

    }

}


