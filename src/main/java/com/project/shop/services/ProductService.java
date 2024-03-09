package com.project.shop.services;

import com.project.shop.dtos.ProductDTO;
import com.project.shop.dtos.ProductImageDTO;
import com.project.shop.excepsions.DataNotFoundExcepsion;
import com.project.shop.excepsions.InvalidParamExcepsion;
import com.project.shop.models.Category;
import com.project.shop.models.Product;
import com.project.shop.models.ProductImage;
import com.project.shop.repositories.CategoryRepository;
import com.project.shop.repositories.ProductImageRepository;
import com.project.shop.repositories.ProductRepository;
import com.project.shop.responses.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService{
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;
    @Override
    @Transactional
    public Product createProduct(ProductDTO productDTO) throws DataNotFoundExcepsion {
        Category existCategory =  categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new DataNotFoundExcepsion(
                        "Cannot find category with id = "+ productDTO.getCategoryId()));
        Product newProduct = Product.builder()
                .name(productDTO.getName())
                .price(productDTO.getPrice())
                .description(productDTO.getDescription())
                .thumbnail(productDTO.getThumbnail())
                .category(existCategory)
                .build();
        productRepository.save(newProduct);
        return newProduct;
    }

    @Override
    public Product getProductById(long id) throws DataNotFoundExcepsion {
        return productRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundExcepsion(
                        "Cannot find category with id = "+id));
    }

    @Override
    public Page<ProductResponse> getAllProducts(String keyword,Long categoryId, PageRequest pageRequest) {
        // Lay san pham theo page va limit
        Page<Product> productPage;
        productPage = productRepository.searchProducts(categoryId,keyword,pageRequest);
        return productPage.map(ProductResponse::fromProduct);

    }

    @Override
    @Transactional
    public Product updateProduct(
            long id,
            ProductDTO productDTO
    )
            throws DataNotFoundExcepsion {
        Product existingProduct = getProductById(id);
        if(existingProduct != null)
        {
            existingProduct.setName(productDTO.getName());
            Category existCategory =  categoryRepository.findById(productDTO.getCategoryId())
                    .orElseThrow(() -> new DataNotFoundExcepsion(
                            "Cannot find category with id = "+ productDTO.getCategoryId()));
            existingProduct.setCategory(existCategory);
            existingProduct.setPrice(productDTO.getPrice());
            existingProduct.setDescription(productDTO.getDescription());
            existingProduct.setThumbnail(productDTO.getThumbnail());
            return productRepository.save(existingProduct);
        }
        return null;
    }

    @Override
    @Transactional
    public void deleteProduct(long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        optionalProduct.ifPresent(productRepository::delete);
    }

    @Override
    public List<ProductImage> getProductImageList(Long productId) {
        return productImageRepository.findByProductId(productId);
    }

    @Override
    public boolean existByName(String name) {
        return productRepository.existsByName(name);
    }

    @Override
    public ProductImage createProductImage(
            Long productId,
            ProductImageDTO productImageDTO
    ) throws Exception {
        Product existProduct=  productRepository.findById(productId)
                .orElseThrow(() -> new DataNotFoundExcepsion(
                        "Cannot find product with id = "+ productImageDTO.getProductId()));

        ProductImage newProductImage = ProductImage.builder()
                .product(existProduct)
                .imageUrl(productImageDTO.getImageUrl())
                .build();
        // Khong cho them qua 5 anh
        int size = productImageRepository.findByProductId(productId).size();
        if(size >= ProductImage.MAXIMUM_IMAGES_PER_PRODUCT)
        {
            throw new InvalidParamExcepsion("Number of images must be <= " + ProductImage.MAXIMUM_IMAGES_PER_PRODUCT);
        }
        return productImageRepository.save(newProductImage);
    }

    @Override
    public List<Product> findProductByIds(List<Long> productIds) {
        return productRepository.findProductByIds(productIds);
    }
}
