package com.koomi.tradingplatfrom.service.imp;

import com.koomi.tradingplatfrom.exception.WatchListException;
import com.koomi.tradingplatfrom.model.entity.Coin;
import com.koomi.tradingplatfrom.model.entity.User;
import com.koomi.tradingplatfrom.model.entity.WatchList;
import com.koomi.tradingplatfrom.repository.WatchListRepository;
import com.koomi.tradingplatfrom.service.WatchListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WatchListServiceImp implements WatchListService {

    @Autowired
    private WatchListRepository watchListRepository;


    @Override
    public WatchList fndUserWatchList(Long userId) {
        WatchList watchList = watchListRepository.findByUserId(userId);
        if (watchList == null) {
            throw new WatchListException("WatchList not found");
        }
        return watchList;
    }

    @Override
    public WatchList createWatchList(User user) {
        WatchList watchList = new WatchList();
        watchList.setUser(user);

        return watchListRepository.save(watchList);
    }

    @Override
    public WatchList findById(Long id) {
        Optional<WatchList> watchList = watchListRepository.findById(id);
        if (watchList.isEmpty()) {
            throw new WatchListException("WatchList not found");
        }
        return watchList.get();
    }

    @Override
    public Coin addCoinToWatchList(Coin coin, User user) {
        WatchList watchList = fndUserWatchList(user.getId());

                if(watchList.getCoins().contains(coin)) {
                    watchList.getCoins().remove(coin);
                }
                watchList.getCoins().add(coin);

                watchListRepository.save(watchList);

        return coin;
    }
}
