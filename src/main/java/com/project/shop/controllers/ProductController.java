package com.project.shop.controllers;

import com.github.javafaker.Faker;
import com.project.shop.components.LocalizationUtils;
import com.project.shop.dtos.CategoryDTO;
import com.project.shop.dtos.ProductDTO;
import com.project.shop.dtos.ProductImageDTO;
import com.project.shop.excepsions.DataNotFoundExcepsion;
import com.project.shop.models.Product;
import com.project.shop.models.ProductImage;
import com.project.shop.responses.ProductListResponse;
import com.project.shop.responses.ProductResponse;
import com.project.shop.services.IProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/products")
public class ProductController {
    private final IProductService productService;
    private final LocalizationUtils localizationUtils;
    // Hiển thị tất cả các products, phân trang
    @GetMapping("")
    public ResponseEntity<ProductListResponse> getProducts(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0",name = "category_id")  Long categoryId,
            @RequestParam("page") int page,
            @RequestParam("limit") int limit
    ){
        PageRequest pageRequest = PageRequest.of(page,limit,
                Sort.by("id").ascending());
        Page<ProductResponse> productPage = productService.getAllProducts(keyword,categoryId,pageRequest);
        // Laays tong so trang
        int totalPages = productPage.getTotalPages();
        List<ProductResponse> products =  productPage.getContent();

        return ResponseEntity.ok(ProductListResponse.builder()
                        .products(products)
                        .totalPages(totalPages)
                .build());
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id){
        try {
            Product existingProduct = productService.getProductById(id);
            List<ProductImage> productImages = productService.getProductImageList(id);
            ProductResponse productResponse = ProductResponse.fromProduct(existingProduct);
            productResponse.setProductImages(productImages);
            return ResponseEntity.ok(productResponse);
        }catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    // Thêm sản phẩm
    @PostMapping(value = "")
    public ResponseEntity<?> insertProduct(
            @Valid @RequestBody ProductDTO productDTO,
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
            Product newProduct =  productService.createProduct(productDTO);

            return ResponseEntity.ok(newProduct);

        }catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
    @PostMapping(value = "uploads/{id}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImages(
            @PathVariable("id") Long productId,
            @ModelAttribute("files") List<MultipartFile> files
    ){
        try {
            Product existingProduct = productService.getProductById(productId);
            files = files == null ? new ArrayList<MultipartFile>() : files;
            if(files.size()>ProductImage.MAXIMUM_IMAGES_PER_PRODUCT)
            {
                return ResponseEntity.badRequest().body("You can only upload max" + ProductImage.MAXIMUM_IMAGES_PER_PRODUCT + " images");
            }
            List<ProductImage> productImages = new ArrayList<>();
            for(MultipartFile file : files)
            {
                if(file.getSize()==0) continue;
                // Kiểm tra kích thước file và định dạng
                if(file.getSize()>10 * 1024 * 1024) // Kích thước >10MB
                {
                    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body("File is to large!");
                }
                // Kiểm tra xem đúng định dạng file ảnh hay không
                String contentType = file.getContentType();
                if(contentType == null || !contentType.startsWith("image/"))
                {
                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("File must be an image!");
                }
                // Luu file va cap nhat thumbnail trong DTO

                String filename = storeFile(file);
                // Luu vao doi tuong product trong DB
                ProductImage productImage = productService.createProductImage(
                        existingProduct.getId(), ProductImageDTO
                        .builder()
                        .imageUrl(filename)
                        .build()
                );
                productImages.add(productImage);
            }
            return ResponseEntity.ok().body(productImages);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    private boolean isImageFile(MultipartFile file)
    {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }
    private  String storeFile(MultipartFile file) throws IOException {
        if(!isImageFile(file) || file.getOriginalFilename()==null)
        {
            throw new IOException("Invalid image format");
        }
        String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        // Thêm UUID vào trước tên file để đảm bảo tên file là duy nhất
        String uniqueFileName = UUID.randomUUID().toString() + "_" + filename;
        Path uploadDir = Paths.get("uploads");
        //Kiểm tra và tạo thư mục nếu nó chưa tồn tại
        if(!Files.exists(uploadDir))
        {
            Files.createDirectories(uploadDir);
        }
        // Duong dan day du vao file
        Path destination = Paths.get(uploadDir.toString(),uniqueFileName);
        // Sao chep file vao thu muc dich
        Files.copy(file.getInputStream(),destination, StandardCopyOption.REPLACE_EXISTING);
        return uniqueFileName;
    }
    // Xem anh
    @GetMapping("/images/{imageName}")
    public ResponseEntity<?> viewImage(@PathVariable String imageName)
    {
        try{
            Path imagePath = Paths.get("uploads/"+imageName);
            UrlResource resource = new UrlResource(imagePath.toUri());

            if(resource.exists())
            {
                return ResponseEntity.ok().
                        contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            }
            else{
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(new UrlResource(Paths.get("uploads/notfound.png").toUri()));
            }

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }


    // Sửa sản phẩm
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductDTO productDTO
    ){
        try {
            Product updatedProduct = productService.getProductById(id);
            return ResponseEntity.ok(updatedProduct);
        }catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    // Xoa san pham
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id){
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok("Delete product with id = "+id);
        }catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

//    @PostMapping("/generateFakeProducts")
    public ResponseEntity<String> generateFakeProducts() {
        try {
            Faker faker = new Faker();
            for(int i=0;i<1000000;i++)
            {
                String name = faker.commerce().productName();
                if(productService.existByName(name))
                {
                    continue;
                }
                ProductDTO productDTO = ProductDTO.builder()
                        .name(name)
                        .price((float)(faker.number().numberBetween(1,1000000)))
                        .description(faker.lorem().sentence())
                        .categoryId((long) faker.number().numberBetween(3,4))
                        .build();
                productService.createProduct(productDTO);
            }
        }catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok("Fake Products created successfully!");
    }

    @GetMapping("/by-ids")
    public ResponseEntity<?> getProductByIds(@RequestParam("ids") String ids)
    {
        try {
            List<Long> productIds = Arrays.stream(ids.split(","))
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
            List<Product> products = productService.findProductByIds(productIds);
            return ResponseEntity.ok(products);
        }catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
