package com.project.shop.services;

import com.project.shop.dtos.CartItemDTO;
import com.project.shop.dtos.OrderDTO;
import com.project.shop.excepsions.DataNotFoundExcepsion;
import com.project.shop.models.*;
import com.project.shop.repositories.OrderDetailRepository;
import com.project.shop.repositories.OrderRepository;
import com.project.shop.repositories.ProductRepository;
import com.project.shop.repositories.UserRepository;
import com.project.shop.responses.OrderResponse;
import com.project.shop.services.IOrderService;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ModelMapper modelMapper;
    @Override
    public OrderResponse createOrder(OrderDTO orderDTO) throws Exception {
        // Tim xem user co ton tai hay k
        User user = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new DataNotFoundExcepsion("Cannot find user by id = "+ orderDTO.getUserId()));;
        //convert
        // Dung mapper
        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));
        Order order = new Order();
        modelMapper.map(orderDTO,order);
        order.setUser(user);
        order.setOrderDate(new Date());
        order.setStatus(OrderStatus.PENDING);
        LocalDate shippingDate = orderDTO.getShippingDate() == null ? LocalDate.now() : orderDTO.getShippingDate();
        if(shippingDate.isBefore(LocalDate.now()))
        {
            throw new DataNotFoundExcepsion("Date must be at least today !");
        }
        order.setShippingDate(shippingDate);
        order.setActive(true);
        order.setTotalMoney(orderDTO.getTotalMoney());
        orderRepository.save(order);
        List<OrderDetail> orderDetails = new ArrayList<>();
        for(CartItemDTO cartItemDTO : orderDTO.getCartItems())
        {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);

            Long productId = cartItemDTO.getProductId();
            int quantity = cartItemDTO.getQuantity();
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new DataNotFoundExcepsion("Product not found with id = "+productId));

            orderDetail.setProduct(product);
            orderDetail.setNumberOfProducts(quantity);
            orderDetail.setPrice(product.getPrice());

            orderDetails.add(orderDetail);
        }
        orderDetailRepository.saveAll(orderDetails);
        modelMapper.typeMap(Order.class, OrderResponse.class);
        return modelMapper.map(order,OrderResponse.class);
    }

    @Override
    public OrderResponse getOrder(Long id) throws DataNotFoundExcepsion {
        modelMapper.typeMap(Order.class, OrderResponse.class);
        Order order = orderRepository.findById(id).orElseThrow(() -> new DataNotFoundExcepsion("Not found order by id = "+id));
        return modelMapper.map(order,OrderResponse.class);
    }

    // Tim kiem don hang theo user_id
    @Override
    public List<OrderResponse> getOrderByUserId(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        modelMapper.typeMap(Order.class, OrderResponse.class);
        List<OrderResponse> orderResponses = new ArrayList<>();
        for(Order order : orders)
        {
            orderResponses.add(modelMapper.map(order,OrderResponse.class));
        }
        return orderResponses;
    }

    @Override
    public OrderResponse updateOrder(Long id, OrderDTO orderDTO) throws DataNotFoundExcepsion {
        Order order = orderRepository.findById(id).orElseThrow(
                () -> new DataNotFoundExcepsion("Not found order by id = "+id)
        );
        // Kiem tra xem co user ton tai k
        User existingUsers = userRepository.findById(orderDTO.getUserId()).orElseThrow(
                () -> new DataNotFoundExcepsion("Not found user by id = "+orderDTO.getUserId())
        );
        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));
        modelMapper.map(orderDTO,order);
        order.setUser(existingUsers);
        orderRepository.save(order);
        modelMapper.typeMap(Order.class, OrderResponse.class);
        return modelMapper.map(order,OrderResponse.class);
    }

    @Override
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order != null)
        {
            order.setActive(false);
            orderRepository.save(order);
        }
    }
    // Tim tat ca
    @Override
    public List<OrderResponse> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        modelMapper.typeMap(Order.class, OrderResponse.class);
        List<OrderResponse> orderResponses = new ArrayList<>();
        for(Order order : orders)
        {
            orderResponses.add(modelMapper.map(order,OrderResponse.class));
        }
        return orderResponses;
    }
}
