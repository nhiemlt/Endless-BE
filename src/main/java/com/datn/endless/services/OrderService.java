package com.datn.endless.services;

import com.datn.endless.dtos.*;
import com.datn.endless.entities.*;
import com.datn.endless.exceptions.*;
import com.datn.endless.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderdetailRepository orderDetailRepository;

    @Autowired
    private OrderstatusRepository orderStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired
    private OrderstatustypeRepository orderstatustypeRepository;

    @Autowired
    private ProductversionRepository productVersionRepository;

    @Autowired
    private UseraddressRepository userAddressRepository;

    @Autowired
    private PromotionproductRepository promotionproductRepository;

    @Autowired
    private ProductversionRepository productversionRepository;

    // Tạo đơn hàng
    public OrderDTO createOrder(OrderDTO orderDTO) {
        try {
            // Xác thực người dùng
            User user = userRepository.findById(orderDTO.getUserID())
                    .orElseThrow(() -> new UserNotFoundException("User with ID " + orderDTO.getUserID() + " not found"));

            // Xác thực voucher nếu có
            Voucher voucher = null;
            if (orderDTO.getVoucherID() != null) {
                voucher = voucherRepository.findById(orderDTO.getVoucherID())
                        .orElseThrow(() -> new VoucherNotFoundException("Voucher with ID " + orderDTO.getVoucherID() + " not found"));
            }

            // Xác thực địa chỉ người dùng
            Useraddress userAddress = userAddressRepository.findById(orderDTO.getOrderAddress())
                    .orElseThrow(() -> new AddressNotFoundException("Address with ID " + orderDTO.getOrderAddress() + " not found"));

            // Tạo đơn hàng
            Order order = new Order();
            order.setOrderID(orderDTO.getOrderID());
            order.setUserID(user);
            order.setVoucherID(voucher);
            order.setOrderDate(LocalDate.now()); // Ngày hiện tại
            order.setOrderAddress(userAddress.getAddressID());
            order.setOrderPhone(user.getPhone());
            order.setOrderName(user.getFullname().isEmpty() ? user.getUsername() : user.getFullname());
            BigDecimal totalMoney;
            if (order.getVoucherID()!=null){
                totalMoney = calculateTotalMoney(orderDTO.getOrderDetails(), orderDTO.getVoucherID());
            }
            else{
                totalMoney = calculateTotalMoney(orderDTO.getOrderDetails());
            }

            order.setTotalMoney(totalMoney);

            // Khởi tạo danh sách chi tiết đơn hàng nếu chưa có
            order.setOrderDetails(new ArrayList<>());

            // Lưu đơn hàng
            Order savedOrder = orderRepository.save(order);

            // Tạo và lưu các chi tiết đơn hàng
            for (OrderDetailDTO detailDTO : orderDTO.getOrderDetails()) {
                Orderdetail orderDetail = convertToOrderDetailEntity(detailDTO, savedOrder);
                orderDetailRepository.save(orderDetail);
                // Thêm chi tiết đơn hàng vào danh sách
                savedOrder.getOrderDetails().add(orderDetail);
            }

            // Cập nhật đơn hàng với danh sách chi tiết
            orderRepository.save(savedOrder);

            // Tạo trạng thái đơn hàng
            OrderstatusId orderstatusId = new OrderstatusId(savedOrder.getOrderID(), 1); // 1 là ID của trạng thái đơn hàng
            Orderstatus initialStatus = new Orderstatus();
            initialStatus.setId(orderstatusId);
            initialStatus.setOrder(savedOrder);
            initialStatus.setStatusType(orderstatustypeRepository.findById(1)
                    .orElseThrow(() -> new StatusTypeNotFoundException("Order status type not found")));
            initialStatus.setTime(Instant.now());
            orderStatusRepository.save(initialStatus);

            return convertToOrderDTO(savedOrder);

        } catch (Exception e) {
            // Log lỗi và trả về thông điệp lỗi chi tiết
            e.printStackTrace();
            throw new RuntimeException("Error creating order: " + e.getMessage(), e);
        }
    }


    // Tính tổng tiền
    private BigDecimal calculateDiscountPrice(String productVersionID, int quantity) {
        // Khởi tạo giá giảm
        BigDecimal discountPricePerUnit = BigDecimal.ZERO;
        LocalDate now = LocalDate.now();

        // Lấy giá gốc của sản phẩm
        BigDecimal price = productversionRepository.findById(productVersionID)
                .orElseThrow(() -> new ProductVersionNotFoundException("Product Version not found"))
                .getPrice();

        try {
            // Lấy danh sách khuyến mãi áp dụng cho sản phẩm
            List<Promotionproduct> promotionProducts = promotionproductRepository.findByProductVersionID(productVersionID);

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
                    discountPricePerUnit = discountPricePerUnit.add(discountAmountPerUnit);
                }
            }
        } catch (Exception e) {
            // Log lỗi và trả về thông điệp lỗi chi tiết
            e.printStackTrace();
            throw new RuntimeException("Error calculating discount price: " + e.getMessage(), e);
        }

        // Tính toán giá cuối cùng sau giảm giá
        return price.subtract(discountPricePerUnit);
    }

    // Tính tổng tiền
    private BigDecimal calculateTotalMoney(List<OrderDetailDTO> orderDetails, String voucherID) {
        BigDecimal totalMoney = BigDecimal.ZERO;

        try {
            // Tính tổng tiền của tất cả các chi tiết đơn hàng dựa trên giá khuyến mãi
            for (OrderDetailDTO detail : orderDetails) {
                Productversion productVersion = productVersionRepository.findById(detail.getProductVersionID())
                        .orElseThrow(() -> new ProductVersionNotFoundException("Product Version with ID " + detail.getProductVersionID() + " not found"));

                // Giá sau khuyến mãi cho từng sản phẩm
                BigDecimal price = calculateDiscountPrice(detail.getProductVersionID(), detail.getQuantity());
                totalMoney = totalMoney.add(price);
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

    private BigDecimal calculateTotalMoney(List<OrderDetailDTO> orderDetails) {
        BigDecimal totalMoney = BigDecimal.ZERO;

        try {
            // Tính tổng tiền của tất cả các chi tiết đơn hàng dựa trên giá khuyến mãi
            for (OrderDetailDTO detail : orderDetails) {
                Productversion productVersion = productVersionRepository.findById(detail.getProductVersionID())
                        .orElseThrow(() -> new ProductVersionNotFoundException("Product Version with ID " + detail.getProductVersionID() + " not found"));

                // Giá sau khuyến mãi cho từng sản phẩm
                BigDecimal price = calculateDiscountPrice(detail.getProductVersionID(), detail.getQuantity());
                totalMoney = totalMoney.add(price);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error calculating total money: " + e.getMessage(), e);
        }

        return totalMoney;
    }




    public List<OrderDetailDTO> getOrderDetailsDTOByOrderId(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));

        List<Orderdetail> orderDetails = orderDetailRepository.findByOrderID(order);
        return orderDetails != null ?
                orderDetails.stream().map(this::convertToOrderDetailDTO).collect(Collectors.toList()) :
                new ArrayList<>();
    }

    // Chuyển đổi OrderDetailDTO thành Orderdetail entity
    private Orderdetail convertToOrderDetailEntity(OrderDetailDTO dto, Order order) {
        Orderdetail orderDetail = new Orderdetail();
        orderDetail.setOrderID(order);
        orderDetail.setProductVersionID(productVersionRepository.findById(dto.getProductVersionID())
                .orElseThrow(() -> new ProductVersionNotFoundException("Product Version not found")));
        orderDetail.setQuantity(dto.getQuantity());
        orderDetail.setPrice(orderDetail.getProductVersionID().getPrice());
        orderDetail.setDiscountPrice(calculateDiscountPrice(dto.getProductVersionID(), dto.getQuantity()));
        return orderDetail;
    }

    // Lấy OrderDTO theo ID
    public OrderDTO getOrderDTOById(String id) {
        return orderRepository.findById(id)
                .map(this::convertToOrderDTO)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));
    }

    // Lấy danh sách tất cả OrderDTO
    public List<OrderDTO> getAllOrderDTOs() {
        return orderRepository.findAll().stream()
                .map(this::convertToOrderDTO)
                .collect(Collectors.toList());
    }

    // Hủy đơn hàng
    public OrderStatusDTO cancelOrder(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));

        List<OrderStatusDTO> statuses = getOrderStatusDTOByOrderId(orderId);
        OrderStatusDTO lastStatus = statuses.isEmpty() ? null : statuses.get(statuses.size() - 1);

        if (lastStatus == null || lastStatus.getStatusID() != 1) {
            throw new OrderCannotBeCancelledException("Order cannot be cancelled. Current status does not allow cancellation.");
        }

        Orderstatus orderStatus = new Orderstatus();
        orderStatus.setOrder(order);
        orderStatus.setStatusType(orderstatustypeRepository.findById(-1)
                .orElseThrow(() -> new StatusTypeNotFoundException("Order status type not found")));
        orderStatus.setTime(Instant.now());

        Orderstatus savedStatus = orderStatusRepository.save(orderStatus);
        return convertToOrderStatusDTO(savedStatus);
    }

    // Chuyển đổi Order entity thành DTO
    private OrderDTO convertToOrderDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setOrderID(order.getOrderID());
        dto.setUserID(order.getUserID().getUserID());
        dto.setVoucherID(order.getVoucherID() != null ? order.getVoucherID().getVoucherID() : null);
        dto.setOrderDate(order.getOrderDate());
        dto.setTotalMoney(order.getTotalMoney());
        dto.setOrderAddress(order.getOrderAddress());
        dto.setOrderPhone(order.getOrderPhone());
        dto.setOrderName(order.getOrderName());

        // Kiểm tra danh sách null và khởi tạo nếu cần
        List<OrderDetailDTO> orderDetailDTOs = order.getOrderDetails() != null ?
                order.getOrderDetails().stream().map(this::convertToOrderDetailDTO).collect(Collectors.toList()) :
                new ArrayList<>();

        dto.setOrderDetails(orderDetailDTOs);

        return dto;
    }

    private OrderDetailDTO convertToOrderDetailDTO(Orderdetail orderDetail) {
        OrderDetailDTO detailDTO = new OrderDetailDTO();
        detailDTO.setOrderDetailID(orderDetail.getOrderDetailID());
        detailDTO.setOrderID(orderDetail.getOrderID().getOrderID());
        detailDTO.setProductVersionID(orderDetail.getProductVersionID().getProductVersionID());
        detailDTO.setQuantity(orderDetail.getQuantity());
        detailDTO.setPrice(orderDetail.getPrice());
        detailDTO.setDiscountPrice(orderDetail.getDiscountPrice());

        return detailDTO;
    }

    private OrderStatusDTO convertToOrderStatusDTO(Orderstatus orderStatus) {
        OrderStatusDTO statusDTO = new OrderStatusDTO();
        statusDTO.setOrderID(orderStatus.getOrder().getOrderID());
        statusDTO.setStatusID(orderStatus.getId().getStatusID());
        statusDTO.setTime(orderStatus.getTime());
        return statusDTO;
    }

    public List<OrderStatusDTO> getOrderStatusDTOByOrderId(String orderId) {
        return orderStatusRepository.findByOrder_OrderID(orderId).stream()
                .map(this::convertToOrderStatusDTO)
                .collect(Collectors.toList());
    }
}
