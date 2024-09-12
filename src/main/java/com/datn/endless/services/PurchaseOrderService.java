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

import java.util.List;

@Service
public class PurchaseOrderService {
    @Autowired
    private PurchaseorderRepository purchaseOrderRepository;

    @Autowired
    private PurchaseorderdetailRepository purchaseOrderDetailRepository;

    @Autowired
    private ProductversionRepository productversionRepository;

    public Purchaseorder createPurchaseOrder(PurchaseOrderDTO purchaseOrderDTO) {
        // Tạo mới đơn nhập hàng
        Purchaseorder purchaseOrder = new Purchaseorder();
        purchaseOrder.setPurchaseDate(purchaseOrderDTO.getPurchaseDate());
        purchaseOrder.setPurchaseOrderStatus(purchaseOrderDTO.getPurchaseOrderStatus());
        purchaseOrder.setTotalMoney(purchaseOrderDTO.getTotalMoney());

        // Lưu đơn nhập hàng vào cơ sở dữ liệu
        Purchaseorder savedPurchaseOrder = purchaseOrderRepository.save(purchaseOrder);

        // Tạo và lưu các chi tiết đơn nhập hàng
        for (PurchaseOrderDetailDTO detailDTO : purchaseOrderDTO.getDetails()) {
            // Kiểm tra sự tồn tại của ProductVersion
            Productversion productVersion = productversionRepository.findById(detailDTO.getProductVersionID())
                    .orElseThrow(() -> new RuntimeException("Product version not found: " + detailDTO.getProductVersionID()));

            Purchaseorderdetail detail = new Purchaseorderdetail();
            detail.setPurchaseOrderID(savedPurchaseOrder);
            detail.setProductVersionID(productVersion);
            detail.setQuantity(detailDTO.getQuantity());
            detail.setPrice(detailDTO.getPrice());

            purchaseOrderDetailRepository.save(detail);
        }

        return savedPurchaseOrder;
    }

    public Purchaseorder getPurchaseOrderById(String id) {
        return purchaseOrderRepository.findById(id).orElse(null);
    }

    public List<Purchaseorder> getAllPurchaseOrders() {
        return purchaseOrderRepository.findAll();
    }
}

