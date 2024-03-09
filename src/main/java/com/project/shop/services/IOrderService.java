package com.project.shop.services;

import com.project.shop.dtos.OrderDTO;
import com.project.shop.dtos.ProductDTO;
import com.project.shop.excepsions.DataNotFoundExcepsion;
import com.project.shop.responses.OrderResponse;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface IOrderService {
    OrderResponse createOrder(OrderDTO orderDTO) throws Exception;
    OrderResponse getOrder(Long id) throws Exception;
    List<OrderResponse> getOrderByUserId(Long userId);
    OrderResponse updateOrder(Long id, OrderDTO orderDTO) throws DataNotFoundExcepsion;
    void deleteOrder(Long id);
    List<OrderResponse> getAllOrders();
}
