package com.datn.endless.services;

import com.datn.endless.dtos.EntryOrderDTO;
import com.datn.endless.dtos.EntryOrderDetailDTO;
import com.datn.endless.entities.Productversion;
import com.datn.endless.entities.Entryorder;
import com.datn.endless.entities.Entryorderdetail;
import com.datn.endless.models.EntryOrderModel;
import com.datn.endless.repositories.OrderdetailRepository;
import com.datn.endless.repositories.ProductversionRepository;
import com.datn.endless.repositories.EntryorderRepository;
import com.datn.endless.repositories.EntryorderdetailRepository;
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
public class PurchaseOrderService {

    @Autowired
    private EntryorderRepository entryorderRepository; 

    @Autowired
    private EntryorderdetailRepository entryorderdetailRepository; 

    @Autowired
    private OrderdetailRepository orderdetailRepository;

    @Autowired
    private ProductversionRepository productversionRepository;

    public EntryOrderDTO createPurchaseOrder(EntryOrderModel purchaseOrderModel) {
        Entryorder purchaseOrder = new Entryorder();
        purchaseOrder.setPurchaseOrderID(UUID.randomUUID().toString());
        purchaseOrder.setPurchaseDate(LocalDate.now());
        purchaseOrder.setTotalMoney(BigDecimal.ZERO);

        List<Entryorderdetail> orderDetails = purchaseOrderModel.getDetails().stream()
                .map(detailModel -> {
                    Productversion productVersion = productversionRepository.findById(detailModel.getProductVersionID())
                            .orElseThrow(() -> new NoSuchElementException("Product version not found"));

                    BigDecimal price = productVersion.getPurchasePrice();
                    BigDecimal detailTotal = price.multiply(new BigDecimal(detailModel.getQuantity()));

                    Entryorderdetail detail = new Entryorderdetail();
                    detail.setPurchaseOrderDetailID(UUID.randomUUID().toString());
                    detail.setProductVersionID(productVersion);
                    detail.setQuantity(detailModel.getQuantity());
                    detail.setPrice(price);
                    detail.setPurchaseOrderID(purchaseOrder);

                    return detail;
                }).collect(Collectors.toList());

        BigDecimal totalMoney = orderDetails.stream()
                .map(detail -> detail.getPrice().multiply(new BigDecimal(detail.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        purchaseOrder.setDetails(orderDetails);
        purchaseOrder.setTotalMoney(totalMoney);

        Entryorder savedOrder = entryorderRepository.save(purchaseOrder);

        return mapToDTO(savedOrder);
    }

    public EntryOrderDTO getPurchaseOrderById(String id) {
        return entryorderRepository.findById(id)
                .map(this::mapToDTO)
                .orElse(null);
    }

    public Page<EntryOrderDTO> getAllPurchaseOrders(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return entryorderRepository.findByPurchaseDateBetween(
                        startDate, endDate, pageable)
                .map(this::mapToDTO);
    }

    public List<EntryOrderDetailDTO> getPurchaseOrderDetails(String id) {
        Entryorder order = entryorderRepository.findById(id).orElse(null);
        return (order != null) ? order.getDetails().stream()
                .map(detail -> {
                    EntryOrderDetailDTO detailDTO = new EntryOrderDetailDTO();
                    detailDTO.setPurchaseOrderDetailID(detail.getPurchaseOrderDetailID());
                    detailDTO.setProductVersionID(detail.getProductVersionID().getProductVersionID());
                    detailDTO.setProductVersionName(detail.getProductVersionID().getVersionName());
                    detailDTO.setQuantity(detail.getQuantity());
                    detailDTO.setPrice(detail.getPrice());
                    return detailDTO;
                }).collect(Collectors.toList()) : null;
    }

    private EntryOrderDTO mapToDTO(Entryorder purchaseOrder) {
        EntryOrderDTO dto = new EntryOrderDTO();
        dto.setPurchaseOrderID(purchaseOrder.getPurchaseOrderID());
        dto.setPurchaseDate(purchaseOrder.getPurchaseDate());
        dto.setTotalMoney(purchaseOrder.getTotalMoney());

        List<EntryOrderDetailDTO> detailDTOs = purchaseOrder.getDetails().stream()
                .map(detail -> {
                    EntryOrderDetailDTO detailDTO = new EntryOrderDetailDTO();
                    detailDTO.setPurchaseOrderDetailID(detail.getPurchaseOrderDetailID());
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
        Integer quantity = quantity = entryorderdetailRepository.findTotalPurchasedQuantityByProductVersion(productVersionID);
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
