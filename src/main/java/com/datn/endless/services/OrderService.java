package com.datn.endless.services;

import com.datn.endless.dtos.*;
import com.datn.endless.entities.*;
import com.datn.endless.exceptions.*;
import com.datn.endless.models.NotificationModel;
import com.datn.endless.models.NotificationModelForUser;
import com.datn.endless.models.OrderDetailModel;
import com.datn.endless.models.OrderModel;
import com.datn.endless.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderdetailRepository orderDetailRepository;

    @Autowired
    private UseraddressRepository userAddressRepository;

    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderstatustypeRepository orderstatustypeRepository;

    @Autowired
    private OrderstatusRepository orderstatusRepository;

    @Autowired
    private ProductversionRepository productversionRepository;

    @Autowired
    private PromotionproductRepository promotionproductRepository;

    @Autowired
    UserLoginInfomation userLoginInformation;

    @Autowired
    EntryService purchaseOrderService;

    @Autowired
    private NotificationService notificationService;


    public OrderDTO createOrder(OrderModel orderModel) {
        // Lấy thông tin người dùng hiện tại từ hệ thống đăng nhập
        User currentUser = userRepository.findByUsername(userLoginInformation.getCurrentUser().getUsername());

        // Validate that orderAddress is not null or empty
        if (orderModel.getOrderAddress() == null || orderModel.getOrderAddress().isEmpty()) {
            throw new IllegalArgumentException("Địa chỉ không được bỏ trống");
        }

        Useraddress userAddress = userAddressRepository.findByUserIDAndAddressID(currentUser, orderModel.getOrderAddress());
        if(userAddress == null) {
            throw new AddressNotFoundException("Địa chỉ với mã " + orderModel.getOrderAddress() + " không tìm thấy");
        }

        Voucher voucher = null;
        if (orderModel.getVoucherID() != null && !orderModel.getVoucherID().isEmpty()) {
            voucher = voucherRepository.findById(orderModel.getVoucherID())
                    .orElseThrow(() -> new VoucherNotFoundException("Voucher với mã " + orderModel.getVoucherID() + " không tìm thấy"));
        }

        // Tạo đối tượng Order
        Order order = new Order();
        order.setOrderID(UUID.randomUUID().toString()); // Generate ID
        order.setUserID(currentUser); // Gán người dùng hiện tại vào UserID
        order.setVoucherID(voucher);
        order.setOrderDate(LocalDate.now()); // Ngày hiện tại
        order.setOrderAddress(orderModel.getOrderAddress());
        order.setOrderPhone(orderModel.getOrderPhone());
        order.setOrderName(orderModel.getOrderName());
        order.setOrderdetails(new LinkedHashSet<>()); // Khởi tạo danh sách orderDetails
        order.setShipFee(orderModel.getShipFee());
        order.setCodValue(orderModel.getCodValue());
        order.setInsuranceValue(orderModel.getInsuranceValue());
        order.setServiceTypeID(orderModel.getServiceTypeID());
        BigDecimal totalMoney = calculateTotalMoney(orderModel.getOrderDetails(), orderModel.getVoucherID());
        order.setTotalMoney(totalMoney);

        // Tạo và lưu thông tin chi tiết đơn hàng
        for (OrderDetailModel detailModel : orderModel.getOrderDetails()) {
            if (detailModel.getQuantity()>purchaseOrderService.getProductVersionQuantity(detailModel.getProductVersionID())){
                throw new ProductVersionQuantityException("Số lượng sản phẩm đã chọn vượt quá số lượng trong kho");
            }
            else{
                Orderdetail orderDetail = convertToOrderDetailEntity(detailModel, order);
                order.getOrderdetails().add(orderDetail);
            }
        }

        // Lưu đơn hàng và thông tin chi tiết
        Order savedOrder = orderRepository.save(order);

        // Tạo trạng thái đơn hàng
        OrderstatusId orderStatusId = new OrderstatusId(savedOrder.getOrderID(), 1); // 1 là ID trạng thái ban đầu
        Orderstatus initialStatus = new Orderstatus();
        initialStatus.setId(orderStatusId);
        initialStatus.setOrder(savedOrder);
        initialStatus.setStatusType(orderstatustypeRepository.findById(1)
                .orElseThrow(() -> new StatusTypeNotFoundException("Không tìm thấy hóa đơn")));
        initialStatus.setTime(Instant.now());
        orderstatusRepository.save(initialStatus);
        sendOrderStatusNotification(order.getOrderID(), "Hóa đơn mới", "Một hóa đơn mới mã "+order.getOrderID()+"đã được tạo thành công!");
        return convertToOrderDTO(savedOrder);
    }


    // Lấy thông tin đơn hàng theo ID
    public OrderDTO getOrderDTOById(String id) {
        return orderRepository.findById(id)
                .map(this::convertToOrderDTO)
                .orElseThrow(() -> new OrderNotFoundException("Không tìm thấy hóa đơn"));
    }

    // Lấy tất cả đơn hàng
    public Page<OrderDTO> getAllOrderDTOs(String userID, String orderAddress, String orderPhone, String orderName, Pageable pageable) {
        return getAllOrderDTOS(orderAddress, orderPhone, orderName, pageable, userID);
    }

    public Page<OrderDTO> getAllOrderDTOsByUserLogin(String orderAddress, String orderPhone, String orderName, Pageable pageable) {
        String userID = userRepository.findByUsername(userLoginInformation.getCurrentUsername()).getUserID();
        return getAllOrderDTOS(orderAddress, orderPhone, orderName, pageable, userID);
    }

    private Page<OrderDTO> getAllOrderDTOS(String orderAddress, String orderPhone, String orderName, Pageable pageable, String userID) {
        Page<Order> orders = orderRepository.findAllByUserIDContainingAndOrderAddressContainingAndOrderPhoneContainingAndOrderNameContaining(
                userID != null ? userID : "",
                orderAddress != null ? orderAddress : "",
                orderPhone != null ? orderPhone : "",
                orderName != null ? orderName : "",
                pageable);

        return orders.map(this::convertToOrderDTO);
    }


    // Lấy chi tiết đơn hàng theo ID
    public List<OrderDetailDTO> getOrderDetailsDTOByOrderId(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Không tìm thấy đơn hàng"));

        return orderDetailRepository.findByOrderID(order).stream()
                .map(this::convertToOrderDetailDTO)
                .collect(Collectors.toList());
    }

    // Lấy trạng thái đơn hàng theo ID
    public List<OrderStatusDTO> getOrderStatusDTOByOrderId(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Không tìm thấy đơn hàng"));

        return orderstatusRepository.findByOrder_OrderID(order.getOrderID()).stream()
                .map(this::convertToOrderStatusDTO)
                .collect(Collectors.toList());
    }

    // Hủy đơn hàng
    public OrderStatusDTO updateOrderStatus(String orderId, int newStatusId, List<Integer> allowedCurrentStatusIds, int timeLimitInHours, String errorMessage) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Không tìm thấy đơn hàng"));

        Orderstatus currentStatus = orderstatusRepository.findTopByOrderIdOrderByTimeDesc(orderId)
                .orElseThrow(() -> new StatusTypeNotFoundException("Không tìm thấy trạng thái hiện tại"));

        UserDetails userDetails = userLoginInformation.getCurrentUser();

        if(newStatusId==-1 || newStatusId==1 || newStatusId==6){
            if(!userDetails.getUsername().equals(order.getUserID().getUsername())) {
                throw new OrderCannotBeUpdateException("Người dùng này không có quyền cập nhật đơn hàng này");
            }
        }

        if (!allowedCurrentStatusIds.contains(currentStatus.getStatusType().getId())) {
            throw new InvalidOrderStatusException(errorMessage);
        }

        if (timeLimitInHours > 0) {
            LocalDateTime orderDateTime = order.getOrderDate().atStartOfDay();
            if (LocalDateTime.now().isAfter(orderDateTime.plusHours(timeLimitInHours))) {
                throw new OrderCancellationNotAllowedException("Không thể cập nhật đơn hàng sau khoảng thời gian cho phép");
            }
        }

        OrderstatusId orderStatusId = new OrderstatusId(orderId, newStatusId);
        Orderstatus newStatus = new Orderstatus();
        newStatus.setId(orderStatusId);
        newStatus.setOrder(order);
        newStatus.setStatusType(orderstatustypeRepository.findById(newStatusId)
                .orElseThrow(() -> new StatusTypeNotFoundException("Không tìm thấy loại trạng thái đơn hàng")));
        newStatus.setTime(Instant.now());
        orderstatusRepository.save(newStatus);

        return convertToOrderStatusDTO(newStatus);
    }

    public OrderStatusDTO cancelOrder(String orderId) {
        OrderStatusDTO updatedStatus = updateOrderStatus(
                orderId,
                -1, // Trạng thái 'Hủy đơn hàng'
                Arrays.asList(1, 2), // Các trạng thái cho phép hủy đơn hàng
                72, // Giới hạn thời gian 72 giờ
                "Đơn hàng không thể hủy do đã được thanh toán hoặc đang giao"
        );

        sendOrderStatusNotification(orderId, "Hủy đơn hàng", "Đơn hàng "+orderId+" đã bị hủy.");
        return updatedStatus;
    }

    public OrderStatusDTO markOrderAsPaid(String orderId) {
        OrderStatusDTO updatedStatus = updateOrderStatus(
                orderId,
                3, // Trạng thái 'Đã thanh toán'
                Arrays.asList(1, 2), // Trạng thái cho phép thanh toán
                0, // Không giới hạn thời gian
                "Không thể đặt đơn hàng này thành đã thanh toán"
        );

        sendOrderStatusNotification(orderId, "Đã thanh toán", "Đơn hàng "+orderId+" đã được thanh toán.");
        return updatedStatus;
    }

    public OrderStatusDTO markOrderAsShipping(String orderId) {
        OrderStatusDTO updatedStatus = updateOrderStatus(
                orderId,
                5, // Trạng thái 'Đang giao hàng'
                Arrays.asList(3, 4), // Chỉ cho phép khi đơn đã thanh toán
                0, // Không giới hạn thời gian
                "Không thể đặt đơn hàng này thành đang giao"
        );

        sendOrderStatusNotification(orderId, "Đang giao hàng", "Đơn hàng "+orderId+" đang được giao.");
        return updatedStatus;
    }

    public OrderStatusDTO markOrderAsDelivered(String orderId) {
        OrderStatusDTO updatedStatus = updateOrderStatus(
                orderId,
                6, // Trạng thái 'Đã giao hàng'
                Arrays.asList(5), // Chỉ cho phép khi đơn hàng đang giao
                0, // Không giới hạn thời gian
                "Không thể đặt đơn hàng này thành đã giao"
        );

        sendOrderStatusNotification(orderId, "Đã giao hàng", "Đơn hàng "+orderId+" đã được giao thành công!");
        return updatedStatus;
    }

    public OrderStatusDTO markOrderAsConfirmed(String orderId) {
        OrderStatusDTO updatedStatus = updateOrderStatus(
                orderId,
                4, // Trạng thái 'Đã xác nhận'
                Arrays.asList(1, 3), // Chỉ cho phép xác nhận ở trạng thái chờ xử lý
                0, // Không giới hạn thời gian
                "Không thể xác nhận đơn hàng ở trạng thái hiện tại"
        );

        sendOrderStatusNotification(orderId, "Đã xác nhận", "Đơn hàng "+orderId+" đã được xác nhận.");
        return updatedStatus;
    }

    public OrderStatusDTO markOrderAsPending(String orderId) {
        OrderStatusDTO updatedStatus = updateOrderStatus(
                orderId,
                7, // Trạng thái 'Chờ xử lý'
                Arrays.asList(4), // Chỉ cho phép đưa về trạng thái chờ xử lý khi đang ở trạng thái xác nhận
                0, // Không giới hạn thời gian
                "Không thể đặt đơn hàng này thành chờ xử lý"
        );

        sendOrderStatusNotification(orderId, "Chờ xử lý", "Đơn hàng "+orderId+" đã được đưa về trạng thái chờ xử lý.");
        return updatedStatus;
    }

    private void sendOrderStatusNotification(String orderId, String title, String content) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new StatusTypeNotFoundException("Không tìm thấy loại trạng thái đơn hàng"));
        NotificationModelForUser notificationModel = new NotificationModelForUser();
        notificationModel.setTitle(title);
        notificationModel.setContent(content);
        notificationModel.setUserID(order.getUserID().getUserID());
        notificationService.sendNotificationForOrder(notificationModel);
    }



    // Tính tổng tiền
    private BigDecimal calculateTotalMoney(List<OrderDetailModel> orderDetails, String voucherID) {
        BigDecimal totalMoney = BigDecimal.ZERO;

        try {
            // Tính tổng tiền của tất cả các chi tiết đơn hàng dựa trên giá khuyến mãi
            for (OrderDetailModel detailModel : orderDetails) {
                // Giá sau khuyến mãi cho từng sản phẩm
                BigDecimal price = calculateDiscountPrice(detailModel.getProductVersionID());
                totalMoney = totalMoney.add(price.multiply(BigDecimal.valueOf(detailModel.getQuantity())));
            }

            // Kiểm tra voucher nếu có
            if (voucherID != null && !voucherID.isEmpty()) {
                Voucher voucher = voucherRepository.findById(voucherID)
                        .orElseThrow(() -> new VoucherNotFoundException("Voucher với mã " + voucherID + " không tồn tại"));

                // Kiểm tra tính hợp lệ của voucher
                if (voucher.getLeastBill().compareTo(totalMoney) > 0) {
                    throw new VoucherCannotBeUsedException("Tổng tiền hóa đơn chưa đủ để sử dụng voucher!");
                } else if (voucher.getStartDate().isAfter(LocalDate.now()) || voucher.getEndDate().isBefore(LocalDate.now())) {
                    throw new VoucherCannotBeUsedException("Voucher này đã hết hàn");
                }

                // Tính toán mức giảm giá từ voucher
                BigDecimal discount = BigDecimal.ZERO;
                int level = voucher.getDiscountLevel();

                if (voucher.getDiscountForm().equals("Percent")) {
                    discount = totalMoney.multiply(BigDecimal.valueOf(level)).divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
                } else {
                    discount = BigDecimal.valueOf(level);
                }

                // Điều chỉnh mức giảm giá dựa trên giới hạn của voucher
                if (discount.compareTo(voucher.getLeastDiscount()) < 0) {
                    discount = voucher.getLeastDiscount();
                } else if (discount.compareTo(voucher.getBiggestDiscount()) > 0) {
                    discount = voucher.getBiggestDiscount();
                }

                // Tính tổng tiền cuối cùng sau khi áp dụng voucher
                totalMoney = totalMoney.subtract(discount);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi tính tổng tiền: " + e.getMessage(), e);
        }

        return totalMoney;
    }

    // Tính giá giảm cho từng sản phẩm
    private BigDecimal calculateDiscountPrice(String productVersionID) {
        // Lấy giá gốc của sản phẩm
        BigDecimal price = productversionRepository.findById(productVersionID)
                .orElseThrow(() -> new ProductVersionNotFoundException("Biến thể sản phẩm không tồn tại"))
                .getPrice();

        // Khởi tạo giá giảm
        BigDecimal discountPricePerUnit = price; // Giá gốc là giá khuyến mãi nếu không có khuyến mãi nào áp dụng
        LocalDate now = LocalDate.now();

        try {
            // Lấy danh sách khuyến mãi áp dụng cho sản phẩm
            List<Promotionproduct> promotionProducts = promotionproductRepository.findByProductVersionID(productVersionID);

            boolean hasValidPromotion = false; // Biến đánh dấu xem có khuyến mãi hợp lệ không

            for (Promotionproduct promotionProduct : promotionProducts) {
                Promotiondetail promotionDetail = promotionProduct.getPromotionDetailID();
                Promotion promotion = promotionDetail.getPromotionID();

                // Kiểm tra thời gian khuyến mãi
                LocalDate startDate = promotion.getStartDate();
                LocalDate endDate = promotion.getEndDate();
                if (!now.isBefore(startDate) && !now.isAfter(endDate)) {
                    // Áp dụng khuyến mãi chỉ khi thời gian hiện tại nằm trong khoảng thời gian khuyến mãi
                    BigDecimal percentDiscount = BigDecimal.valueOf(promotionDetail.getPercentDiscount()).divide(BigDecimal.valueOf(100)); // e.g., 10 for 10%

                    // Tính toán giảm giá cho một đơn vị sản phẩm
                    BigDecimal discountAmountPerUnit = percentDiscount.multiply(price);
                    if (discountAmountPerUnit.compareTo(BigDecimal.ZERO) < 0) {
                        discountAmountPerUnit = BigDecimal.ZERO;
                    }
                    discountPricePerUnit = discountPricePerUnit.subtract(discountAmountPerUnit);

                    hasValidPromotion = true; // Đánh dấu đã có khuyến mãi hợp lệ
                }
            }

            // Nếu không có khuyến mãi hợp lệ, giá khuyến mãi bằng giá gốc
            if (!hasValidPromotion) {
                discountPricePerUnit = price;
            }

        } catch (Exception e) {
            // Log lỗi và trả về thông điệp lỗi chi tiết
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi tính tổng tiền: " + e.getMessage(), e);
        }

        // Đảm bảo giá không âm
        if (discountPricePerUnit.compareTo(BigDecimal.ZERO) < 0) {
            discountPricePerUnit = BigDecimal.ZERO;
        }

        return discountPricePerUnit;
    }


    // Chuyển đổi Order thành OrderDTO
    private OrderDTO convertToOrderDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setOrderID(order.getOrderID());

        // Handle potential null values
        dto.setCustomer(order.getUserID() != null ? convertUserToDTO(order.getUserID()) : null);
        dto.setVoucher(order.getVoucherID() != null ? convertVoucherToDTO(order.getVoucherID()) : null);
        dto.setOrderDate(order.getOrderDate());
        dto.setShipFee(order.getShipFee());
        dto.setTotalMoney(order.getTotalMoney());
        dto.setOrderAddress(formatAddress(order.getOrderAddress()));
        dto.setOrderPhone(order.getOrderPhone());
        dto.setOrderName(order.getOrderName());
        dto.setStatus(orderstatusRepository.findTopByOrderIdOrderByTimeDesc(order.getOrderID()).get().getStatusType().getName());
        List<OrderDetailDTO> orderDetailDTOs = order.getOrderdetails() != null ?
                order.getOrderdetails().stream().map(this::convertToOrderDetailDTO).collect(Collectors.toList()) :
                new ArrayList<>();

        dto.setOrderDetails(orderDetailDTOs);

        return dto;
    }


    private String formatAddress(String addressId) {
        if (addressId == null || addressId.isEmpty()) {
            return "Địa chỉ không được cung cấp";
        }

        try {
            Useraddress userAddress = userAddressRepository.findById(addressId)
                    .orElseThrow(() -> new AddressNotFoundException("Địa chỉ với ID " + addressId + " không tìm thấy"));

            return String.format("%s ,%s, %s, %s, %s",
                    userAddress.getDetailAddress(),
                    userAddress.getWardCode(),
                    userAddress.getDistrictID(),
                    userAddress.getProvinceID());
        } catch (AddressNotFoundException e) {
            return "Địa chỉ không tìm thấy";
        } catch (Exception e) {
            return "Lỗi khi lấy địa chỉ";
        }
    }


    // Chuyển đổi Orderdetail thành OrderDetailDTO
    private OrderDetailDTO convertToOrderDetailDTO(Orderdetail orderDetail) {
        OrderDetailDTO dto = new OrderDetailDTO();
        dto.setOrderDetailID(orderDetail.getOrderDetailID());
        dto.setOrderID(orderDetail.getOrderID().getOrderID());
        dto.setProductVersionID(orderDetail.getProductVersionID().getProductVersionID());
        dto.setProductName(orderDetail.getProductVersionID().getProductID().getName()); // Assuming you have a Product entity
        dto.setProductVersionName(orderDetail.getProductVersionID().getVersionName()); // Assuming you have a ProductVersion entity
        dto.setProductVersionImage(orderDetail.getProductVersionID().getImage()); // Assuming you have a ProductVersion entity
        dto.setQuantity(orderDetail.getQuantity());
        dto.setPrice(orderDetail.getPrice());
        dto.setDiscountPrice(orderDetail.getDiscountPrice());
        return dto;
    }

    // Chuyển đổi OrderDetailModel thành Orderdetail entity
    private Orderdetail convertToOrderDetailEntity(OrderDetailModel detailModel, Order savedOrder) {
        Orderdetail orderDetail = new Orderdetail();
        Productversion productversion = productversionRepository.findById(detailModel.getProductVersionID())
                .orElseThrow(() -> new ProductVersionNotFoundException("Biến thể sản phẩm không tồn tại"));
        orderDetail.setOrderDetailID(UUID.randomUUID().toString()); // Generate ID
        orderDetail.setOrderID(savedOrder);
        orderDetail.setProductVersionID(productversion);
        orderDetail.setQuantity(detailModel.getQuantity());
        BigDecimal price = productversion.getPrice();
        orderDetail.setPrice(price);
        orderDetail.setDiscountPrice(price.subtract(calculateDiscountPrice(detailModel.getProductVersionID()))); // Giảm giá cho một đơn vị sản phẩm
        return orderDetail;
    }

    // Chuyển đổi User thành UserOderDTO
    private UserOderDTO convertUserToDTO(User user) {
        UserOderDTO dto = new UserOderDTO();
        dto.setUserID(user.getUserID());
        dto.setUsername(user.getUsername());
        dto.setFullname(user.getFullname());
        dto.setAvatar(user.getAvatar());
        return dto;
    }

    // Chuyển đổi Voucher thành VoucherOrderDTO
    private VoucherOrderDTO convertVoucherToDTO(Voucher voucher) {
        if (voucher == null) return null;
        VoucherOrderDTO dto = new VoucherOrderDTO();
        dto.setVoucherID(voucher.getVoucherID());
        dto.setVoucherCode(voucher.getVoucherCode());
        return dto;
    }

    // Chuyển đổi OrderStatus thành OrderStatusDTO
    private OrderStatusDTO convertToOrderStatusDTO(Orderstatus orderstatus) {
        OrderStatusDTO dto = new OrderStatusDTO();
        dto.setOrderID(orderstatus.getOrder().getOrderID());
        dto.setStatusID(orderstatus.getId().getStatusID());
        dto.setStatusType(orderstatus.getStatusType().getName());
        dto.setTime(orderstatus.getTime());
        return dto;
    }
}