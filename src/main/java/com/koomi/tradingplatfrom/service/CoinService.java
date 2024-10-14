package com.koomi.tradingplatfrom.service;

import com.koomi.tradingplatfrom.model.entity.Coin;

import java.util.List;

public interface CoinService {
    List<Coin> getCoinList(int page);

    String getMarketChart(String coinId, int days);

    String getCoinDetail(String coinId);

    Coin findCoinById(String id);

    String searchCoin(String keyword);

    String getTop50CoinByMarketCapRank();

    String getTrendingCoin();

}
