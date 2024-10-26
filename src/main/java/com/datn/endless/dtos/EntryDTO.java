package com.datn.endless.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class EntryDTO {
    private String EntryID;
    private LocalDate entryDate;
    private BigDecimal totalMoney;
    private List<EntryDetailDTO> details;
}
