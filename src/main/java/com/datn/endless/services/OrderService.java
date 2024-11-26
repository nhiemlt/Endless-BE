package com.datn.endless.services;

import com.datn.endless.dtos.*;
import com.datn.endless.entities.*;
import com.datn.endless.exceptions.*;
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
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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
    private UservoucherRepository uservoucherRepository;

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

    @Autowired
    private CartService cartService;

    // Tính tổng tiền
    private BigDecimal calculateTotalMoney(List<OrderDetailModel> orderDetails, Voucher voucher) {
        BigDecimal totalMoney = orderDetails.stream()
                .map(detail -> calculateDiscountPrice(detail.getProductVersionID()).multiply(BigDecimal.valueOf(detail.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Áp dụng giảm giá từ voucher nếu có
        if (voucher != null) {
            validateVoucherUsage(voucher, totalMoney);

            BigDecimal discount = calculateVoucherDiscount(voucher);
            totalMoney = totalMoney.subtract(discount);
        }

        return totalMoney;
    }

    private BigDecimal calculateDiscountPrice(String productVersionID) {
        // Bước 1: Lấy thông tin của phiên bản sản phẩm từ database
        Productversion productVersion = productversionRepository.findById(productVersionID)
                .orElseThrow(() -> new ProductVersionNotFoundException("Không tìm thấy phiên bản sản phẩm"));

        // Bước 2: Lấy thông tin khuyến mãi áp dụng cho sản phẩm này trong thời gian hiện tại
        List<Promotionproduct> promotionproducts = promotionproductRepository.findByProductVersionIDAndPromotionStartDateBeforeAndPromotionEndDateAfter(
                productVersion.getProductVersionID(), Instant.now());

        if (promotionproducts.isEmpty()) {
            return productVersion.getPrice();  // Không có khuyến mãi, trả về giá gốc
        }

        // Bước 3: Lấy khuyến mãi hợp lệ đầu tiên
        Promotion validPromotion = null;
        for (Promotionproduct promotionproduct : promotionproducts) {
            Promotion promotion = promotionproduct.getPromotionID();

            // Kiểm tra xem khuyến mãi có đang trong thời gian hiệu lực hay không
            if (isPromotionActive(promotion)) {
                validPromotion = promotion;
                break;  // Dừng lại khi tìm thấy khuyến mãi hợp lệ đầu tiên
            }
        }

        // Nếu không có khuyến mãi hợp lệ, trả về giá gốc
        if (validPromotion == null) {
            return productVersion.getPrice();
        }

        // Bước 4: Tính toán giá sau khi giảm
        BigDecimal originalPrice = productVersion.getPrice();  // Lấy giá gốc của sản phẩm
        BigDecimal discountPercent = BigDecimal.valueOf(validPromotion.getPercentDiscount());
        BigDecimal discountAmount = originalPrice.multiply(discountPercent).divide(BigDecimal.valueOf(100));
        BigDecimal discountedPrice = originalPrice.subtract(discountAmount);

        // Bước 5: Trả về giá sau giảm
        return discountedPrice.setScale(2, RoundingMode.HALF_UP);  // Làm tròn đến 2 chữ số thập phân
    }

    // Hàm kiểm tra xem khuyến mãi có đang trong thời gian hiệu lực hay không
    private boolean isPromotionActive(Promotion promotion) {
        Instant now = Instant.now();
        return !promotion.getStartDate().isAfter(now) && !promotion.getEndDate().isBefore(now);
    }


    // Phương thức kiểm tra điều kiện áp dụng voucher
    private void validateVoucherUsage(Voucher voucher, BigDecimal totalMoney) {
        if (voucher.getLeastBill().compareTo(totalMoney) > 0) {
            throw new VoucherCannotBeUsedException("Tổng tiền hóa đơn chưa đủ để sử dụng voucher!");
        }
        if (voucher.getStartDate().isAfter(LocalDate.now()) || voucher.getEndDate().isBefore(LocalDate.now())) {
            throw new VoucherCannotBeUsedException("Voucher này đã hết hạn");
        }
    }

    // Phương thức tính toán voucher discount
    private BigDecimal calculateVoucherDiscount(Voucher voucher) {
        BigDecimal discount = BigDecimal.valueOf(voucher.getDiscountLevel());
        return discount.max(voucher.getLeastDiscount()).min(voucher.getBiggestDiscount());
    }

    public OrderDTO createOrder(OrderModel orderModel) {
        User currentUser = userRepository.findByUsername(userLoginInformation.getCurrentUser().getUsername());

        // Kiểm tra địa chỉ
        if (orderModel.getOrderAddress() == null || orderModel.getOrderAddress().isEmpty()) {
            throw new IllegalArgumentException("Địa chỉ không được bỏ trống");
        }

        Useraddress userAddress = userAddressRepository.findByUserIDAndAddressID(currentUser, orderModel.getOrderAddress());
        if (userAddress == null) {
            throw new AddressNotFoundException("Địa chỉ với mã " + orderModel.getOrderAddress() + " không tìm thấy");
        }

        // Kiểm tra Voucher nếu có
        Voucher voucher = orderModel.getVoucherID() != null && !orderModel.getVoucherID().isEmpty()
                ? voucherRepository.findById(orderModel.getVoucherID()).orElse(null) : null;

        // Tính tổng tiền
        BigDecimal totalMoney = calculateTotalMoney(orderModel.getOrderDetails(), voucher);
        totalMoney = totalMoney.add(orderModel.getShipFee());

        // Tạo mới Order
        Order order = new Order();
        order.setOrderID(UUID.randomUUID().toString());
        order.setUserID(currentUser);
        order.setVoucherID(voucher);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderAddress(orderModel.getOrderAddress());
        order.setOrderPhone(orderModel.getOrderPhone());
        order.setOrderName(orderModel.getOrderName());
        order.setOrderdetails(new LinkedHashSet<>());
        order.setShipFee(orderModel.getShipFee());
        order.setCodValue(orderModel.getCodValue());
        order.setInsuranceValue(orderModel.getInsuranceValue());
        order.setServiceTypeID(orderModel.getServiceTypeID());
        if(voucher!=null){{
            Uservoucher uservoucher = uservoucherRepository.findByUserIDAndVoucherID(currentUser, order.getVoucherID());
            if(uservoucher==null){
                throw new VoucherCannotBeUsedException("Không tìm thấy voucher của người dùng");
            }
            else{
                order.setVoucherDiscount(calculateVoucherDiscount(voucher));
                uservoucherRepository.delete(uservoucher);
            }

        }}
        order.setTotalMoney(totalMoney);

        // Kiểm tra và thêm chi tiết đơn hàng
        if (orderModel.getOrderDetails() != null && !orderModel.getOrderDetails().isEmpty()) {
            for (OrderDetailModel detailModel : orderModel.getOrderDetails()) {
                if (detailModel.getQuantity() > purchaseOrderService.getProductVersionQuantity(detailModel.getProductVersionID())) {
                    throw new ProductVersionQuantityException("Số lượng sản phẩm đã chọn vượt quá số lượng trong kho");
                } else {
                    Orderdetail orderDetail = convertToOrderDetailEntity(detailModel, order);
                    order.getOrderdetails().add(orderDetail);  // Thêm vào Set orderdetails
                    try{
                        cartService.deleteCartItem(orderDetail.getProductVersionID().getProductVersionID());
                    }
                    catch(Exception e) {
                        System.out.print("");
                    }
                }
            }
        } else {
            throw new IllegalArgumentException("Danh sách chi tiết đơn hàng không được trống");
        }

        // Lưu đơn hàng
        Order savedOrder = orderRepository.save(order);
        saveOrderStatus(savedOrder);

        sendOrderStatusNotification(order.getOrderID(), "Hóa đơn mới", "Một hóa đơn mới mã " + order.getOrderID() + " đã được tạo thành công!");

        return convertToOrderDTO(savedOrder);
    }

    // Phương thức lưu trạng thái của hóa đơn
    private void saveOrderStatus(Order order) {
        OrderstatusId orderStatusId = new OrderstatusId(order.getOrderID(), 1);
        Orderstatus initialStatus = new Orderstatus();
        initialStatus.setId(orderStatusId);
        initialStatus.setOrder(order);
        initialStatus.setStatusType(orderstatustypeRepository.findById(1)
                .orElseThrow(() -> new StatusTypeNotFoundException("Không tìm thấy hóa đơn")));
        initialStatus.setTime(Instant.now());
        orderstatusRepository.save(initialStatus);
    }

    public OrderDTO createOrderVNPay(OrderModel orderModel) {
        User currentUser = userRepository.findByUsername(userLoginInformation.getCurrentUser().getUsername());

        // Kiểm tra địa chỉ
        if (orderModel.getOrderAddress() == null || orderModel.getOrderAddress().isEmpty()) {
            throw new IllegalArgumentException("Địa chỉ không được bỏ trống");
        }

        Useraddress userAddress = userAddressRepository.findByUserIDAndAddressID(currentUser, orderModel.getOrderAddress());
        if (userAddress == null) {
            throw new AddressNotFoundException("Địa chỉ với mã " + orderModel.getOrderAddress() + " không tìm thấy");
        }

        // Kiểm tra Voucher nếu có
        Voucher voucher = orderModel.getVoucherID() != null && !orderModel.getVoucherID().isEmpty()
                ? voucherRepository.findById(orderModel.getVoucherID()).orElse(null) : null;

        // Tính tổng tiền
        BigDecimal totalMoney = calculateTotalMoney(orderModel.getOrderDetails(), voucher);
        totalMoney = totalMoney.add(orderModel.getShipFee());

        // Tạo mới Order
        Order order = new Order();
        order.setOrderID(UUID.randomUUID().toString());
        order.setUserID(currentUser);
        order.setVoucherID(voucher);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderAddress(orderModel.getOrderAddress());
        order.setOrderPhone(orderModel.getOrderPhone());
        order.setOrderName(orderModel.getOrderName());
        order.setOrderdetails(new LinkedHashSet<>());
        order.setShipFee(orderModel.getShipFee());
        order.setCodValue(orderModel.getCodValue());
        order.setInsuranceValue(orderModel.getInsuranceValue());
        order.setServiceTypeID(orderModel.getServiceTypeID());
        order.setVoucherDiscount(voucher != null ? calculateVoucherDiscount(voucher) : BigDecimal.ZERO);
        order.setTotalMoney(totalMoney);

        // Kiểm tra và thêm chi tiết đơn hàng
        if (orderModel.getOrderDetails() != null && !orderModel.getOrderDetails().isEmpty()) {
            for (OrderDetailModel detailModel : orderModel.getOrderDetails()) {
                if (detailModel.getQuantity() > purchaseOrderService.getProductVersionQuantity(detailModel.getProductVersionID())) {
                    throw new ProductVersionQuantityException("Số lượng sản phẩm đã chọn vượt quá số lượng trong kho");
                } else {
                    Orderdetail orderDetail = convertToOrderDetailEntity(detailModel, order);
                    order.getOrderdetails().add(orderDetail);  // Thêm vào Set orderdetails
                    try{
                        cartService.deleteCartItem(orderDetail.getProductVersionID().getProductVersionID());
                    }
                    catch(Exception e) {
                        System.out.print("");
                    }
                }
            }
        } else {
            throw new IllegalArgumentException("Danh sách chi tiết đơn hàng không được trống");
        }

        // Lưu đơn hàng
        Order savedOrder = orderRepository.save(order);
        saveOrderVNPayStatus(savedOrder);

        LocalDateTime now = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy:HH-mm-ss");

        String formattedDate = now.format(formatter);
        sendOrderStatusNotification(order.getOrderID(), "Hóa đơn mới", "Một hóa đơn mới mã đã được tạo thành công vào"+formattedDate);

        return convertToOrderDTO(savedOrder);
    }

    // Phương thức lưu trạng thái của hóa đơn
    private void saveOrderVNPayStatus(Order order) {
        OrderstatusId orderStatusId = new OrderstatusId(order.getOrderID(), 1);
        Orderstatus initialStatus = new Orderstatus();
        initialStatus.setId(orderStatusId);
        initialStatus.setOrder(order);
        initialStatus.setStatusType(orderstatustypeRepository.findById(2)
                .orElseThrow(() -> new StatusTypeNotFoundException("Không tìm thấy hóa đơn")));
        initialStatus.setTime(Instant.now());
        orderstatusRepository.save(initialStatus);
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

    public OrderStatusDTO updateOrderStatus(String orderId, int newStatusId, List<Integer> allowedCurrentStatusIds, int timeLimitInHours, String errorMessage) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Không tìm thấy đơn hàng"));

        Orderstatus currentStatus = orderstatusRepository.findTopByOrderIdOrderByTimeDesc(orderId)
                .orElseThrow(() -> new StatusTypeNotFoundException("Không tìm thấy trạng thái hiện tại"));

        UserDetails userDetails = userLoginInformation.getCurrentUser();

        if(newStatusId==7 || newStatusId==1 || newStatusId==6){
            if(!userDetails.getUsername().equals(order.getUserID().getUsername())) {
                throw new OrderCannotBeUpdateException("Người dùng này không có quyền cập nhật đơn hàng này");
            }
        }

        if (!allowedCurrentStatusIds.contains(currentStatus.getStatusType().getId())) {
            throw new InvalidOrderStatusException(errorMessage);
        }

        if (timeLimitInHours > 0) {
            LocalDateTime orderDateTime = order.getOrderDate(); // Sử dụng trực tiếp LocalDateTime nếu đã chứa thời gian
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

    // Hủy đơn hàng
    public void autoUpdateOrderStatus(String orderId, int newStatusId, List<Integer> allowedCurrentStatusIds, String errorMessage) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Không tìm thấy đơn hàng"));

        Orderstatus currentStatus = orderstatusRepository.findTopByOrderIdOrderByTimeDesc(orderId)
                .orElseThrow(() -> new StatusTypeNotFoundException("Không tìm thấy trạng thái hiện tại"));

        if (!allowedCurrentStatusIds.contains(currentStatus.getStatusType().getId())) {
            throw new InvalidOrderStatusException(errorMessage);
        }


        OrderstatusId orderStatusId = new OrderstatusId(orderId, newStatusId);
        Orderstatus newStatus = new Orderstatus();
        newStatus.setId(orderStatusId);
        newStatus.setOrder(order);
        newStatus.setStatusType(orderstatustypeRepository.findById(newStatusId)
                .orElseThrow(() -> new StatusTypeNotFoundException("Không tìm thấy loại trạng thái đơn hàng")));
        newStatus.setTime(Instant.now());
        orderstatusRepository.save(newStatus);
    }

    public OrderStatusDTO cancelOrder(String orderId) {
        OrderStatusDTO updatedStatus = updateOrderStatus(
                orderId,
                7, // Trạng thái 'Hủy đơn hàng'
                Arrays.asList(1, 2), // Các trạng thái cho phép hủy đơn hàng
                72, // Giới hạn thời gian 72 giờ
                "Đơn hàng không thể hủy do đã được thanh toán hoặc đang giao"
        );

        sendOrderStatusNotification(orderId, "Hủy đơn hàng", "Đơn hàng "+orderId+" đã bị hủy.");
        return updatedStatus;
    }

    public void cancelOrderUnpair(String orderId) {
        autoUpdateOrderStatus(
                orderId,
                7, // Trạng thái 'Hủy đơn hàng'
                Arrays.asList(1, 2), // Các trạng thái cho phép hủy đơn hàng
                "Đơn hàng không thể hủy do đã được thanh toán hoặc đang giao"
        );

        sendOrderStatusNotification(orderId, "Hủy đơn hàng", "Đơn hàng "+orderId+" đã bị hủy do quá hạn thanh toán");
    }

    public void cancelOrderUnconfirm(String orderId) {
        autoUpdateOrderStatus(
                orderId,
                7, // Trạng thái 'Hủy đơn hàng'
                Arrays.asList(1, 2), // Các trạng thái cho phép hủy đơn hàng
                "Đơn hàng không thể hủy do đã được thanh toán hoặc đang giao"
        );

        sendOrderStatusNotification(orderId, "Hủy đơn hàng", "Đơn hàng "+orderId+" đã bị hủy do quá hạn xác nhận");
    }

    public void cancelUnpaidOrdersBefore() {
        List<Order> allOrder = orderRepository.findAll();

        for (Order order : allOrder) {
            Orderstatus orderstatus = orderstatusRepository.findTopByOrderIdOrderByTimeDesc(order.getOrderID())
                    .orElseThrow(() -> new StatusTypeNotFoundException("Không tìm thấy trạng thái hiện tại"));

            if (orderstatus.getStatusType().getId() == 2 &&
                    orderstatus.getTime().isBefore(Instant.now().minus(Duration.ofMinutes(15)))) {
                cancelOrderUnpair(order.getOrderID());
            }
        }
    }

    public void cancelWaitToConfirmOrdersBefore() {
        List<Order> allOrder = orderRepository.findAll();

        for (Order order : allOrder) {
            Orderstatus orderstatus = orderstatusRepository.findTopByOrderIdOrderByTimeDesc(order.getOrderID())
                    .orElseThrow(() -> new StatusTypeNotFoundException("Không tìm thấy trạng thái hiện tại"));
            if (orderstatus.getStatusType().getId() == 1 &&
                    orderstatus.getTime().isBefore(Instant.now().minus(Duration.ofDays(7)))) {
                cancelOrderUnconfirm(order.getOrderID());
            }
        }
    }

    public OrderStatusDTO markOrderAsPaid(String orderId) {
        OrderStatusDTO updatedStatus = updateOrderStatus(
                orderId,
                3, // Trạng thái 'Đã thanh toán'
                Arrays.asList(1, 2),
                0,
                "Không thể đặt đơn hàng này thành đã thanh toán"
        );

        sendOrderStatusNotification(orderId, "Đã thanh toán", "Đơn hàng "+orderId+" đã được thanh toán.");
        return updatedStatus;
    }

    public void autoMarkOrderAsPaid(String orderId) {
        autoUpdateOrderStatus(
                orderId,
                3, // Trạng thái 'Đã thanh toán'
                Arrays.asList(1, 2),
                "Không thể đặt đơn hàng này thành đã thanh toán"
        );

        sendOrderStatusNotification(orderId, "Đã thanh toán", "Đơn hàng "+orderId+" đã được thanh toán.");
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



        // Chuyển đổi Order thành OrderDTO
    private OrderDTO convertToOrderDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setOrderID(order.getOrderID());

        // Handle potential null values
        dto.setCustomer(order.getUserID() != null ? convertUserToDTO(order.getUserID()) : null);
        dto.setVoucher(order.getVoucherID()!=null ? order.getVoucherID().getVoucherCode() : null);
        dto.setVoucherDiscount(order.getVoucherDiscount()==null ? BigDecimal.ZERO : order.getVoucherDiscount());
        dto.setOrderDate(order.getOrderDate());
        dto.setShipFee(order.getShipFee());
        dto.setTotalMoney(order.getTotalMoney());
        dto.setOrderAddress(formatAddress(order.getOrderAddress()));
        dto.setOrderPhone(order.getOrderPhone());
        dto.setOrderName(order.getOrderName());
        dto.setInsuranceValue(order.getInsuranceValue());
        dto.setServiceTypeID(order.getServiceTypeID());
        dto.setStatus(orderstatusRepository.findTopByOrderIdOrderByTimeDesc(order.getOrderID()).get().getStatusType().getName());
        List<OrderDetailDTO> orderDetailDTOs = order.getOrderdetails() != null ?
                order.getOrderdetails().stream().map(this::convertToOrderDetailDTO).collect(Collectors.toList()) :
                new ArrayList<>();
        dto.setOrderDetails(orderDetailDTOs);
        BigDecimal totalProduct = BigDecimal.ZERO;
        for (OrderDetailDTO detail : orderDetailDTOs) {
            totalProduct = totalProduct.add(detail.getDiscountPrice() == null ? detail.getPrice() : detail.getDiscountPrice());
        }
        dto.setTotalProductPrice(totalProduct);
        dto.setCodValue(order.getCodValue());
        BigDecimal money = totalProduct.add(order.getShipFee());
        dto.setMoney(money);

        return dto;
    }


    private String formatAddress(String addressId) {
        if (addressId == null || addressId.isEmpty()) {
            return "Địa chỉ không được cung cấp";
        }

        try {
            Useraddress userAddress = userAddressRepository.findById(addressId)
                    .orElseThrow(() -> new AddressNotFoundException("Địa chỉ với ID " + addressId + " không tìm thấy"));

            return String.format("%s , %s, %s, %s",
                    userAddress.getDetailAddress(),
                    userAddress.getWardName(),
                    userAddress.getDistrictName(),
                    userAddress.getProvinceName());
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
        dto.setDiscountPrice(orderDetail.getDiscountPrice().doubleValue() == 0 ? orderDetail.getPrice() : orderDetail.getDiscountPrice());
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