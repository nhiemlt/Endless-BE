package com.datn.endless.schedulers;

import com.datn.endless.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OrderScheduler {

    @Autowired
    private OrderService orderService;

    @Scheduled(fixedRate = 60000)
    public void checkAndCancelUnpaidOrders() {
        orderService.cancelUnpaidOrdersBefore();
    }

    @Scheduled(cron = "0 55 23 * * ?")
    public void checkAndCancelConfirmOrders() {
        orderService.cancelWaitToConfirmOrdersBefore();
    }
}
