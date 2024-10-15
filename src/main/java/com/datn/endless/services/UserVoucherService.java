package com.datn.endless.services;

import com.datn.endless.dtos.VoucherDTO;
import com.datn.endless.entities.User;
import com.datn.endless.entities.Uservoucher;
import com.datn.endless.entities.Voucher;
import com.datn.endless.repositories.UserRepository;
import com.datn.endless.repositories.UservoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserVoucherService {
    @Autowired
    private UservoucherRepository userVoucherRepository;

    @Autowired
    private UserRepository userRepository;

    public List<VoucherDTO> getValidUserVouchers(String username) {
        // Tìm người dùng dựa trên tên đăng nhập
        User user = userRepository.findByUsername(username);

        // Tìm tất cả các Uservoucher của người dùng
        List<Uservoucher> userVouchers = userVoucherRepository.findByUserID(user);

        // Lọc và chỉ giữ lại những voucher còn hạn sử dụng
        LocalDate today = LocalDate.now();
        return userVouchers.stream()
                .map(uv -> {
                    Voucher voucher = uv.getVoucherID();
                    if (voucher != null && (voucher.getEndDate() == null || voucher.getEndDate().isAfter(today))) {
                        // Tạo VoucherDTO
                        VoucherDTO voucherDTO = new VoucherDTO();
                        voucherDTO.setVoucherID(voucher.getVoucherID());
                        voucherDTO.setVoucherCode(voucher.getVoucherCode());
                        voucherDTO.setLeastBill(voucher.getLeastBill());
                        voucherDTO.setLeastDiscount(voucher.getLeastDiscount());
                        voucherDTO.setBiggestDiscount(voucher.getBiggestDiscount());
                        voucherDTO.setDiscountLevel(voucher.getDiscountLevel());
                        voucherDTO.setDiscountForm(voucher.getDiscountForm());
                        voucherDTO.setStartDate(voucher.getStartDate());
                        voucherDTO.setEndDate(voucher.getEndDate());
                        return voucherDTO;
                    }
                    return null;
                })
                .filter(voucherDTO -> voucherDTO != null)  // Bỏ qua các voucher không hợp lệ
                .collect(Collectors.toList());
    }
}
