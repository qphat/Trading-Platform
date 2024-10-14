package com.koomi.tradingplatfrom.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.koomi.tradingplatfrom.model.entity.Coin;
import com.koomi.tradingplatfrom.service.CoinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("coin")
public class CoinController {

    @Autowired
    private CoinService coinService;

    @Autowired
    private ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("list")
    ResponseEntity<List<Coin>> getCoinList(@RequestParam("page") int page) {
        List<Coin> coinList = coinService.getCoinList(page);
        return new ResponseEntity<>(coinList, HttpStatus.OK);
    }

    @GetMapping("/{coinId}/chart")
    ResponseEntity<JsonNode> getMarketChart(
            @PathVariable("coinId") String coinId,
            @RequestParam("days") int days) throws JsonProcessingException {


        String res = coinService.getMarketChart(coinId, days);
        JsonNode jsonNode = objectMapper.readTree(res);

        return new ResponseEntity<>(jsonNode, HttpStatus.ACCEPTED);
    }

    @GetMapping("search")
    ResponseEntity<JsonNode> searchCoin(@RequestParam("query") String query) throws JsonProcessingException {
        String res = coinService.searchCoin(query);
        JsonNode jsonNode = objectMapper.readTree(res);

        return new ResponseEntity<>(jsonNode, HttpStatus.ACCEPTED);
    }

    @GetMapping("/top50")
    ResponseEntity<JsonNode> getTop50() throws JsonProcessingException {
        String res = coinService.getTop50CoinByMarketCapRank();
        JsonNode jsonNode = objectMapper.readTree(res);

        return new ResponseEntity<>(jsonNode, HttpStatus.ACCEPTED);
    }

    @GetMapping("/trending")
    ResponseEntity<JsonNode> getTrendingCoin() throws JsonProcessingException {
        String res = coinService.getTrendingCoin();
        JsonNode jsonNode = objectMapper.readTree(res);

        return new ResponseEntity<>(jsonNode, HttpStatus.ACCEPTED);
    }

    @GetMapping("/detail/{coinId}")
    ResponseEntity<JsonNode> getCoinDetail(@PathVariable("coinId") String coinId) throws JsonProcessingException {
        String res = coinService.getCoinDetail(coinId);
        JsonNode jsonNode = objectMapper.readTree(res);

        return new ResponseEntity<>(jsonNode, HttpStatus.ACCEPTED);
    }





}
