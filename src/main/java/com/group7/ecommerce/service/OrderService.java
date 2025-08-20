package com.group7.ecommerce.service;

import java.util.List;

import com.group7.ecommerce.dto.request.UpdateOrderStatusDto;
import com.group7.ecommerce.dto.response.OrderDetailResp;
import com.group7.ecommerce.dto.request.OrderRequestItem;
import com.group7.ecommerce.dto.response.OrderSummaryResp;
import com.group7.ecommerce.entity.Order;
import com.group7.ecommerce.enums.OrderStatus;

public interface OrderService {
	List<OrderSummaryResp> getAllOrderSummaries(String customerName, OrderStatus status);

	OrderDetailResp getOrderDetailById(Integer orderId);

	OrderDetailResp updateOrderStatus(Integer orderId, UpdateOrderStatusDto request);
	/**
	 * Tạo một đơn hàng mới cho người dùng.
	 * <p>
	 * Quy trình xử lý:
	 * <ul>
	 *   <li>Kiểm tra người dùng hợp lệ theo {@code userId}.</li>
	 *   <li>Kiểm tra thông tin giao hàng ({@code shipInfoId}) thuộc về user.</li>
	 *   <li>Xác minh sản phẩm hợp lệ, còn hàng và chưa bị xóa.</li>
	 *   <li>Tính tổng tiền của đơn hàng.</li>
	 *   <li>Tạo bản ghi trong bảng {@code Orders} với trạng thái mặc định <b>pending</b>.</li>
	 *   <li>Thêm chi tiết đơn hàng vào bảng {@code Order_Items}.</li>
	 *   <li>Cập nhật tồn kho sản phẩm.</li>
	 * </ul>
	 * </p>
	 *
	 * @param userId        ID người dùng tạo đơn hàng.
	 * @param shipInfoId    ID thông tin giao hàng của user.
	 * @param paymentMethod Phương thức thanh toán (COD, banking, ...).
	 * @param items         Danh sách sản phẩm và số lượng đặt mua.
	 * @return Đối tượng {@link Order} đã được lưu trong cơ sở dữ liệu.
	 * @throws RuntimeException nếu:
	 * <ul>
	 *   <li>Không tìm thấy user hoặc shipping info.</li>
	 *   <li>Thông tin shipping không thuộc về user.</li>
	 *   <li>Sản phẩm không tồn tại, đã bị xóa hoặc tồn kho không đủ.</li>
	 * </ul>
	 */

	Order createOrder(Long userId, int shipInfoId, String paymentMethod, List<OrderRequestItem> items);
}
