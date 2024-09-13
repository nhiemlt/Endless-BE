package com.datn.endless.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class OrderStatusDTO {
    private String orderID;
    private Integer statusID;
    private Instant time;
}
