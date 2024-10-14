package com.koomi.tradingplatfrom.service.imp;

import com.fasterxml.jackson.databind.JsonNode;
import com.koomi.tradingplatfrom.exception.CoinException;
import com.koomi.tradingplatfrom.exception.HttpClientException;
import com.koomi.tradingplatfrom.model.entity.Coin;
import com.koomi.tradingplatfrom.repository.CoinRepository;
import com.koomi.tradingplatfrom.service.CoinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;

@Service
public class CoinServiceImp implements CoinService {

    @Autowired
    private CoinRepository coinRepository;

    @Autowired
    private ObjectMapper objectMapper;


    @Override
    public List<Coin> getCoinList(int page) {
        String url = "https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&per_page=50&page=" + page;

        RestTemplate restTemplate = new RestTemplate();

        try {
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            // Chuyển đổi chuỗi JSON thành List<Coin> bằng ObjectMapper
            List<Coin> coinList = objectMapper.readValue(response.getBody(),
                    new TypeReference<List<Coin>>() {});

            return coinList;

        } catch (HttpClientException e) {

            throw new HttpClientException("Error while fetching data from CoinGecko API: " + e.getMessage());
        } catch (Exception e) {
            throw new HttpClientException("An unexpected error occurred while processing CoinGecko API response: " + e.getMessage());
        }


    }


    @Override
    public String getMarketChart(String coinId, int days) {
        String url = "https://api.coingecko.com/api/v3/coins/" + coinId + "/market_chart?vs_currency=usd&days=" + days;

        RestTemplate restTemplate = new RestTemplate();

        try {
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            return response.getBody();

        } catch (HttpClientException e) {
            throw new HttpClientException(HttpStatus.BAD_REQUEST,
                    "Error while fetching data from CoinGecko API: " + e.getMessage());
        } catch (HttpServerErrorException e) {

            throw new HttpClientException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Server error while fetching data from CoinGecko API: " + e.getStatusCode() + " " + e.getResponseBodyAsString());
        } catch (ResourceAccessException e) {

            throw new HttpClientException(HttpStatus.GATEWAY_TIMEOUT,
                    "Unable to connect to CoinGecko API: " + e.getMessage());
        } catch (Exception e) {

            throw new HttpClientException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "An unexpected error occurred while processing CoinGecko API response: " + e.getMessage());
        }
    }


    @Override
    public String getCoinDetail(String coinId) {
        String url = "https://api.coingecko.com/api/v3/coins/" + coinId;

        RestTemplate restTemplate = new RestTemplate();

        try {
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            JsonNode jsonNode = objectMapper.readTree(response.getBody());

            Coin coin = new Coin();
            coin.setId(jsonNode.get("id").asText());
            coin.setName(jsonNode.get("name").asText());
            coin.setSymbol(jsonNode.get("symbol").asText());
            coin.setImageUrl(jsonNode.get("image").get("large").asText());

            JsonNode marketData = jsonNode.get("market_data");

            coin.setCurrentPrice(marketData.get("current_price").get("usd").asDouble());
            coin.setMarketCap(marketData.get("market_cap").get("usd").asLong());
            coin.setMarketCapRank(marketData.get("market_cap_rank").asInt());
            coin.setTotalVolume(marketData.get("total_volume").get("usd").asLong());
            coin.setHigh24h(marketData.get("high_24h").get("usd").asDouble());
            coin.setLow24h(marketData.get("low_24h").get("usd").asDouble());
            coin.setPriceChange24h(marketData.get("price_change_24h").get("usd").asDouble());
            coin.setPriceChangePercentage24h(marketData.get("price_change_percentage_24h").asDouble());
            coin.setMarketCapChange24h(marketData.get("market_cap_change_24h").get("usd").asLong());
            coin.setMarketCapChangePercentage24h(marketData.get("market_cap_change_percentage_24h").asDouble());
            coin.setTotalSupply(marketData.get("total_supply").asLong());

            coinRepository.save(coin);


            return response.getBody();

        } catch (HttpClientException e) {
            throw new HttpClientException(HttpStatus.BAD_REQUEST,
                    "Error while fetching data from CoinGecko API: " + e.getMessage());
        } catch (HttpServerErrorException e) {

            throw new HttpClientException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Server error while fetching data from CoinGecko API: " + e.getStatusCode() + " " + e.getResponseBodyAsString());
        } catch (ResourceAccessException e) {

            throw new HttpClientException(HttpStatus.GATEWAY_TIMEOUT,
                    "Unable to connect to CoinGecko API: " + e.getMessage());
        } catch (Exception e) {

            throw new HttpClientException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "An unexpected error occurred while processing CoinGecko API response: " + e.getMessage());
        }
    }


    @Override
    public Coin findCoinById(String id) {
        Optional<Coin> coin = coinRepository.findById(id);

        if(coin.isEmpty()) {
            throw new CoinException("Coin not found");
        }
        return coin.get();
    }

    @Override
    public String searchCoin(String keyword) {
        String url = "https://api.coingecko.com/api/v3/search?query/" + keyword;

        RestTemplate restTemplate = new RestTemplate();

        try {
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            return response.getBody();

        } catch (HttpClientException e) {
            throw new HttpClientException(HttpStatus.BAD_REQUEST,
                    "Error while fetching data from CoinGecko API: " + e.getMessage());
        } catch (HttpServerErrorException e) {

            throw new HttpClientException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Server error while fetching data from CoinGecko API: " + e.getStatusCode() + " " + e.getResponseBodyAsString());
        } catch (ResourceAccessException e) {

            throw new HttpClientException(HttpStatus.GATEWAY_TIMEOUT,
                    "Unable to connect to CoinGecko API: " + e.getMessage());
        } catch (Exception e) {

            throw new HttpClientException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "An unexpected error occurred while processing CoinGecko API response: " + e.getMessage());
        }
    }


    @Override
    public String getTop50CoinByMarketCapRank() {
        String url = "https://api.coingecko.com/api/v3/coins/market_chart?vs_currency=usd&per_page=50&page=1" ;

        RestTemplate restTemplate = new RestTemplate();

        try {
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            return response.getBody();

        } catch (HttpClientException e) {
            throw new HttpClientException(HttpStatus.BAD_REQUEST,
                    "Error while fetching data from CoinGecko API: " + e.getMessage());
        } catch (HttpServerErrorException e) {

            throw new HttpClientException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Server error while fetching data from CoinGecko API: " + e.getStatusCode() + " " + e.getResponseBodyAsString());
        } catch (ResourceAccessException e) {

            throw new HttpClientException(HttpStatus.GATEWAY_TIMEOUT,
                    "Unable to connect to CoinGecko API: " + e.getMessage());
        } catch (Exception e) {

            throw new HttpClientException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "An unexpected error occurred while processing CoinGecko API response: " + e.getMessage());
        }
    }

    @Override
    public String getTrendingCoin() {
        String url = "https://api.coingecko.com/api/v3/search/trending\n";

        RestTemplate restTemplate = new RestTemplate();

        try {
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            return response.getBody();

        } catch (HttpClientException e) {
            throw new HttpClientException(HttpStatus.BAD_REQUEST,
                    "Error while fetching data from CoinGecko API: " + e.getMessage());
        } catch (HttpServerErrorException e) {

            throw new HttpClientException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Server error while fetching data from CoinGecko API: " + e.getStatusCode() + " " + e.getResponseBodyAsString());
        } catch (ResourceAccessException e) {

            throw new HttpClientException(HttpStatus.GATEWAY_TIMEOUT,
                    "Unable to connect to CoinGecko API: " + e.getMessage());
        } catch (Exception e) {

            throw new HttpClientException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "An unexpected error occurred while processing CoinGecko API response: " + e.getMessage());
        }
    }
}
