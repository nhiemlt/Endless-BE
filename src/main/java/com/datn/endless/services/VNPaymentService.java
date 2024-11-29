package com.datn.endless.services;

import com.datn.endless.configs.VNPayConfig;
import com.datn.endless.dtos.OrderDTO;
import com.datn.endless.entities.Order;
import com.datn.endless.exceptions.DuplicateResourceException;
import com.datn.endless.exceptions.OrderNotFoundException;
import com.datn.endless.repositories.OrderRepository;
import com.datn.endless.utils.UUIDUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class VNPaymentService {

    @Autowired
    OrderService orderService;
    @Autowired
    OrderRepository orderRepository;

    public String generatePaymentUrl(String orderId) throws UnsupportedEncodingException {
        // Lấy thông tin đơn hàng từ cơ sở dữ liệu
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("Hóa đơn không tồn tại"));

        OrderDTO orderDTO = orderService.getOrderDTOById(orderId);
        // Kiểm tra trạng thái đơn hàng
        if (!orderDTO.getStatus().equals("Chờ thanh toán")) {
            throw new DuplicateResourceException("Không thể thanh toán cho hóa đơn này!");
        }

        // Khởi tạo thông tin thanh toán
        long totalAmount = order.getTotalMoney().longValue();
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String orderType = "other";
        long amount = totalAmount * 100; // Chuyển đổi sang VND
        String bankCode = "NCB"; // Mã ngân hàng mặc định

        // Tạo tham số VNPay
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", VNPayConfig.vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_BankCode", bankCode);
        vnp_Params.put("vnp_TxnRef", orderId);
        vnp_Params.put("vnp_OrderInfo", "Thanh toán đơn hàng: " + orderId);
        vnp_Params.put("vnp_OrderType", orderType);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", VNPayConfig.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", "127.0.0.1");

        // Lấy thời gian hiện tại
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

        String vnp_CreateDate = order.getOrderDate().format(formatter); // Ngày tạo
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        // Tính toán thời gian hết hạn (15 phút)
        LocalDateTime expireTime = now.plusMinutes(15);
        String vnp_ExpireDate = expireTime.format(formatter);
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        // Tạo chuỗi hash và URL thanh toán
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        for (String fieldName : fieldNames) {
            String fieldValue = vnp_Params.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString())).append('=')
                        .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (!fieldName.equals(fieldNames.get(fieldNames.size() - 1))) {
                    hashData.append('&');
                    query.append('&');
                }
            }
        }

        // Tạo Secure Hash
        String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.secretKey, hashData.toString());
        query.append("&vnp_SecureHash=").append(vnp_SecureHash);

        // Trả về URL thanh toán
        return VNPayConfig.vnp_PayUrl + "?" + query.toString();
    }


    public String handleVnpayCallback(Map<String, String> payload) throws Exception {
        String vnpTxnRef = payload.get("vnp_TxnRef"); // Mã đơn hàng
        String vnpResponseCode = payload.get("vnp_ResponseCode"); // Trạng thái giao dịch
        String vnpAmount = payload.get("vnp_Amount"); // Tổng tiền

        if (vnpTxnRef == null || vnpResponseCode == null) {
            throw new IllegalArgumentException("Thiếu thông tin 'vnp_TxnRef' hoặc 'vnp_ResponseCode' trong payload.");
        }

        // Lấy thông tin đơn hàng từ cơ sở dữ liệu
        Order order = orderRepository.findById(vnpTxnRef)
                .orElseThrow(() -> new OrderNotFoundException("Hóa đơn không tồn tại."));
        BigDecimal totalMoney = order.getTotalMoney(); // Tổng tiền từ đơn hàng

        // Chuyển đổi vnp_Amount (chuỗi) sang BigDecimal và chia cho 100
        BigDecimal amountFromVNPay = new BigDecimal(vnpAmount).divide(BigDecimal.valueOf(100)); // VNĐ

        // Kiểm tra tổng tiền
        if (totalMoney.compareTo(amountFromVNPay) != 0) {
            throw new IllegalArgumentException("Số tiền thanh toán không khớp với tổng tiền đơn hàng.");
        }

        if ("00".equals(vnpResponseCode)) {
            processSuccessfulTransaction(vnpTxnRef);
            return "Transaction successful";
        } else {
            return "Transaction failed with code: " + vnpResponseCode;
        }
    }

    private void processSuccessfulTransaction(String txnRef) {
        orderService.autoMarkOrderAsPaid(txnRef);
    }


    public String generateHtml(String title, String message, String content) {
        return "<!DOCTYPE html>" +
                "<html lang=\"vi\">" +
                "<head>" +
                "<meta charset=\"UTF-8\">" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "<title>" + title + "</title>" +
                "<link href=\"https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css\" rel=\"stylesheet\">" +
                "</head>" +
                "<body>" +
                "<div class=\"grid h-screen place-content-center bg-white px-4\">" +
                "<div class=\"text-center\">" +
                "<h1 class=\"text-9xl font-black text-gray-200\">" + title + "</h1>" +
                "<p class=\"text-2xl font-bold tracking-tight text-gray-900 sm:text-4xl\">" + message + "</p>" +
                "<p class=\"mt-4 text-gray-500\">" + content + "</p>" +
                "<a href=\"http://localhost:3000\" class=\"mt-6 inline-block rounded bg-indigo-600 px-5 py-3 text-sm font-medium text-white hover:bg-indigo-700 focus:outline-none focus:ring\">" +
                "Đi đến trang đăng nhập" +
                "</a>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
}
