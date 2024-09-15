package com.datn.endless.services;

import com.datn.endless.dtos.PurchaseOrderDTO;
import com.datn.endless.dtos.PurchaseOrderDetailDTO;
import com.datn.endless.entities.Productversion;
import com.datn.endless.entities.Purchaseorder;
import com.datn.endless.entities.Purchaseorderdetail;
import com.datn.endless.repositories.ProductversionRepository;
import com.datn.endless.repositories.PurchaseorderRepository;
import com.datn.endless.repositories.PurchaseorderdetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PurchaseOrderService {
    @Autowired
    private PurchaseorderRepository purchaseOrderRepository;

    @Autowired
    private PurchaseorderdetailRepository purchaseOrderDetailRepository;

    @Autowired
    private ProductversionRepository productversionRepository;

    public PurchaseOrderDTO createPurchaseOrder(PurchaseOrderDTO purchaseOrderDTO) {
        Purchaseorder purchaseOrder = new Purchaseorder();
        purchaseOrder.setPurchaseOrderID(UUID.randomUUID().toString());
        purchaseOrder.setPurchaseDate(purchaseOrderDTO.getPurchaseDate());
        purchaseOrder.setTotalMoney(BigDecimal.ZERO);

        List<Purchaseorderdetail> orderDetails = new ArrayList<>();

        BigDecimal totalMoney = BigDecimal.ZERO;

        for (PurchaseOrderDetailDTO detailDTO : purchaseOrderDTO.getDetails()) {
            Productversion productVersion = productversionRepository.findById(detailDTO.getProductVersionID())
                    .orElseThrow(() -> new NoSuchElementException("Product version not found"));

            BigDecimal price = detailDTO.getPrice();
            BigDecimal detailTotal = price.multiply(new BigDecimal(detailDTO.getQuantity()));

            totalMoney = totalMoney.add(detailTotal);

            Purchaseorderdetail detail = new Purchaseorderdetail();
            detail.setPurchaseOrderDetailID(UUID.randomUUID().toString());
            detail.setProductVersionID(productVersion);
            detail.setQuantity(detailDTO.getQuantity());
            detail.setPrice(price);
            detail.setPurchaseOrderID(purchaseOrder);

            orderDetails.add(detail);
        }

        purchaseOrder.setDetails(orderDetails);
        purchaseOrder.setTotalMoney(totalMoney);

        Purchaseorder savedOrder = purchaseOrderRepository.save(purchaseOrder);

        return mapToDTO(savedOrder);
    }

    public PurchaseOrderDTO getPurchaseOrderById(String id) {
        Purchaseorder order = purchaseOrderRepository.findById(id).orElse(null);
        return (order != null) ? mapToDTO(order) : null;
    }

    public List<PurchaseOrderDTO> getAllPurchaseOrders() {
        return purchaseOrderRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<PurchaseOrderDTO> searchPurchaseOrders(String status, String startDate, String endDate) {
        LocalDate start = (startDate != null) ? LocalDate.parse(startDate) : null;
        LocalDate end = (endDate != null) ? LocalDate.parse(endDate) : null;

        List<Purchaseorder> orders;

        if (start != null && end != null) {
            orders = purchaseOrderRepository.findByPurchaseDateBetween(start, end);
        } else {
            orders = purchaseOrderRepository.findAll();
        }

        return orders.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<PurchaseOrderDetailDTO> getPurchaseOrderDetails(String id) {
        Purchaseorder order = purchaseOrderRepository.findById(id).orElse(null);
        if (order != null) {
            return order.getDetails().stream().map(detail -> {
                PurchaseOrderDetailDTO detailDTO = new PurchaseOrderDetailDTO();
                detailDTO.setProductVersionID(detail.getProductVersionID().getProductVersionID());
                detailDTO.setQuantity(detail.getQuantity());
                detailDTO.setPrice(detail.getPrice());
                return detailDTO;
            }).collect(Collectors.toList());
        }
        return null;
    }

    private PurchaseOrderDTO mapToDTO(Purchaseorder purchaseOrder) {
        PurchaseOrderDTO dto = new PurchaseOrderDTO();
        dto.setPurchaseOrderID(purchaseOrder.getPurchaseOrderID());
        dto.setPurchaseDate(purchaseOrder.getPurchaseDate());
        dto.setTotalMoney(purchaseOrder.getTotalMoney());

        List<PurchaseOrderDetailDTO> detailDTOs = purchaseOrder.getDetails().stream().map(detail -> {
            PurchaseOrderDetailDTO detailDTO = new PurchaseOrderDetailDTO();
            detailDTO.setPurchaseOrderDetailID(detail.getPurchaseOrderDetailID());
            detailDTO.setProductVersionID(detail.getProductVersionID().getProductVersionID());
            detailDTO.setQuantity(detail.getQuantity());
            detailDTO.setPrice(detail.getPrice());
            return detailDTO;
        }).collect(Collectors.toList());

        dto.setDetails(detailDTOs);
        return dto;
    }
}
