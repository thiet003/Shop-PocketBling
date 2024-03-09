package com.project.shop.services;

import com.project.shop.dtos.OrderDetailDTO;
import com.project.shop.excepsions.DataNotFoundExcepsion;
import com.project.shop.models.Order;
import com.project.shop.models.OrderDetail;
import com.project.shop.models.Product;
import com.project.shop.repositories.OrderDetailRepository;
import com.project.shop.repositories.OrderRepository;
import com.project.shop.repositories.ProductRepository;
import com.project.shop.responses.OrderDetailResponse;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
@AllArgsConstructor
public class OrderDetailService implements  IOrderDetailService{
    private final OrderDetailRepository orderDetailRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    // Themm chi tiet don hang
    @Override
    public OrderDetailResponse createOrderDetail(OrderDetailDTO orderDetailDTO) throws DataNotFoundExcepsion {
        // Kiem tra xem orderId co ton tai hay khong
        Order order = orderRepository.findById(orderDetailDTO.getOrderId())
                .orElseThrow(() -> new DataNotFoundExcepsion("Not found Order have id ="+ orderDetailDTO.getOrderId()));

        // Kiem tra xem productId co ton tai hay khong
        Product product = productRepository.findById(orderDetailDTO.getProductId())
                .orElseThrow(() -> new DataNotFoundExcepsion("Not found Product have id ="+ orderDetailDTO.getProductId()));
        OrderDetail orderDetail = OrderDetail.builder()
                .order(order)
                .product(product)
                .price(orderDetailDTO.getPrice())
                .numberOfProducts(orderDetailDTO.getNumberOfProducts())
                .totalMoney(orderDetailDTO.getTotalMoney())
                .color(orderDetailDTO.getColor())
                .build();
        OrderDetail newOrderDetail =  orderDetailRepository.save(orderDetail);
        return OrderDetailResponse.fromOrderDetail(newOrderDetail);
    }

    @Override
    public OrderDetailResponse getOrderDetail(Long id) throws DataNotFoundExcepsion {
        return OrderDetailResponse.fromOrderDetail(orderDetailRepository.findById(id).orElseThrow(
                () -> new DataNotFoundExcepsion("Not found OrderDetail with id = "+id)
        ));
    }

    @Override
    public OrderDetailResponse updateOrderDetail(Long id, OrderDetailDTO orderDetailDTO) throws DataNotFoundExcepsion {
        // Tim xem orderdetail co ton tai hay khong
        OrderDetail existingOrderDetail = orderDetailRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundExcepsion("Not found OrderDetail with id: "+id));

        Order existingOrder = orderRepository.findById(orderDetailDTO.getOrderId())
                .orElseThrow(() -> new DataNotFoundExcepsion("Not found Order with id: "+orderDetailDTO.getOrderId()));
        Product product = productRepository.findById(orderDetailDTO.getProductId())
                .orElseThrow(() -> new DataNotFoundExcepsion("Not found Product with id: "+orderDetailDTO.getProductId()));

        existingOrderDetail.setPrice(orderDetailDTO.getPrice());
        existingOrderDetail.setNumberOfProducts(orderDetailDTO.getNumberOfProducts());
        existingOrderDetail.setTotalMoney(orderDetailDTO.getTotalMoney());
        existingOrderDetail.setColor(orderDetailDTO.getColor());
        existingOrderDetail.setOrder(existingOrder);
        existingOrderDetail.setProduct(product);

        orderDetailRepository.save(existingOrderDetail);
        return OrderDetailResponse.fromOrderDetail(existingOrderDetail);
    }

    @Override
    public void deleteOrderDetail(Long id) {
        orderDetailRepository.deleteById(id);
    }

    @Override
    public List<OrderDetailResponse> getOrderDetails(Long orderId) {
        List<OrderDetail> orderDetails = orderDetailRepository.findByOrderId(orderId);
        List<OrderDetailResponse> orderDetailResponses = new ArrayList<>();
        for(OrderDetail orderDetail : orderDetails)
        {
            orderDetailResponses.add(OrderDetailResponse.fromOrderDetail(orderDetail));
        }
        return orderDetailResponses;
    }
}
