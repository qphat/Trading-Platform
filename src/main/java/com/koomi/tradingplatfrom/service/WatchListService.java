package com.koomi.tradingplatfrom.service;

import com.koomi.tradingplatfrom.model.entity.Coin;
import com.koomi.tradingplatfrom.model.entity.User;
import com.koomi.tradingplatfrom.model.entity.WatchList;

public interface WatchListService {

    WatchList fndUserWatchList(Long userId);

    WatchList createWatchList(User user);

    WatchList findById(Long id);

    Coin addCoinToWatchList( Coin coin , User user);

}
