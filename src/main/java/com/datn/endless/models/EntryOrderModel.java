package com.datn.endless.models;

import lombok.Data;

import java.util.List;

@Data
public class EntryOrderModel {
    private List<EntryOrderDetailModel> details;
}
