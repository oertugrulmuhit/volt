package com.oemspring.bookz.repos;

import com.oemspring.bookz.models.ProfitOfDay;
import com.oemspring.bookz.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfitOfDayRepository extends JpaRepository<ProfitOfDay, Long> {
    List<ProfitOfDay> findByUser(User user);

}
