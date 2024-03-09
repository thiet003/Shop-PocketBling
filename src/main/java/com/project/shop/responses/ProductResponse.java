package com.project.shop.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shop.models.BaseEntity;
import com.project.shop.models.Product;
import com.project.shop.models.ProductImage;
import com.project.shop.repositories.ProductImageRepository;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponse extends BaseReponse {
    private Long id;
    private String name;
    private Float price;
    private String thumbnail;
    private String description;

    @JsonProperty("category_id")
    private Long categoryId;

    @JsonProperty("product_images")
    private List<ProductImage> productImages = new ArrayList<>();
    public static ProductResponse fromProduct(Product product)
    {
        ProductResponse productReponse =
                ProductResponse.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .price(product.getPrice())
                        .thumbnail(product.getThumbnail())
                        .description(product.getDescription())
                        .categoryId(product.getCategory().getId())
                        .build();
        productReponse.setCreatedAt(product.getCreatedAt());
        productReponse.setUpdatedAt(product.getUpdatedAt());
        return productReponse;
    }
}
