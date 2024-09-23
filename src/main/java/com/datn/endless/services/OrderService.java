package com.datn.endless.services;

import com.datn.endless.dtos.*;
import com.datn.endless.entities.*;
import com.datn.endless.exceptions.*;
import com.datn.endless.models.OrderDetailModel;
import com.datn.endless.models.OrderModel;
import com.datn.endless.models.OrderModelForUser;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
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


    // Tạo đơn hàng
    public OrderDTO createOrder(OrderModel orderModel) {
        // Validate that userID is not null or empty
        if (orderModel.getUserID() == null || orderModel.getUserID().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }

        User user = userRepository.findById(orderModel.getUserID())
                .orElseThrow(() -> new UserNotFoundException("User with ID " + orderModel.getUserID() + " not found"));

        Useraddress userAddress = userAddressRepository.findByUserIDAndAddressID(user, orderModel.getOrderAddress());

        if(userAddress == null) {
            throw new AddressNotFoundException("Address with ID " + orderModel.getOrderAddress() + " cannot be used");
        }

        Voucher voucher = null;
        if (orderModel.getVoucherID() != null && !orderModel.getVoucherID().isEmpty()) {
            voucher = voucherRepository.findById(orderModel.getVoucherID())
                    .orElseThrow(() -> new VoucherNotFoundException("Voucher with ID " + orderModel.getVoucherID() + " not found"));
        }

        // Validate that orderAddress is not null or empty
        if (orderModel.getOrderAddress() == null || orderModel.getOrderAddress().isEmpty()) {
            throw new IllegalArgumentException("Order address cannot be null or empty");
        }

        Order order = new Order();
        order.setOrderID(UUID.randomUUID().toString()); // Generate ID
        order.setUserID(user);
        order.setVoucherID(voucher);
        order.setOrderDate(LocalDate.now()); // Current date
        order.setOrderAddress(orderModel.getOrderAddress());
        order.setOrderPhone(user.getPhone());
        order.setOrderName(user.getFullname().isEmpty()?user.getUsername():user.getFullname());
        order.setOrderDetails(new ArrayList<>()); // Initialize the orderDetails list
        BigDecimal totalMoney = calculateTotalMoney(orderModel.getOrderDetails(), orderModel.getVoucherID());
        order.setTotalMoney(totalMoney);

        User creater = userRepository.findByUsername(userLoginInformation.getCurrentUser().getUsername());
        order.setCreater(creater);

        // Create and save order details
        for (OrderDetailModel detailModel : orderModel.getOrderDetails()) {
            Orderdetail orderDetail = convertToOrderDetailEntity(detailModel, order);
            order.getOrderDetails().add(orderDetail);
        }

        // Save order with details
        Order savedOrder = orderRepository.save(order);

        // Create order status
        OrderstatusId orderStatusId = new OrderstatusId(savedOrder.getOrderID(), 1); // 1 is the initial status ID
        Orderstatus initialStatus = new Orderstatus();
        initialStatus.setId(orderStatusId);
        initialStatus.setOrder(savedOrder);
        initialStatus.setStatusType(orderstatustypeRepository.findById(1)
                .orElseThrow(() -> new StatusTypeNotFoundException("Order status type not found")));
        initialStatus.setTime(Instant.now());
        orderstatusRepository.save(initialStatus);

        return convertToOrderDTO(savedOrder);
    }

    public OrderDTO createOrderForUser(OrderModelForUser orderModel) {
        // Lấy thông tin người dùng hiện tại từ hệ thống đăng nhập
        User currentUser = userRepository.findByUsername(userLoginInformation.getCurrentUser().getUsername());

        // Validate that orderAddress is not null or empty
        if (orderModel.getOrderAddress() == null || orderModel.getOrderAddress().isEmpty()) {
            throw new IllegalArgumentException("Order address cannot be null or empty");
        }

        Useraddress userAddress = userAddressRepository.findByUserIDAndAddressID(currentUser, orderModel.getOrderAddress());
        if(userAddress == null) {
            throw new AddressNotFoundException("Address with ID " + orderModel.getOrderAddress() + " cannot be used");
        }

        Voucher voucher = null;
        if (orderModel.getVoucherID() != null && !orderModel.getVoucherID().isEmpty()) {
            voucher = voucherRepository.findById(orderModel.getVoucherID())
                    .orElseThrow(() -> new VoucherNotFoundException("Voucher with ID " + orderModel.getVoucherID() + " not found"));
        }

        // Tạo đối tượng Order
        Order order = new Order();
        order.setOrderID(UUID.randomUUID().toString()); // Generate ID
        order.setUserID(currentUser); // Gán người dùng hiện tại vào UserID
        order.setCreater(null);
        order.setVoucherID(voucher);
        order.setOrderDate(LocalDate.now()); // Ngày hiện tại
        order.setOrderAddress(orderModel.getOrderAddress());
        order.setOrderPhone(orderModel.getOrderPhone());
        order.setOrderName(orderModel.getOrderName());
        order.setOrderDetails(new ArrayList<>()); // Khởi tạo danh sách orderDetails

        BigDecimal totalMoney = calculateTotalMoney(orderModel.getOrderDetails(), orderModel.getVoucherID());
        order.setTotalMoney(totalMoney);

        // Tạo và lưu thông tin chi tiết đơn hàng
        for (OrderDetailModel detailModel : orderModel.getOrderDetails()) {
            Orderdetail orderDetail = convertToOrderDetailEntity(detailModel, order);
            order.getOrderDetails().add(orderDetail);
        }

        // Lưu đơn hàng và thông tin chi tiết
        Order savedOrder = orderRepository.save(order);

        // Tạo trạng thái đơn hàng
        OrderstatusId orderStatusId = new OrderstatusId(savedOrder.getOrderID(), 1); // 1 là ID trạng thái ban đầu
        Orderstatus initialStatus = new Orderstatus();
        initialStatus.setId(orderStatusId);
        initialStatus.setOrder(savedOrder);
        initialStatus.setStatusType(orderstatustypeRepository.findById(1)
                .orElseThrow(() -> new StatusTypeNotFoundException("Order status type not found")));
        initialStatus.setTime(Instant.now());
        orderstatusRepository.save(initialStatus);

        return convertToOrderDTO(savedOrder);
    }


    // Lấy thông tin đơn hàng theo ID
    public OrderDTO getOrderDTOById(String id) {
        return orderRepository.findById(id)
                .map(this::convertToOrderDTO)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));
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
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));

        return orderDetailRepository.findByOrderID(order).stream()
                .map(this::convertToOrderDetailDTO)
                .collect(Collectors.toList());
    }

    // Lấy trạng thái đơn hàng theo ID
    public List<OrderStatusDTO> getOrderStatusDTOByOrderId(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));

        return orderstatusRepository.findByOrder_OrderID(order.getOrderID()).stream()
                .map(this::convertToOrderStatusDTO)
                .collect(Collectors.toList());
    }

    // Hủy đơn hàng
    public OrderStatusDTO updateOrderStatus(String orderId, int newStatusId, List<Integer> allowedCurrentStatusIds, int timeLimitInHours, String errorMessage) {
        // Tìm đơn hàng theo ID
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));

        // Lấy trạng thái hiện tại của đơn hàng
        Orderstatus currentStatus = orderstatusRepository.findTopByOrderIdOrderByTimeDesc(orderId)
                .orElseThrow(() -> new StatusTypeNotFoundException("Current status not found"));

        UserDetails userDetails = userLoginInformation.getCurrentUser();

        if(!userDetails.getUsername().equals(order.getUserID().getUsername())) {
            throw new OrderCannotBeUpdateException("This user is not allowed to update this order");
        }

        // Kiểm tra xem trạng thái hiện tại có hợp lệ để chuyển đổi không
        if (!allowedCurrentStatusIds.contains(currentStatus.getStatusType().getId())) {
            throw new InvalidOrderStatusException(errorMessage);
        }

        // Nếu có giới hạn thời gian (ví dụ: 24 giờ), kiểm tra giới hạn này
        if (timeLimitInHours > 0) {
            LocalDateTime orderDateTime = order.getOrderDate().atStartOfDay();
            if (LocalDateTime.now().isAfter(orderDateTime.plusHours(timeLimitInHours))) {
                throw new OrderCancellationNotAllowedException("Order cannot be updated after the allowed time period");
            }
        }

        // Cập nhật trạng thái đơn hàng
        OrderstatusId orderStatusId = new OrderstatusId(orderId, newStatusId);
        Orderstatus newStatus = new Orderstatus();
        newStatus.setId(orderStatusId);
        newStatus.setOrder(order);
        newStatus.setStatusType(orderstatustypeRepository.findById(newStatusId)
                .orElseThrow(() -> new StatusTypeNotFoundException("Order status type not found")));
        newStatus.setTime(Instant.now());
        orderstatusRepository.save(newStatus);

        return convertToOrderStatusDTO(newStatus);
    }

    public OrderStatusDTO cancelOrder(String orderId) {
        return updateOrderStatus(
                orderId,
                -1, // Trạng thái 'Hủy đơn hàng'
                Arrays.asList(1, 2, 6, 7), // Các trạng thái cho phép hủy đơn hàng
                24, // Giới hạn thời gian 24 giờ
                "Order cannot be cancelled as it is already paid, shipping, or delivered"
        );
    }

    public OrderStatusDTO markOrderAsPaid(String orderId) {
        return updateOrderStatus(
                orderId,
                3, // Trạng thái 'Đã thanh toán'
                Arrays.asList(1, 7), // Chỉ cho phép thanh toán ở trạng thái 'Đã đặt hàng chưa thanh toán' và 'Đã xác nhận'
                0, // Không giới hạn thời gian
                "Order cannot be marked as paid in its current state"
        );
    }

    public OrderStatusDTO markOrderAsShipping(String orderId) {
        return updateOrderStatus(
                orderId,
                4, // Trạng thái 'Đang giao hàng'
                Arrays.asList(3), // Chỉ cho phép giao hàng khi đơn đã được thanh toán
                0, // Không giới hạn thời gian
                "Order cannot be marked as shipping if it has not been paid"
        );
    }

    public OrderStatusDTO markOrderAsDelivered(String orderId) {
        return updateOrderStatus(
                orderId,
                5, // Trạng thái 'Đã giao hàng'
                Arrays.asList(4), // Chỉ cho phép giao hàng khi đơn hàng đang ở trạng thái 'Đang giao hàng'
                0, // Không giới hạn thời gian
                "Order cannot be marked as delivered if it is not in shipping state"
        );
    }

    public OrderStatusDTO markOrderAsConfirmed(String orderId) {
        return updateOrderStatus(
                orderId,
                7, // Trạng thái 'Đã xác nhận'
                Arrays.asList(1), // Chỉ cho phép xác nhận đơn hàng ở trạng thái 'Chờ xử lý'
                0, // Không giới hạn thời gian
                "Order cannot be confirmed in its current state"
        );
    }

    public OrderStatusDTO markOrderAsPending(String orderId) {
        return updateOrderStatus(
                orderId,
                6, // Trạng thái 'Chờ xử lý'
                Arrays.asList(7), // Chỉ cho phép đưa đơn hàng về trạng thái 'Chờ xử lý' khi đang ở trạng thái 'Đã xác nhận'
                0, // Không giới hạn thời gian
                "Order cannot be set to pending in its current state"
        );
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
                        .orElseThrow(() -> new VoucherNotFoundException("Voucher with ID " + voucherID + " not found"));

                // Kiểm tra tính hợp lệ của voucher
                if (voucher.getLeastBill().compareTo(totalMoney) > 0) {
                    throw new VoucherCannotBeUsedException("The total order value is not enough to use the voucher!");
                } else if (voucher.getStartDate().isAfter(LocalDate.now()) || voucher.getEndDate().isBefore(LocalDate.now())) {
                    throw new VoucherCannotBeUsedException("The current time is not suitable to use the voucher");
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
            throw new RuntimeException("Error calculating total money: " + e.getMessage(), e);
        }

        return totalMoney;
    }

    // Tính giá giảm cho từng sản phẩm
    private BigDecimal calculateDiscountPrice(String productVersionID) {
        // Lấy giá gốc của sản phẩm
        BigDecimal price = productversionRepository.findById(productVersionID)
                .orElseThrow(() -> new ProductVersionNotFoundException("Product Version not found"))
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
            throw new RuntimeException("Error calculating discount price: " + e.getMessage(), e);
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
        dto.setCreater(order.getCreater() != null ? convertUserToDTO(order.getCreater()) : null);
        dto.setVoucher(order.getVoucherID() != null ? convertVoucherToDTO(order.getVoucherID()) : null);
        dto.setOrderDate(order.getOrderDate());
        dto.setTotalMoney(order.getTotalMoney());
        dto.setOrderAddress(formatAddress(order.getOrderAddress()));
        dto.setOrderPhone(order.getOrderPhone());
        dto.setOrderName(order.getOrderName());

        List<OrderDetailDTO> orderDetailDTOs = order.getOrderDetails() != null ?
                order.getOrderDetails().stream().map(this::convertToOrderDetailDTO).collect(Collectors.toList()) :
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

            Province province = userAddress.getProvinceCode();
            District district = userAddress.getDistrictCode();
            Ward ward = userAddress.getWardCode();

            return String.format("%s, %s, %s, %s",
                    userAddress.getHouseNumberStreet(),
                    ward != null ? ward.getFullName() : "",
                    district != null ? district.getFullName() : "",
                    province != null ? province.getFullName() : "");
        } catch (AddressNotFoundException e) {
            // Ghi lại cảnh báo khi địa chỉ không tìm thấy
            System.err.println(e.getMessage());
            return "Địa chỉ không tìm thấy";
        } catch (Exception e) {
            // Xử lý lỗi khác và ghi lại thông báo lỗi
            e.printStackTrace();
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
                .orElseThrow(() -> new ProductVersionNotFoundException("Product version not found"));
        orderDetail.setOrderDetailID(UUID.randomUUID().toString()); // Generate ID
        orderDetail.setOrderID(savedOrder);
        orderDetail.setProductVersionID(productversion);
        orderDetail.setQuantity(detailModel.getQuantity());
        // Tính giá và giá giảm theo yêu cầu
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