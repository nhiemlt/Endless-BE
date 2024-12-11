package com.datn.endless.services;

import com.datn.endless.dtos.VoucherDTO;
import com.datn.endless.entities.User;
import com.datn.endless.entities.Uservoucher;
import com.datn.endless.entities.Voucher;
import com.datn.endless.repositories.UserRepository;
import com.datn.endless.repositories.UservoucherRepository;
import com.datn.endless.repositories.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserVoucherService {

    @Autowired
    private UservoucherRepository userVoucherRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    UserLoginInfomation userLoginInfomation;

    @Autowired
    VoucherService voucherService;
    @Autowired
    private VoucherRepository voucherRepository;

    public List<VoucherDTO> getValidUserVouchers() {
        String username = userLoginInfomation.getCurrentUsername();
        List<Voucher> vouchers = userVoucherRepository.findByUsername(username);

        // Chỉ lọc voucher còn hiệu lực
        return vouchers.stream()
                .filter(voucher -> voucher.getEndDate().isAfter(LocalDateTime.now()))
                .map(voucherService::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<VoucherDTO> getVouchersByAmount(BigDecimal totalAmount) {
        String username = userLoginInfomation.getCurrentUsername();
        List<Voucher> userVouchers = userVoucherRepository.findByUsername(username);

        LocalDate today = LocalDate.now();

        return userVouchers.stream()
                .filter(voucher ->
                        !voucher.getStartDate().toLocalDate().isBefore(today) &&
                                voucher.getEndDate().isAfter(LocalDateTime.now()) &&
                                totalAmount.compareTo(voucher.getLeastBill()) >= 0)
                .map(voucherService::convertToDTO)
                .collect(Collectors.toList());
    }

}
