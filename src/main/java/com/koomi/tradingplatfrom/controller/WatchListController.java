package com.koomi.tradingplatfrom.controller;

import com.koomi.tradingplatfrom.model.entity.Coin;
import com.koomi.tradingplatfrom.model.entity.User;
import com.koomi.tradingplatfrom.model.entity.WatchList;
import com.koomi.tradingplatfrom.service.CoinService;
import com.koomi.tradingplatfrom.service.UserService;
import com.koomi.tradingplatfrom.service.WatchListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/watchlist")
public class WatchListController {

    @Autowired
    private WatchListService watchListService;

    @Autowired
    UserService userService;

    @Autowired
    CoinService coinService;

    @GetMapping("/user")
    public ResponseEntity<WatchList> getUserWatchList(
            @RequestHeader("Authorization") String jwt){

        User user = userService.findUserProfileByJwt(jwt);
        WatchList watchList = watchListService.fndUserWatchList(user.getId());

        return new ResponseEntity<>(watchList, HttpStatus.OK);
    }

    @PostMapping("/user/create")
    public ResponseEntity<WatchList> createWatchList(
            @RequestHeader("Authorization") String jwt){

        User user = userService.findUserProfileByJwt(jwt);
        WatchList watchList = watchListService.createWatchList(user);

        return new ResponseEntity<>(watchList, HttpStatus.CREATED);
    }

    @GetMapping("/{watchListId}")
    public ResponseEntity<WatchList> getWatchList(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long watchListId){

        User user = userService.findUserProfileByJwt(jwt);
        WatchList watchList = watchListService.findById(watchListId);

        return new ResponseEntity<>(watchList, HttpStatus.OK);
    }

    @PatchMapping("/add/coin/{coinId}")
    public ResponseEntity<Coin> addCoinToWatchList(
            @RequestHeader("Authorization") String jwt,
            @PathVariable String coinId){

        User user = userService.findUserProfileByJwt(jwt);
        Coin coin = coinService.findCoinById(coinId);
        Coin addCoin = watchListService.addCoinToWatchList(coin, user);

        return new ResponseEntity<>(coin, HttpStatus.OK);
    }







}
