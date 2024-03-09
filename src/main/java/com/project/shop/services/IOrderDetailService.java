package com.project.shop.services;

import com.project.shop.dtos.OrderDetailDTO;
import com.project.shop.excepsions.DataNotFoundExcepsion;
import com.project.shop.models.OrderDetail;
import com.project.shop.responses.OrderDetailResponse;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface IOrderDetailService {
    OrderDetailResponse createOrderDetail(OrderDetailDTO orderDetailDTO) throws DataNotFoundExcepsion;
    OrderDetailResponse getOrderDetail(Long id) throws DataNotFoundExcepsion;
    OrderDetailResponse updateOrderDetail(Long id, OrderDetailDTO orderDetailDTO) throws DataNotFoundExcepsion;
    void deleteOrderDetail(Long id);
    List<OrderDetailResponse> getOrderDetails(Long orderId);
}
