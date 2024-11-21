package com.datn.endless.controllers;

import com.datn.endless.configs.ZaloPayConfig;
import com.datn.endless.services.ZaloPaymentService;
import com.datn.endless.exceptions.DuplicateResourceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class ZaloPaymentController {

    @Autowired
    private ZaloPaymentService zaloPaymentService;

    // Tạo đơn hàng thanh toán
    @PostMapping("/create/{order_id}")
    public ResponseEntity<Map<String, Object>> createPayment(@PathVariable("order_id") String orderId) {
        try {
            Map<String, Object> response = zaloPaymentService.createPayment(orderId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (DuplicateResourceException e) {
            // Trả về lỗi nếu trạng thái đơn hàng không phải là "Chờ thanh toán"
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // Xử lý lỗi chung
            return new ResponseEntity<>(Map.of("message", "Đã có lỗi xảy ra, vui lòng thử lại sau."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Lấy trạng thái thanh toán của đơn hàng
    @GetMapping("/status/{apptransid}")
    public ResponseEntity<Map<String, Object>> getPaymentStatus(@PathVariable("apptransid") String apptransid) {
        try {
            Map<String, Object> status = zaloPaymentService.getStatusByApptransid(apptransid);
            return new ResponseEntity<>(status, HttpStatus.OK);
        } catch (Exception e) {
            // Xử lý lỗi khi lấy trạng thái thanh toán
            return new ResponseEntity<>(Map.of("message", "Không thể lấy trạng thái thanh toán."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/callback")
    public ResponseEntity<String> handleZaloPayCallback(@RequestParam Map<String, String> payload) {
        try {
            // Xử lý callback từ ZaloPay
            String response = zaloPaymentService.handleZaloPayCallback(payload);

            // Kiểm tra kết quả giao dịch và trả về trang HTML thích hợp
            if (response.equals("Transaction successful")) {
                // Thanh toán thành công
                String htmlResponse = zaloPaymentService.generateHtml("Thanh toán thành công",
                        "Cảm ơn bạn đã thanh toán.",
                        "Đơn hàng của bạn đã được thanh toán thành công.");
                return new ResponseEntity<>(htmlResponse, HttpStatus.OK);
            } else {
                // Thanh toán thất bại
                String htmlResponse = zaloPaymentService.generateHtml("Thanh toán thất bại",
                        "Giao dịch không thành công.",
                        "Xin lỗi, có lỗi xảy ra trong quá trình thanh toán. Vui lòng thử lại sau.");
                return new ResponseEntity<>(htmlResponse, HttpStatus.OK);
            }
        } catch (Exception e) {
            // Xử lý lỗi khi nhận callback
            String htmlResponse = zaloPaymentService.generateHtml("Có lỗi xảy ra",
                    "Có lỗi xảy ra khi xử lý giao dịch.",
                    "Vui lòng liên hệ với chúng tôi để biết thêm chi tiết.");
            return new ResponseEntity<>(htmlResponse, HttpStatus.BAD_REQUEST);
        }
    }
}
