package com.koomi.tradingplatfrom.service.imp;

import com.koomi.tradingplatfrom.exception.AssetException;
import com.koomi.tradingplatfrom.model.entity.Asset;
import com.koomi.tradingplatfrom.model.entity.Coin;
import com.koomi.tradingplatfrom.model.entity.User;
import com.koomi.tradingplatfrom.repository.AssetRepository;
import com.koomi.tradingplatfrom.service.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssetServiceImp implements AssetService {

    @Autowired
    AssetRepository assetRepository;

    @Override
    public Asset createAsset(User user, Coin coin, double quantity) {
        Asset asset = new Asset();
        asset.setUser(user);
        asset.setCoin(coin);
        asset.setQuantity(quantity);
        asset.setBuyPrice(coin.getCurrentPrice());

        return assetRepository.save(asset);
    }

    @Override
    public Asset getAssetById(Long assetId) {
        return assetRepository.findById(assetId).orElseThrow(() -> new AssetException("Asset not found"));
    }



    @Override
    public Asset getByUserIdAndId(Long userId, Long assetId) {
        return null;
    }

    @Override
    public List<Asset> getUserAssets(Long userId) {
        return assetRepository.findByUserId(userId);
    }

    @Override
    public Asset updateAsset(Long assetId, double quantity) {
        Asset oldAsset = getAssetById(assetId);
        oldAsset.setQuantity(quantity + oldAsset.getQuantity());

        return assetRepository.save(oldAsset);
    }

    @Override
    public Asset findAssetByUserIdAndCoinId(Long userId, String coinId) {
        return assetRepository.findByUserIdAndCoinId(userId, coinId);
    }

    @Override
    public void deleteAsset(Long assetId) {

        assetRepository.deleteById(assetId);
    }
}
