package com.koomi.tradingplatfrom.controller;


import com.koomi.tradingplatfrom.model.entity.Asset;
import com.koomi.tradingplatfrom.model.entity.User;
import com.koomi.tradingplatfrom.service.AssetService;
import com.koomi.tradingplatfrom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/asset")
public class AssetController {

    @Autowired
    private AssetService assetService;

    @Autowired
    private UserService userService;

    @GetMapping("/{assetId}")
    public ResponseEntity<Asset> getAssetById(@PathVariable Long assetId) {
        Asset asset = assetService.getAssetById(assetId);
        return ResponseEntity.ok(asset);
    }

    @GetMapping("/coin/{coinId}/user")
    public ResponseEntity<Asset> getAssetByUserIdAndCoinId(
            @RequestHeader("Authorization") String jwt,
            @PathVariable String coinId) {
        Asset asset = assetService.findAssetByUserIdAndCoinId(userService.findUserProfileByJwt(jwt).getId(), coinId);
        return ResponseEntity.ok(asset);
    }

    @GetMapping
    public ResponseEntity<Asset> getUserAssets(@RequestHeader("Authorization") String jwt) {
        User user = userService.findUserProfileByJwt(jwt);
        List<Asset> assets = assetService.getUserAssets(user.getId());
        return ResponseEntity.ok((Asset) assets);
    }

}
