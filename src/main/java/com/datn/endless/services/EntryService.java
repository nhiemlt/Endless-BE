package com.datn.endless.services;

import com.datn.endless.dtos.EntryDTO;
import com.datn.endless.dtos.EntryDetailDTO;
import com.datn.endless.entities.Productversion;
import com.datn.endless.entities.Entry;
import com.datn.endless.entities.Entrydetail;
import com.datn.endless.models.EntryModel;
import com.datn.endless.repositories.OrderdetailRepository;
import com.datn.endless.repositories.ProductversionRepository;
import com.datn.endless.repositories.EntryRepository;
import com.datn.endless.repositories.EntrydetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EntryService {

    @Autowired
    private EntryRepository entryRepository;

    @Autowired
    private EntrydetailRepository entrydetailRepository;

    @Autowired
    private OrderdetailRepository orderdetailRepository;

    @Autowired
    private ProductversionRepository productversionRepository;

    public EntryDTO createPurchaseOrder(EntryModel purchaseOrderModel) {
        Entry entry = new Entry();
        entry.setEntryID(UUID.randomUUID().toString());
        entry.setOrderDate(LocalDate.now());
        entry.setTotalMoney(BigDecimal.ZERO);

        List<Entrydetail> orderDetails = purchaseOrderModel.getDetails().stream()
                .map(detailModel -> {
                    Productversion productVersion = productversionRepository.findById(detailModel.getProductVersionID())
                            .orElseThrow(() -> new NoSuchElementException("Product version not found"));

                    BigDecimal price = productVersion.getPurchasePrice();
                    BigDecimal detailTotal = price.multiply(new BigDecimal(detailModel.getQuantity()));

                    Entrydetail detail = new Entrydetail();
                    detail.setEntryDetailID(UUID.randomUUID().toString());
                    detail.setProductVersionID(productVersion);
                    detail.setQuantity(detailModel.getQuantity());
                    detail.setPrice(price);
                    detail.setEntry(entry); // Thay đổi liên kết đúng entity

                    return detail;
                }).collect(Collectors.toList());

        BigDecimal totalMoney = orderDetails.stream()
                .map(detail -> detail.getPrice().multiply(new BigDecimal(detail.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        entry.setDetails(orderDetails);
        entry.setTotalMoney(totalMoney);

        Entry savedOrder = entryRepository.save(entry);

        return mapToDTO(savedOrder);
    }

    public EntryDTO getPurchaseOrderById(String id) {
        return entryRepository.findById(id)
                .map(this::mapToDTO)
                .orElse(null);
    }

    public Page<EntryDTO> getAllPurchaseOrders(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return entryRepository.findByPurchaseDateBetween(
                        startDate, endDate, pageable)
                .map(this::mapToDTO);
    }

    public List<EntryDetailDTO> getPurchaseOrderDetails(String id) {
        Entry order = entryRepository.findById(id).orElse(null);
        return (order != null) ? order.getDetails().stream()
                .map(detail -> {
                    EntryDetailDTO detailDTO = new EntryDetailDTO();
                    detailDTO.setPurchaseOrderDetailID(detail.getEntryDetailID());
                    detailDTO.setProductVersionID(detail.getProductVersionID().getProductVersionID());
                    detailDTO.setProductVersionName(detail.getProductVersionID().getVersionName());
                    detailDTO.setQuantity(detail.getQuantity());
                    detailDTO.setPrice(detail.getPrice());
                    return detailDTO;
                }).collect(Collectors.toList()) : null;
    }

    private EntryDTO mapToDTO(Entry entry) {
        EntryDTO dto = new EntryDTO();
        dto.setPurchaseOrderID(entry.getEntryID());
        dto.setPurchaseDate(entry.getOrderDate());
        dto.setTotalMoney(entry.getTotalMoney());


        List<EntryDetailDTO> detailDTOs = entry.getDetails().stream()
                .map(detail -> {
                    EntryDetailDTO detailDTO = new EntryDetailDTO();
                    detailDTO.setPurchaseOrderDetailID(detail.getEntryDetailID());
                    detailDTO.setProductVersionID(detail.getProductVersionID().getProductVersionID());
                    detailDTO.setProductVersionName(detail.getProductVersionID().getVersionName());
                    detailDTO.setQuantity(detail.getQuantity());
                    detailDTO.setPrice(detail.getPrice());
                    return detailDTO;
                }).collect(Collectors.toList());

        dto.setDetails(detailDTOs);
        return dto;
    }

    Integer getProductVersionPurchaseQuantity(String productVersionID){
        Integer quantity = quantity = entrydetailRepository.findTotalPurchasedQuantityByProductVersion(productVersionID);
        return quantity == null ? 0 : quantity;
    }

    Integer getProductVersionOrderQuantity(String productVersionID){
        Integer quantity = quantity = orderdetailRepository.findTotalSoldQuantityByProductVersion(productVersionID);
        return quantity == null ? 0 : quantity;
    }

    Integer getProductVersionQuantity(String productVersionID){
        return getProductVersionPurchaseQuantity(productVersionID) - getProductVersionOrderQuantity(productVersionID);
    }
}