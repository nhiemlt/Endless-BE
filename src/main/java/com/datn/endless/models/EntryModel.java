package com.datn.endless.models;

import lombok.Data;

import java.util.List;

@Data
public class EntryModel {
    private List<EntryDetailModel> details;
}
