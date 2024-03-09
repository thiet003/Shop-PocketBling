package com.project.shop.controllers;

import com.project.shop.components.LocalizationUtils;
import com.project.shop.dtos.OrderDTO;
import com.project.shop.dtos.OrderDetailDTO;
import com.project.shop.excepsions.DataNotFoundExcepsion;
import com.project.shop.services.IOrderDetailService;
import com.project.shop.services.OrderDetailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/order_details")
@RequiredArgsConstructor
public class OrderDetailController {
    private final IOrderDetailService orderDetailService;
    private final LocalizationUtils localizationUtils;
    // Them moi 1 order_detail
    @PostMapping("")
    public ResponseEntity<?> createOrderDetail(
            @Valid @RequestBody OrderDetailDTO orderDetailDTO,
            BindingResult result
    ){
        try {
            if (result.hasErrors())
            {
                List<String> errorMessages =  result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }

            return ResponseEntity.ok(orderDetailService.createOrderDetail(orderDetailDTO));
        }catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
    // Lay order detail tu 1 id
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderDetail(
            @Valid @PathVariable("id") Long id
    ){
        try {

            return ResponseEntity.ok(orderDetailService.getOrderDetail(id));
        }catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    //Lay order detail tu 1 orderId
    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getOrderDetails(
            @Valid @PathVariable("orderId") Long orderId
    ){
        try {

            return ResponseEntity.ok(orderDetailService.getOrderDetails(orderId));
        }catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    // Cap nhat thong tin order detail
    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrderDetail(
            @Valid @PathVariable Long id,
            @Valid @RequestBody OrderDetailDTO newOrderDetailDTO
    ) throws DataNotFoundExcepsion {
        orderDetailService.updateOrderDetail(id,newOrderDetailDTO);
        return ResponseEntity.ok("Update orderDetail with id = "+ id);
    }
    // Xoa 1 order detail
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrderDetail(@PathVariable Long id){
        orderDetailService.deleteOrderDetail(id);
        return ResponseEntity.ok("OrderDetail deleted successfully!");
    }

}
