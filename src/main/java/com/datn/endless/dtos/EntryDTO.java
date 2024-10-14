package com.datn.endless.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class EntryDTO {
    private String purchaseOrderID;
    private LocalDate purchaseDate;
    private BigDecimal totalMoney;
    private List<EntryDetailDTO> details;
}
