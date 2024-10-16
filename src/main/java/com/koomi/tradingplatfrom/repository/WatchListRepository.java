package com.koomi.tradingplatfrom.repository;

import com.koomi.tradingplatfrom.model.entity.WatchList;
import org.springframework.data.jpa.repository.JpaRepository;


public interface WatchListRepository extends JpaRepository<WatchList, Long> {

    WatchList findByUserId(Long userId);

}
