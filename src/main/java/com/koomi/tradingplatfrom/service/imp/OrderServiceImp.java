package com.koomi.tradingplatfrom.service.imp;

import com.koomi.tradingplatfrom.domain.OrderStatus;
import com.koomi.tradingplatfrom.domain.OrderType;
import com.koomi.tradingplatfrom.exception.OrderException;
import com.koomi.tradingplatfrom.model.entity.*;
import com.koomi.tradingplatfrom.repository.OrderItemRepository;
import com.koomi.tradingplatfrom.repository.OrderRepository;
import com.koomi.tradingplatfrom.service.AssetService;
import com.koomi.tradingplatfrom.service.OrderService;
import com.koomi.tradingplatfrom.service.WalletService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class OrderServiceImp implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private  WalletService walletService;


    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private AssetService assetService;

    @Override
    public Order createOrder(User user, OrderItem orderItem, OrderType orderType) {

        double price = orderItem.getCoin().getCurrentPrice()*orderItem.getQuantity();

        Order order = new Order();
        order.setUser(user);
        order.setOrderItem(orderItem);
        order.setOrderType(orderType);
        order.setPrice(BigDecimal.valueOf(price));
        order.setTimestamp(LocalDate.now());
        order.setStatus(OrderStatus.PENDING);


        return orderRepository.save(order);
    }

    @Override
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new OrderException("Order not found"));
    }

    @Override
    public List<Order> getAllOrderOfUser(Long userId, OrderType orderType, String assetSymbol) {
        return orderRepository.findByUserId(userId);
    }

    private OrderItem createOrderItem(Coin coin, double quantity, double buyPrice, double sellPrice){
        OrderItem orderItem = new OrderItem();
        orderItem.setCoin(coin);
        orderItem.setQuantity(quantity);
        orderItem.setBuyPrice(buyPrice);
        orderItem.setSellPrice(sellPrice);
        return orderItemRepository.save(orderItem);
    }

    @Transactional
    public Order buyAsset(Coin coin, double quantity, User user){
        if(quantity <= 0){
            throw new OrderException("Quantity must be greater than 0");
        }

        double buyPrice = coin.getCurrentPrice();
        OrderItem orderItem = createOrderItem(coin, quantity, buyPrice, 0);

        Order order = createOrder(user, orderItem, OrderType.BUY);
        orderItem.setOrder(order);

        walletService.payOderPayment(order, user);

        order.setStatus(OrderStatus.SUCCESS);
        order.setOrderType(OrderType.BUY);
        Order savedOrder = orderRepository.save(order);

        //create asset
        Asset oldAsset = assetService.findAssetByUserIdAndCoinId(order.getUser().getId(),
                order.getOrderItem().getCoin().getId());

        if(oldAsset == null){
            assetService.createAsset(user, orderItem.getCoin(), orderItem.getQuantity());
        } else {
            assetService.updateAsset(oldAsset.getId(), orderItem.getQuantity());
        }

        return savedOrder;

    }

    @Transactional
    public Order sellAsset(Coin coin, double quantity, User user){
        if(quantity <= 0){
            throw new OrderException("Quantity must be greater than 0");
        }

        double buyPrice = coin.getCurrentPrice();

        double sellPrice = coin.getCurrentPrice();


        Asset assetToSell = assetService.findAssetByUserIdAndCoinId(user.getId(), coin.getId());


        if(assetToSell != null) {
            OrderItem orderItem = createOrderItem(coin, quantity, buyPrice, sellPrice);

            Order order = createOrder(user, orderItem, OrderType.SELL);
            orderItem.setOrder(order);

            if (assetToSell.getQuantity() >= quantity) {
                order.setStatus(OrderStatus.SUCCESS);
                order.setOrderType(OrderType.SELL);
                Order savedOrder = orderRepository.save(order);

                walletService.payOderPayment(order, user);

                Asset updateAsset = assetService.updateAsset(assetToSell.getId(), -quantity);

                if (updateAsset.getQuantity() * coin.getCurrentPrice() <= 0) {
                    assetService.deleteAsset(updateAsset.getId());
                }
                return savedOrder;
            }
            throw new OrderException("Insufficient to sell");
        }
        throw new OrderException("Asset not found");
    }

    @Override
    @Transactional
    public Order processOrder(Coin coin, double quantity, OrderType orderType, User user) {
        return switch (orderType) {
            case BUY -> buyAsset(coin, quantity, user);
            case SELL -> sellAsset(coin, quantity, user);

            default -> throw new OrderException("Invalid order type: " + orderType);
        };
    }

}
