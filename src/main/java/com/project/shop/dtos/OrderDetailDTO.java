package com.project.shop.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailDTO {
    @Min(value = 1, message = "OrderId must be > 0")
    @JsonProperty("order_id")
    private Long orderId;

    @Min(value = 1, message = "ProductId must be > 0")
    @JsonProperty("product_id")
    private Long productId;

    @Min(value = 0, message = "Product's price must be >=0")
    private Float price;

    @Min(value = 1, message = "Product's price must be >0")
    @JsonProperty("number_of_products")
    private int numberOfProducts;

    @Min(value = 0, message = "Total price must be >=0")
    @JsonProperty("total_money")
    private Float totalMoney;

    private String color;
}
