package com.datn.endless.services;

import com.datn.endless.dtos.VoucherDTO;
import com.datn.endless.entities.*;
import com.datn.endless.exceptions.VoucherNotFoundException;
import com.datn.endless.models.NotificationModelForAll;
import com.datn.endless.models.NotificationModelForUser;
import com.datn.endless.models.VoucherModel;
import com.datn.endless.models.VoucherModel2;
import com.datn.endless.repositories.UserRepository;
import com.datn.endless.repositories.UservoucherRepository;
import com.datn.endless.repositories.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class VoucherService {

    @Autowired
    private VoucherRepository voucherRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UservoucherRepository uservoucherRepository;

    @Autowired
    NotificationService notificationService;

    private VoucherDTO convertToDTO(Voucher voucher) {
        VoucherDTO dto = new VoucherDTO();
        dto.setVoucherID(voucher.getVoucherID());
        dto.setVoucherCode(voucher.getVoucherCode());
        dto.setLeastBill(voucher.getLeastBill());
        dto.setLeastDiscount(voucher.getLeastDiscount());
        dto.setBiggestDiscount(voucher.getBiggestDiscount());
        dto.setDiscountLevel(voucher.getDiscountLevel());
        dto.setStartDate(voucher.getStartDate());
        dto.setEndDate(voucher.getEndDate());
        return dto;
    }

    public Page<VoucherDTO> getAllVouchers(String voucherCode, BigDecimal leastBill, BigDecimal leastDiscount, Pageable pageable) {
        Page<Voucher> vouchers = voucherRepository.findByFilters(voucherCode, leastBill, leastDiscount, pageable);
        return vouchers.map(this::convertToDTO);
    }
public VoucherDTO getVoucherById(String id) throws VoucherNotFoundException {
    return voucherRepository.findById(id)
        .map(this::convertToDTO) // Chuyển đổi Voucher sang VoucherDTO
        .orElseThrow(() -> new VoucherNotFoundException("Voucher not found with ID: " + id)); // Ném ngoại lệ nếu không tìm thấy
}

    public void addVoucherAllUser(VoucherModel voucherModel) {
        // Kiểm tra mã voucher có tồn tại không
        if (voucherRepository.findByVoucherCode(voucherModel.getVoucherCode()).isPresent()) {
            throw new RuntimeException("Mã voucher đã tồn tại");
        }

        // Kiểm tra startDate phải trước endDate
        if (!voucherModel.getStartDate().isBefore(voucherModel.getEndDate())) {
            throw new RuntimeException("Ngày bắt đầu phải trước ngày kết thúc");
        }

        // Kiểm tra biggestDiscount phải lớn hơn leastDiscount và nhỏ hơn leastBill
        if (voucherModel.getBiggestDiscount().compareTo(voucherModel.getLeastDiscount()) <= 0) {
            throw new RuntimeException("Giảm tối đa phải lớn hơn giảm tối thiểu");
        }

        if (voucherModel.getBiggestDiscount().compareTo(voucherModel.getLeastBill()) >= 0) {
            throw new RuntimeException("Giảm tối đa phải nhỏ hơn hóa đơn tối thiểu");
        }

        // Tạo đối tượng voucher
        Voucher voucher = new Voucher();

        // Chuyển mã voucher thành chữ in hoa và loại bỏ khoảng trắng
        String voucherCode = voucherModel.getVoucherCode().trim().toUpperCase();
        voucher.setVoucherCode(voucherCode); // Gán mã voucher đã xử lý

        voucher.setLeastBill(voucherModel.getLeastBill());
        voucher.setLeastDiscount(voucherModel.getLeastDiscount());
        voucher.setBiggestDiscount(voucherModel.getBiggestDiscount());
        voucher.setDiscountLevel(voucherModel.getDiscountLevel());
        voucher.setStartDate(voucherModel.getStartDate());
        voucher.setEndDate(voucherModel.getEndDate());

        // Lưu voucher vào database
        voucherRepository.save(voucher);

        // Lấy danh sách tất cả user có active là true
        List<User> activeUsers = userRepository.findByActiveTrue();

        // Cấp voucher cho tất cả user có active là true
        for (User user : activeUsers) {
            Uservoucher userVoucher = new Uservoucher();
            userVoucher.setUserID(user);
            userVoucher.setVoucherID(voucher);
            uservoucherRepository.save(userVoucher);
        }

        // Gửi thông báo cho tất cả người dùng về voucher mới
        NotificationModelForAll notification = new NotificationModelForAll();
        notification.setContent("Bạn vừa nhận được voucher với mã " + voucher.getVoucherCode() +
                " giảm đến " + voucher.getBiggestDiscount() +
                " VNĐ cho đơn từ " + voucher.getLeastBill() + " VNĐ");
        notification.setTitle("Thông báo nhận voucher");
        notificationService.sendNotificationForAll(notification);
    }



    public void addVoucherForUser(VoucherModel2 voucherModel) {
        // Chuyển mã voucher thành chữ in hoa và loại bỏ khoảng trắng
        String voucherCode = voucherModel.getVoucherCode().trim().toUpperCase();

        // Kiểm tra mã voucher có tồn tại không
        if (voucherRepository.findByVoucherCode(voucherCode).isPresent()) {
            throw new RuntimeException("Mã voucher đã tồn tại");
        }

        // Kiểm tra startDate phải trước endDate
        if (!voucherModel.getStartDate().isBefore(voucherModel.getEndDate())) {
            throw new RuntimeException("Ngày bắt đầu phải trước ngày kết thúc");
        }

        // Kiểm tra biggestDiscount phải lớn hơn leastDiscount và nhỏ hơn leastBill
        if (voucherModel.getBiggestDiscount().compareTo(voucherModel.getLeastDiscount()) <= 0) {
            throw new RuntimeException("Giảm tối đa phải lớn hơn giảm tối thiểu");
        }

        if (voucherModel.getBiggestDiscount().compareTo(voucherModel.getLeastBill()) >= 0) {
            throw new RuntimeException("Giảm tối đa phải nhỏ hơn hóa đơn tối thiểu");
        }

        // Tạo đối tượng voucher
        Voucher voucher = new Voucher();
        voucher.setVoucherCode(voucherCode); // Gán mã voucher đã xử lý
        voucher.setLeastBill(voucherModel.getLeastBill());
        voucher.setLeastDiscount(voucherModel.getLeastDiscount());
        voucher.setBiggestDiscount(voucherModel.getBiggestDiscount());
        voucher.setDiscountLevel(voucherModel.getDiscountLevel());
        voucher.setStartDate(voucherModel.getStartDate());
        voucher.setEndDate(voucherModel.getEndDate());

        // Lưu voucher vào database
        Voucher saveVoucher = voucherRepository.save(voucher);
        List<User> users = new ArrayList<>();

        for (String id : voucherModel.getUserIds()) {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + id));

            Uservoucher userVoucher = new Uservoucher();
            userVoucher.setUserID(user);
            userVoucher.setVoucherID(saveVoucher);
            uservoucherRepository.save(userVoucher);

            // Tạo và gửi thông báo cho người dùng
            NotificationModelForUser notification = new NotificationModelForUser();
            notification.setContent("Bạn vừa nhận được voucher với mã " + saveVoucher.getVoucherCode() +
                    " giảm đến " + saveVoucher.getBiggestDiscount() +
                    " VNĐ cho đơn từ " + saveVoucher.getLeastBill() + " VNĐ");
            notification.setTitle("Thông báo nhận voucher");
            notification.setUserID(user.getUserID());

            notificationService.sendNotificationForOrder(notification);
        }
    }




    public void updateVoucher(String id, VoucherModel updatedVoucher) {
        Optional<Voucher> optionalVoucher = voucherRepository.findById(id);
        if (optionalVoucher.isEmpty()) {
            throw new RuntimeException("Không tìm thấy voucher");
        }

        Voucher voucher = optionalVoucher.get();

        // Chuyển mã voucher thành chữ in hoa và loại bỏ khoảng trắng
        String updatedVoucherCode = updatedVoucher.getVoucherCode().trim().toUpperCase();

        // Nếu voucherCode được cập nhật và khác với voucher hiện tại, kiểm tra trùng lặp
        if (!voucher.getVoucherCode().equals(updatedVoucherCode)) {
            if (voucherRepository.findByVoucherCode(updatedVoucherCode).isPresent()) {
                throw new RuntimeException("Mã voucher đã tồn tại");
            }
            voucher.setVoucherCode(updatedVoucherCode);
        }

        // Kiểm tra startDate phải trước endDate
        if (!updatedVoucher.getStartDate().isBefore(updatedVoucher.getEndDate())) {
            throw new RuntimeException("Ngày bắt đầu phải trước ngày kết thúc");
        }

        // Kiểm tra biggestDiscount phải lớn hơn leastDiscount và nhỏ hơn leastBill
        if (updatedVoucher.getBiggestDiscount().compareTo(updatedVoucher.getLeastDiscount()) <= 0) {
            throw new RuntimeException("Giảm tối đa phải lớn hơn giảm tối thiểu");
        }

        if (updatedVoucher.getBiggestDiscount().compareTo(updatedVoucher.getLeastBill()) >= 0) {
            throw new RuntimeException("Giảm tối đa phải nhỏ hơn hóa đơn tối thiểu");
        }

        // Cập nhật các thuộc tính của voucher
        voucher.setLeastBill(updatedVoucher.getLeastBill());
        voucher.setLeastDiscount(updatedVoucher.getLeastDiscount());
        voucher.setBiggestDiscount(updatedVoucher.getBiggestDiscount());
        voucher.setDiscountLevel(updatedVoucher.getDiscountLevel());
        voucher.setStartDate(updatedVoucher.getStartDate());
        voucher.setEndDate(updatedVoucher.getEndDate());

        // Lưu voucher đã cập nhật vào database
        voucherRepository.save(voucher);
    }


    public void deleteVoucher(String id) {
        Optional<Voucher> optionalVoucher = voucherRepository.findById(id);
        if (optionalVoucher.isEmpty()) {
            throw new RuntimeException("Không tìm thấy voucher");
        }
        voucherRepository.deleteById(id);
    }
}
