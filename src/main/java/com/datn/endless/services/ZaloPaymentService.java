package com.datn.endless.services;

import com.datn.endless.dtos.OrderDTO;
import com.datn.endless.dtos.OrderDetailDTO;
import com.datn.endless.entities.Order;
import com.datn.endless.exceptions.DuplicateResourceException;
import com.datn.endless.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.datn.endless.configs.ZaloPayConfig;
import com.datn.endless.utils.HMACUtil;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.*;

@Service
public class ZaloPaymentService {
    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    // Hàm lấy thời gian hiện tại theo định dạng
    public String getCurrentTimeString(String format) {
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT+7"));
        SimpleDateFormat fmt = new SimpleDateFormat(format);
        fmt.setCalendar(cal);
        return fmt.format(cal.getTimeInMillis());
    }

    // Tạo đơn hàng thanh toán
    public Map<String, Object> createPayment(String order_id) throws Exception {
        OrderDTO order = orderService.getOrderDTOById(order_id);
        if (!order.getStatus().equals("Chờ thanh toán")) {
            throw new DuplicateResourceException("Không thể thanh toán cho hóa đơn này!");
        }

        Map<String, Object> zalopayParams = new HashMap<>();
        zalopayParams.put("appid", ZaloPayConfig.APP_ID);
        zalopayParams.put("apptransid", order.getOrderID());
        long unixTimestampInMilliseconds = order.getOrderDate()
                .atZone(ZoneId.systemDefault()) // Chuyển sang ZonedDateTime dựa trên múi giờ hệ thống
                .toInstant()                   // Chuyển sang Instant
                .toEpochMilli();
        zalopayParams.put("apptime", unixTimestampInMilliseconds);
        zalopayParams.put("appuser", order.getOrderName());
        zalopayParams.put("amount", order.getTotalMoney().longValue());
        zalopayParams.put("description", "Thanh toán đơn hàng #" + order_id);
        zalopayParams.put("bankcode", "");// Chuyển danh sách OrderDetailDTO thành chuỗi JSON cho `item`
        List<OrderDetailDTO> orderDetails = order.getOrderDetails();
        List<Map<String, Object>> items = new ArrayList<>();

        for (OrderDetailDTO detail : orderDetails) {
            Map<String, Object> item = new HashMap<>();
            item.put("itemid", detail.getProductVersionID()); // Sử dụng ProductVersionID làm mã sản phẩm
            item.put("itemname", detail.getProductVersionName()+ " "+ detail.getProductName());    // Tên sản phẩm
            item.put("itemprice", detail.getDiscountPrice());         // Giá sản phẩm
            item.put("itemquantity", detail.getQuantity());   // Số lượng
            items.add(item);
        }

        // Chuyển danh sách `items` thành JSON và đưa vào params
        String itemJson = new JSONObject().put("items", items).toString();
        zalopayParams.put("item", itemJson);

        // Embed data
        Map<String, String> embeddata = new HashMap<>();
        embeddata.put("merchantinfo", "endless");
        embeddata.put("promotioninfo", "");
        embeddata.put("redirecturl", ZaloPayConfig.REDIRECT_URL);

        Map<String, String> columninfo = new HashMap<>();
        columninfo.put("store_name", "E-Shop");
        embeddata.put("columninfo", new JSONObject(columninfo).toString());
        zalopayParams.put("embeddata", new JSONObject(embeddata).toString());

        // Tính MAC
        String data = zalopayParams.get("appid") + "|" + zalopayParams.get("apptransid") + "|"
                + zalopayParams.get("appuser") + "|" + zalopayParams.get("amount") + "|"
                + zalopayParams.get("apptime") + "|" + zalopayParams.get("embeddata") + "|"
                + zalopayParams.get("item");
        zalopayParams.put("mac", HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, ZaloPayConfig.KEY1, data));

        // Gửi yêu cầu POST
        String response = sendPostRequest(ZaloPayConfig.CREATE_ORDER_URL, zalopayParams);

        // Phân tích phản hồi
        JSONObject result = new JSONObject(response);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("returnmessage", result.get("returnmessage"));
        resultMap.put("orderurl", result.get("orderurl"));
        resultMap.put("returncode", result.get("returncode"));
        resultMap.put("zptranstoken", result.get("zptranstoken"));

        return resultMap;
    }

    // Lấy trạng thái đơn hàng
    public Map<String, Object> getStatusByApptransid(String apptransid) throws Exception {
        String appid = ZaloPayConfig.APP_ID;
        String key1 = ZaloPayConfig.KEY1;
        String data = appid + "|" + apptransid + "|" + key1; // appid|apptransid|key1
        String mac = HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, key1, data);

        Map<String, String> params = new HashMap<>();
        params.put("appid", appid);
        params.put("apptransid", apptransid);
        params.put("mac", mac);

        // Gửi yêu cầu GET
        String response = sendGetRequest(ZaloPayConfig.GET_STATUS_PAY_URL, params);

        // Phân tích phản hồi
        JSONObject result = new JSONObject(response);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("returncode", result.get("returncode"));
        resultMap.put("returnmessage", result.get("returnmessage"));
        resultMap.put("isprocessing", result.get("isprocessing"));
        resultMap.put("amount", result.get("amount"));
        resultMap.put("discountamount", result.get("discountamount"));
        resultMap.put("zptransid", result.get("zptransid"));

        return resultMap;
    }

    // Gửi yêu cầu POST
    private String sendPostRequest(String urlString, Map<String, Object> params) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (postData.length() > 0) postData.append("&");
            postData.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            postData.append("=");
            postData.append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
        }

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = postData.toString().getBytes("UTF-8");
            os.write(input, 0, input.length);
        }

        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            return response.toString();
        }
    }

    // Gửi yêu cầu GET
    private String sendGetRequest(String urlString, Map<String, String> params) throws IOException {
        StringBuilder urlWithParams = new StringBuilder(urlString);
        if (params != null && !params.isEmpty()) {
            urlWithParams.append("?");
            for (Map.Entry<String, String> entry : params.entrySet()) {
                urlWithParams.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                urlWithParams.append("=");
                urlWithParams.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                urlWithParams.append("&");
            }
            urlWithParams.setLength(urlWithParams.length() - 1);
        }

        URL url = new URL(urlWithParams.toString());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            return response.toString();
        }
    }

    public String handleZaloPayCallback(Map<String, String> payload) throws Exception {
        // Lấy dữ liệu từ callback
        String callbackData = payload.get("data");  // Dữ liệu của đơn hàng từ ZaloPay
        String callbackMac = payload.get("mac");    // MAC từ ZaloPay để xác thực dữ liệu

        // Chuyển dữ liệu callback thành đối tượng JSON để dễ xử lý
        JSONObject dataJson = new JSONObject(callbackData);
        String apptransid = dataJson.getString("apptransid");  // Mã giao dịch của đơn hàng
        String zptransid = dataJson.getString("zptransid");    // Mã giao dịch của ZaloPay
        String responseCode = dataJson.getString("returncode"); // Mã phản hồi
        long amount = dataJson.getLong("amount");  // Số tiền ứng dụng nhận được
        long userFeeAmount = dataJson.getLong("userfeeamount");  // Số tiền phí
        long discountAmount = dataJson.getLong("discountamount"); // Số tiền giảm giá

        // Xác minh MAC để đảm bảo tính toàn vẹn
        String calculatedMac = calculateZaloPayMac(callbackData);
        if (!callbackMac.equals(calculatedMac)) {
            return "Invalid MAC"; // Nếu MAC không hợp lệ, trả về lỗi
        }

        // Kiểm tra trạng thái giao dịch
        if ("00".equals(responseCode)) {
            // Thanh toán thành công, cập nhật trạng thái đơn hàng
            processSuccessfulTransaction(apptransid); // Cập nhật trạng thái thanh toán của đơn hàng
            return "Transaction successful";
        } else {
            // Thanh toán thất bại, xử lý giao dịch thất bại
            processFailedTransaction(apptransid, responseCode);
            return "Transaction failed";
        }
    }

    // Hàm tính MAC để xác thực callback từ ZaloPay
    private String calculateZaloPayMac(String callbackData) throws Exception {
        // Sử dụng Key2 và HMACSHA256 để tính toán MAC
        String key2 = ZaloPayConfig.KEY2; // Đảm bảo KEY2 được cấu hình chính xác
        return HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, key2, callbackData);
    }

    // Hàm xử lý giao dịch thành công và cập nhật trạng thái đơn hàng
    private void processSuccessfulTransaction(String apptransid) {
        orderService.markOrderAsPaid(apptransid);
        System.out.println("Order " + apptransid + " marked as paid.");
    }

    // Hàm xử lý giao dịch thất bại
    private void processFailedTransaction(String apptransid, String responseCode) {
        System.out.println("Transaction failed with apptransid: " + apptransid + " and response code: " + responseCode);
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
                "<a href=\"http://localhost:3000/login\" class=\"mt-6 inline-block rounded bg-indigo-600 px-5 py-3 text-sm font-medium text-white hover:bg-indigo-700 focus:outline-none focus:ring\">" +
                "Đi đến trang đăng nhập" +
                "</a>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
}
