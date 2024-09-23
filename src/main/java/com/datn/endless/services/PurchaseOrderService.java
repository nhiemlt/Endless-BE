package com.datn.endless.services;

import com.datn.endless.dtos.PurchaseOrderDTO;
import com.datn.endless.dtos.PurchaseOrderDetailDTO;
import com.datn.endless.entities.Productversion;
import com.datn.endless.entities.Purchaseorder;
import com.datn.endless.entities.Purchaseorderdetail;
import com.datn.endless.models.PurchaseOrderDetailModel;
import com.datn.endless.models.PurchaseOrderModel;
import com.datn.endless.repositories.ProductversionRepository;
import com.datn.endless.repositories.PurchaseorderRepository;
import com.datn.endless.repositories.PurchaseorderdetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PurchaseOrderService {

    @Autowired
    private PurchaseorderRepository purchaseOrderRepository;


    @Autowired
    private ProductversionRepository productversionRepository;

    public PurchaseOrderDTO createPurchaseOrder(PurchaseOrderModel purchaseOrderModel) {
        Purchaseorder purchaseOrder = new Purchaseorder();
        purchaseOrder.setPurchaseOrderID(UUID.randomUUID().toString());
        purchaseOrder.setPurchaseDate(LocalDate.now());
        purchaseOrder.setTotalMoney(BigDecimal.ZERO);

        List<Purchaseorderdetail> orderDetails = purchaseOrderModel.getDetails().stream()
                .map(detailModel -> {
                    Productversion productVersion = productversionRepository.findById(detailModel.getProductVersionID())
                            .orElseThrow(() -> new NoSuchElementException("Product version not found"));

                    BigDecimal price = productVersion.getPurchasePrice();
                    BigDecimal detailTotal = price.multiply(new BigDecimal(detailModel.getQuantity()));

                    Purchaseorderdetail detail = new Purchaseorderdetail();
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

        Purchaseorder savedOrder = purchaseOrderRepository.save(purchaseOrder);

        return mapToDTO(savedOrder);
    }

    public PurchaseOrderDTO getPurchaseOrderById(String id) {
        return purchaseOrderRepository.findById(id)
                .map(this::mapToDTO)
                .orElse(null);
    }

    public Page<PurchaseOrderDTO> getAllPurchaseOrders(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return purchaseOrderRepository.findByPurchaseDateBetween(
                        startDate, endDate, pageable)
                .map(this::mapToDTO);
    }

    public List<PurchaseOrderDetailDTO> getPurchaseOrderDetails(String id) {
        Purchaseorder order = purchaseOrderRepository.findById(id).orElse(null);
        return (order != null) ? order.getDetails().stream()
                .map(detail -> {
                    PurchaseOrderDetailDTO detailDTO = new PurchaseOrderDetailDTO();
                    detailDTO.setPurchaseOrderDetailID(detail.getPurchaseOrderDetailID());
                    detailDTO.setProductVersionID(detail.getProductVersionID().getProductVersionID());
                    detailDTO.setProductVersionName(detail.getProductVersionID().getVersionName());
                    detailDTO.setQuantity(detail.getQuantity());
                    detailDTO.setPrice(detail.getPrice());
                    return detailDTO;
                }).collect(Collectors.toList()) : null;
    }

    private PurchaseOrderDTO mapToDTO(Purchaseorder purchaseOrder) {
        PurchaseOrderDTO dto = new PurchaseOrderDTO();
        dto.setPurchaseOrderID(purchaseOrder.getPurchaseOrderID());
        dto.setPurchaseDate(purchaseOrder.getPurchaseDate());
        dto.setTotalMoney(purchaseOrder.getTotalMoney());

        List<PurchaseOrderDetailDTO> detailDTOs = purchaseOrder.getDetails().stream()
                .map(detail -> {
                    PurchaseOrderDetailDTO detailDTO = new PurchaseOrderDetailDTO();
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
}
