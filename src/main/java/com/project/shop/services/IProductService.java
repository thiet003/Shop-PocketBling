package com.project.shop.services;

import com.project.shop.dtos.ProductDTO;
import com.project.shop.dtos.ProductImageDTO;
import com.project.shop.excepsions.DataNotFoundExcepsion;
import com.project.shop.models.Product;
import com.project.shop.models.ProductImage;
import com.project.shop.responses.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IProductService {
    public Product createProduct(ProductDTO productDTO) throws DataNotFoundExcepsion;
    Product getProductById(long id) throws DataNotFoundExcepsion;
    Page<ProductResponse> getAllProducts(String keyword,Long categoryId,PageRequest pageRequest);
    Product updateProduct(long id, ProductDTO productDTO) throws DataNotFoundExcepsion;
    void deleteProduct(long id);

    List<ProductImage> getProductImageList(Long productId);
    boolean existByName(String name);
    public ProductImage createProductImage(
            Long productId,
            ProductImageDTO productImageDTO
    ) throws Exception;

    public List<Product> findProductByIds(List<Long> productIds);
}
