package com.datn.endless.schedulers;

import com.datn.endless.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OrderScheduler {

    @Autowired
    private OrderService orderService;

//    @Scheduled(fixedRate = 60000)
//    public void checkAndCancelUnpaidOrders() {
//        System.out.println("\n\nQuét hóa đơn chưa thanh toán");
//        orderService.cancelUnpaidOrdersBefore();
//    }

    @Scheduled(fixedRate = 60000)
    public void checkAndCancelConfirmOrders() {
        System.out.println("\n\nQuét hóa đơn chưa xác nhận");
        orderService.cancelWaitToConfirmOrdersBefore();
    }
}
