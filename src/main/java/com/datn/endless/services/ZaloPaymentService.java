package com.datn.endless.services;

import com.datn.endless.dtos.OrderDTO;
import com.datn.endless.dtos.OrderDetailDTO;
import com.datn.endless.entities.Order;
import com.datn.endless.exceptions.DuplicateResourceException;
import com.datn.endless.repositories.OrderRepository;
import com.datn.endless.utils.UUIDUtils;
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

    public String getCurrentTimeString() {
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT+7"));
        SimpleDateFormat fmt = new SimpleDateFormat("HHmmss");
        fmt.setCalendar(cal);

        return fmt.format(cal.getTime());
    }

    // Tạo đơn hàng thanh toán
    public Map<String, Object> createPayment(String order_id) throws Exception {
        OrderDTO order = orderService.getOrderDTOById(order_id);
        if (!order.getStatus().equals("Chờ thanh toán")) {
            throw new DuplicateResourceException("Không thể thanh toán cho hóa đơn này!");
        }

        Map<String, Object> zalopayParams = new HashMap<>();
        zalopayParams.put("appid", ZaloPayConfig.APP_ID);
        zalopayParams.put("apptransid", getCurrentTimeString() + "_" + UUIDUtils.modifyUUID(order.getOrderID()));
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
        // Nếu apptransid chứa UUID có dấu '-', chúng ta cần loại bỏ dấu '-'
        String cleanedApptransid = UUIDUtils.modifyUUID(apptransid);  // Hàm này sẽ loại bỏ dấu '-'

        // Lấy thông tin từ cấu hình
        String appid = ZaloPayConfig.APP_ID;
        String key1 = ZaloPayConfig.KEY1;

        // Tạo dữ liệu cho việc tính toán MAC
        String data = appid + "|" + cleanedApptransid + "|" + key1;  // Sử dụng cleanedApptransid đã loại bỏ dấu '-'
        String mac = HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, key1, data);

        // Tạo map cho các tham số gửi yêu cầu
        Map<String, String> params = new HashMap<>();
        params.put("appid", appid);
        params.put("apptransid", cleanedApptransid);  // Sử dụng cleanedApptransid
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
        // Lấy các tham số từ query string
        String apptransid = payload.get("apptransid");
        String status = payload.get("status");
        String modifiedUUID = apptransid.substring(apptransid.indexOf('_') + 1);
        String orderID = UUIDUtils.decodeModifiedUUID(modifiedUUID);
        System.out.println("\n\n\n\n\n\n\n\n"+apptransid);
        System.out.println(orderID);


        // Kiểm tra trạng thái giao dịch
        if ("1".equals(status)) {
            processSuccessfulTransaction(orderID);
            return "Transaction successful";
        } else {
            return "Transaction failed";
        }
    }

    private String calculateZaloPayMac(String callbackData) throws Exception {
        return HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, ZaloPayConfig.KEY1, callbackData);
    }

    private void processSuccessfulTransaction(String apptransid) {
        orderService.autoMarkOrderAsPaid(apptransid);
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
