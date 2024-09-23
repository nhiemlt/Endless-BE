package com.datn.endless.services;

import com.datn.endless.dtos.VoucherDTO;
import com.datn.endless.models.VoucherModel;
import com.datn.endless.entities.Voucher;
import com.datn.endless.repositories.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class VoucherService {

    @Autowired
    private VoucherRepository voucherRepository;

    private VoucherDTO convertToDTO(Voucher voucher) {
        VoucherDTO dto = new VoucherDTO();
        dto.setVoucherID(voucher.getVoucherID());
        dto.setVoucherCode(voucher.getVoucherCode());
        dto.setLeastBill(voucher.getLeastBill());
        dto.setLeastDiscount(voucher.getLeastDiscount());
        dto.setBiggestDiscount(voucher.getBiggestDiscount());
        dto.setDiscountLevel(voucher.getDiscountLevel());
        dto.setDiscountForm(voucher.getDiscountForm());
        dto.setStartDate(voucher.getStartDate());
        dto.setEndDate(voucher.getEndDate());
        return dto;
    }

    public Page<VoucherDTO> getAllVouchers(String voucherCode, BigDecimal leastBill, BigDecimal leastDiscount, Pageable pageable) {
        Page<Voucher> vouchers = voucherRepository.findByFilters(voucherCode, leastBill, leastDiscount, pageable);
        return vouchers.map(this::convertToDTO);
    }

    public void addVoucher(VoucherModel voucherModel) {
        // Kiểm tra mã voucher có tồn tại không
        if (voucherRepository.findByVoucherCode(voucherModel.getVoucherCode()).isPresent()) {
            throw new RuntimeException("Voucher code already exists");
        }

        // Kiểm tra startDate phải trước endDate
        if (!voucherModel.getStartDate().isBefore(voucherModel.getEndDate())) {
            throw new RuntimeException("startDate must be before endDate");
        }

        Voucher voucher = new Voucher();
        voucher.setVoucherCode(voucherModel.getVoucherCode());
        voucher.setLeastBill(voucherModel.getLeastBill());
        voucher.setLeastDiscount(voucherModel.getLeastDiscount());
        voucher.setBiggestDiscount(voucherModel.getBiggestDiscount());
        voucher.setDiscountLevel(voucherModel.getDiscountLevel());
        voucher.setDiscountForm(voucherModel.getDiscountForm());
        voucher.setStartDate(voucherModel.getStartDate());
        voucher.setEndDate(voucherModel.getEndDate());

        voucherRepository.save(voucher);
    }

    public void updateVoucher(String id, VoucherModel updatedVoucher) {
        Optional<Voucher> optionalVoucher = voucherRepository.findById(id);
        if (optionalVoucher.isEmpty()) {
            throw new RuntimeException("Voucher not found");
        }

        Voucher voucher = optionalVoucher.get();

        // Nếu voucherCode được cập nhật và khác với voucher hiện tại, kiểm tra trùng lặp
        if (!voucher.getVoucherCode().equals(updatedVoucher.getVoucherCode())) {
            if (voucherRepository.findByVoucherCode(updatedVoucher.getVoucherCode()).isPresent()) {
                throw new RuntimeException("Voucher code already exists");
            }
            voucher.setVoucherCode(updatedVoucher.getVoucherCode());
        }

        // Kiểm tra startDate phải trước endDate
        if (!updatedVoucher.getStartDate().isBefore(updatedVoucher.getEndDate())) {
            throw new RuntimeException("startDate must be before endDate");
        }

        voucher.setLeastBill(updatedVoucher.getLeastBill());
        voucher.setLeastDiscount(updatedVoucher.getLeastDiscount());
        voucher.setBiggestDiscount(updatedVoucher.getBiggestDiscount());
        voucher.setDiscountLevel(updatedVoucher.getDiscountLevel());
        voucher.setDiscountForm(updatedVoucher.getDiscountForm());
        voucher.setStartDate(updatedVoucher.getStartDate());
        voucher.setEndDate(updatedVoucher.getEndDate());

        voucherRepository.save(voucher);
    }

    public void deleteVoucher(String id) {
        Optional<Voucher> optionalVoucher = voucherRepository.findById(id);
        if (optionalVoucher.isEmpty()) {
            throw new RuntimeException("Voucher not found");
        }
        voucherRepository.deleteById(id);
    }
}