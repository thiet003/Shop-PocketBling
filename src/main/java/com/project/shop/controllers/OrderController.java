package com.project.shop.controllers;

import com.project.shop.components.LocalizationUtils;
import com.project.shop.dtos.OrderDTO;
import com.project.shop.models.Order;
import com.project.shop.responses.OrderResponse;
import com.project.shop.services.IOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/orders")
@RequiredArgsConstructor
public class OrderController {
    private final IOrderService orderService;
    private final LocalizationUtils localizationUtils;
    //Tao moi order
    @PostMapping("")
    public ResponseEntity<?> createOrder(
            @Valid @RequestBody OrderDTO orderDTO,
            BindingResult result
    ) throws Exception {
        try {
            if (result.hasErrors())
            {
                List<String> errorMessages =  result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            OrderResponse orderResponse =  orderService.createOrder(orderDTO);
            return ResponseEntity.ok(orderResponse);
        }catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    // Xem order theo userId
    @GetMapping("/user/{user_id}")
    public ResponseEntity<?> getOrders(
            @Valid @PathVariable("user_id") Long userId
    ){
        try {
            List<OrderResponse> orderResponses = orderService.getOrderByUserId(userId);
            return ResponseEntity.ok(orderResponses);
        }catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Xem order theo id don hang
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrder(
            @Valid @PathVariable("id") Long id
    ){
        try {
            OrderResponse existingOrder =  orderService.getOrder(id);
            return ResponseEntity.ok(existingOrder);
        }catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("")
    public ResponseEntity<?> getAllOrders(){
        try {
            List<OrderResponse> orderResponses = orderService.getAllOrders();
            return ResponseEntity.ok(orderResponses);
        }catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    // Cap nhat thong tin don hang
    // Cong viec cua admin
    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder(
            @Valid @PathVariable Long id,
            @Valid @RequestBody OrderDTO orderDTO
    ){
        try {
            OrderResponse orderResponse = orderService.updateOrder(id,orderDTO);
            return ResponseEntity.ok(orderResponse);
        }catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    // Xoa don hang
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long id){
        // Xoa mem => Cap nhat truong active = false
        orderService.deleteOrder(id);
        return ResponseEntity.ok("Order deleted successfully!");
    }
}
