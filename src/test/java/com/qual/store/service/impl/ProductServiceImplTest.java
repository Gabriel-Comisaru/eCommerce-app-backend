package com.qual.store.service.impl;

import com.qual.store.converter.ProductConverter;
import com.qual.store.dto.ProductDto;
import com.qual.store.dto.paginated.PaginatedProductResponse;
import com.qual.store.dto.request.ProductRequestDto;
import com.qual.store.exceptions.CategoryNotFoundException;
import com.qual.store.exceptions.DeleteProductException;
import com.qual.store.exceptions.ImageModelException;
import com.qual.store.exceptions.ProductNotFoundException;
import com.qual.store.model.*;
import com.qual.store.repository.*;
import com.qual.store.utils.validators.Validator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private Validator<Product> validator;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ProductConverter productConverter;
    @Mock
    private AppUserRepository appUserRepository;
    @Mock
    private ImageRepository imageRepository;
    @Mock
    private ReviewRepository reviewRepository;
    @InjectMocks
    private ProductServiceImpl productService;

    private AutoCloseable closeable;

    @BeforeEach
    void setup() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @Test
    @Disabled
    public void saveProductCategoryTest() {
        //given
        ProductRequestDto productRequestDto = ProductRequestDto.builder()
                .name("Test Product")
                .description("Test Description")
                .price(10.0)
                .unitsInStock(5L)
                .discountPercentage(0.0)
                .build();
        Long categoryId = 1L;

        Category category = Category.builder().build();
        category.setId(categoryId);

        Product product = Product.builder()
                .name(productRequestDto.getName())
                .description(productRequestDto.getDescription())
                .price(productRequestDto.getPrice())
                .unitsInStock(productRequestDto.getUnitsInStock())
                .discountPercentage(productRequestDto.getDiscountPercentage())
                .category(category)
                .build();

        // when
//        doNothing().when(SecurityContextHolder.getContext().getAuthentication());
        when(productConverter.convertRequestToModel(productRequestDto)).thenReturn(product);
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(appUserRepository.findUserByUsername(anyString())).thenReturn(new AppUser());
        when(imageRepository.save(any(ImageModel.class))).thenReturn(new ImageModel());

        Product savedProduct = productService.saveProductCategory(productRequestDto, categoryId);

        // then
        assertNotNull(savedProduct);
        verify(validator, times(1)).validate(product);
        verify(productRepository, times(1)).save(product);
        verify(imageRepository, times(1)).save(any(ImageModel.class));
    }

    @Test
    public void saveProductCategoryThrowsCategoryNotFoundExceptionTest() {
        // given
        ProductRequestDto productRequestDto = ProductRequestDto.builder()
                .name("Test Product")
                .description("Test Description")
                .price(10.0)
                .unitsInStock(5L)
                .discountPercentage(0.0)
                .build();
        Long categoryId = 1L;

        // when
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // then
        assertThrows(ProductNotFoundException.class, () ->
                productService.saveProductCategory(productRequestDto, categoryId)
        );
        verify(validator, times(1)).validate(any());
        verify(productRepository, times(0)).save(any());
    }

    @Test
    void getAllProductsTest() {
        // given
        Product productOne = Product.builder().name("Product 1").build();
        productOne.setId(1L);

        Product productTwo = Product.builder().name("Product 2").build();
        productTwo.setId(2L);

        List<Product> products = new ArrayList<>();
        products.add(productOne);
        products.add(productTwo);

        // when
        when(productRepository.findAllWithCategoryAndReviewsAndImages()).thenReturn(products);
        when(productConverter.convertModelToDto(any(Product.class))).thenReturn(new ProductDto());

        // then
        List<Product> result = productService.getAllProducts();

        // Verify the result
        assertEquals(2, result.size());
        verify(productRepository, times(1)).findAllWithCategoryAndReviewsAndImages();
    }

    @Test
    void saveProductTest() {
        // given
        Product product = Product.builder().name("Test Product").build();

        // when
        productService.saveProduct(product);

        // then
        verify(validator, times(1)).validate(product);
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void updateProductTest() {
        // given
        Long productId = 1L;
        ProductRequestDto productRequestDto = ProductRequestDto.builder()
                .name("Updated Product")
                .description("Updated Description")
                .price(20.0)
                .unitsInStock(10L)
                .discountPercentage(5.0)
                .build();
        Product product = Product.builder()
                .name(productRequestDto.getName())
                .description(productRequestDto.getDescription())
                .price(productRequestDto.getPrice())
                .unitsInStock(productRequestDto.getUnitsInStock())
                .discountPercentage(productRequestDto.getDiscountPercentage())
                .build();
        product.setId(1L);
        Optional<Product> optionalProduct = Optional.of(product);

        // when
        when(productConverter.convertRequestToModel(productRequestDto)).thenReturn(product);
        when(productRepository.findAllWithCategoryAndReviewsAndImages()).thenReturn(List.of(product));
        when(productRepository.findById(productId)).thenReturn(optionalProduct);
        Product updatedProduct = productService.updateProduct(productId, productRequestDto);

        // then
        assertNotNull(updatedProduct);
        assertEquals(productRequestDto.getName(), updatedProduct.getName());
        assertEquals(productRequestDto.getDescription(), updatedProduct.getDescription());
        assertEquals(productRequestDto.getPrice(), updatedProduct.getPrice());
        assertEquals(productRequestDto.getUnitsInStock(), updatedProduct.getUnitsInStock());
        assertEquals(productRequestDto.getDiscountPercentage(), updatedProduct.getDiscountPercentage());
        verify(validator, times(1)).validate(product);
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).findAllWithCategoryAndReviewsAndImages();
    }

    @Test
    void updateProductThrowsProductNotFoundExceptionTest() {
        // given
        Long productId = 1L;
        ProductRequestDto productRequestDto = ProductRequestDto.builder()
                .name("Updated Product")
                .description("Updated Description")
                .price(20.0)
                .unitsInStock(10L)
                .discountPercentage(5.0)
                .build();

        // when
        when(productRepository.findAllWithCategoryAndReviewsAndImages())
                .thenReturn(new ArrayList<>());

        // then
        assertThrows(ProductNotFoundException.class, () ->
                productService.updateProduct(productId, productRequestDto)
        );
        verify(validator, times(1)).validate(any());
        verify(productRepository, times(0)).findById(any());
        verify(productRepository, times(0)).save(any());
    }

    @Test
    void findProductByIdTest() {
        // given
        Long productId = 1L;
        Product product = Product.builder().name("Test Product").build();
        product.setId(productId);

        // when
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        Product result = productService.findProductById(productId);

        // then
        assertNotNull(result);
        assertEquals(productId, result.getId());
        assertEquals("Test Product", result.getName());
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void findProductByIdThrowsProductNotFoundExceptionTest() {
        // given
        Long productId = 1L;

        // when
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // then
        assertThrows(ProductNotFoundException.class, () ->
                productService.findProductById(productId)
        );
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void deleteProductByIdThrowsProductNotFoundExceptionTest() {
        // given
        Long productId = 1L;

        // when
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // then
        assertThrows(ProductNotFoundException.class,
                () -> productService.deleteProductById(productId));
        verify(productRepository, times(1)).findById(productId);
        verify(categoryRepository, times(0)).findById(productId);
        verify(categoryRepository, times(0)).save(any(Category.class));
        verify(reviewRepository, times(0)).deleteAll(any());
        verify(imageRepository, times(0)).deleteAll(any());
        verify(productRepository, times(0)).deleteById(productId);
    }

    @Test
    void deleteProductByIdThrowsDeleteProductExceptionTest() {
        // given
        Product product = Product.builder()
                .name("Test product name")
                .description("Test product description")
                .price(20)
                .unitsInStock(20)
                .discountPercentage(20)
                .orderItems(new HashSet<>())
                .build();
        Category category = Category.builder().build();
        product.setId(1L);
        category.setId(1L);
        category.setProducts(new ArrayList<>(List.of(product)));
        product.setCategory(category);
        OrderItem orderItem = new OrderItem();
        product.addOrderItem(orderItem);

        // when
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(category)).thenReturn(category);

        // then
        assertThrows(DeleteProductException.class,
                () -> productService.deleteProductById(1L));
        verify(productRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).save(any(Category.class));
        verify(reviewRepository, times(0)).deleteAll(any());
        verify(imageRepository, times(0)).deleteAll(any());
        verify(productRepository, times(0)).deleteById(1L);
    }

    @Test
    void deleteProductByIdTest() {
        // given
        Product product = Product.builder()
                .name("Test product name")
                .description("Test product description")
                .price(20)
                .unitsInStock(20)
                .discountPercentage(20)
                .orderItems(new HashSet<>())
                .reviews(new ArrayList<>())
                .images(new HashSet<>())
                .build();
        Category category = Category.builder().build();
        product.setId(1L);
        category.setId(1L);
        category.setProducts(new ArrayList<>(List.of(product)));
        product.setCategory(category);

        // when
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(category)).thenReturn(category);
        productService.deleteProductById(1L);

        // then
        verify(productRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).save(any(Category.class));
        verify(reviewRepository, times(1)).deleteAll(any());
        verify(imageRepository, times(1)).deleteAll(any());
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    public void getProductsTest() {
        // given
        int pageNumber = 0;
        int pageSize = 10;
        String sortBy = "name";
        Product product = new Product();
        List<Product> products = List.of(product);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Product> productPage = new PageImpl<>(products, pageable, products.size());
        ProductDto productDto = new ProductDto();
        PaginatedProductResponse expectedResponse = PaginatedProductResponse.builder()
                .products(List.of(productDto))
                .numberOfItems((long) products.size())
                .numberOfPages(1)
                .build();

        // when
        when(productRepository.findAll(pageable)).thenReturn(productPage);
        when(productConverter.convertModelToDto(any(Product.class))).thenReturn(productDto);
        PaginatedProductResponse actualResponse = productService.getProducts(pageNumber, pageSize, sortBy);

        // then
        assertEquals(expectedResponse.getProducts(), actualResponse.getProducts());
        assertEquals(expectedResponse.getNumberOfItems(), actualResponse.getNumberOfItems());
        assertEquals(expectedResponse.getNumberOfPages(), actualResponse.getNumberOfPages());

        verify(productRepository, times(1)).findAll(pageable);
        verify(productConverter, times(1)).convertModelToDto(product);
        verifyNoMoreInteractions(productRepository);
    }


    @Test
    public void getProductByIdTest() {
        // given
        Long productId = 1L;
        Product product = new Product();
        product.setId(productId);
        ProductDto expectedDto = new ProductDto();

        // when
        when(productRepository.findAllWithCategoryAndReviewsAndImages()).thenReturn(Collections.singletonList(product));
        when(productConverter.convertModelToDto(product)).thenReturn(expectedDto);
        ProductDto actualDto = productService.getProductById(productId);

        // then
        assertEquals(expectedDto, actualDto);

        verify(productRepository, times(1)).findAllWithCategoryAndReviewsAndImages();
        verify(productConverter, times(1)).convertModelToDto(product);
        verifyNoMoreInteractions(productRepository);
        verifyNoMoreInteractions(productConverter);
    }

    @Test
    public void getProductByIdThrowsProductNotFoundExceptionTest() {
        // given
        Long productId = 1L;

        // when
        when(productRepository.findAllWithCategoryAndReviewsAndImages()).thenReturn(Collections.emptyList());

        // then
        assertThrows(ProductNotFoundException.class, () -> productService.getProductById(productId));

        verify(productRepository, times(1)).findAllWithCategoryAndReviewsAndImages();
        verifyNoMoreInteractions(productRepository);
        verifyNoInteractions(productConverter);
    }

    @Test
    public void findProductsByCategoryTest() {
        // given
        Long categoryId = 1L;
        Category category = Category.builder().build();
        category.setId(categoryId);

        Product product1 = new Product();
        product1.setId(1L);
        product1.setCategory(category);
        Product product2 = new Product();
        product2.setId(2L);
        product2.setCategory(category);
        List<Product> productList = List.of(product1, product2);

        // when
        when(productRepository.findAllWithCategoryAndReviewsAndImages()).thenReturn(productList);
        List<Product> result = productService.findProductsByCategory(categoryId);

        // then
        assertEquals(productList, result);

        verify(productRepository, times(1)).findAllWithCategoryAndReviewsAndImages();
        verifyNoMoreInteractions(productRepository);
    }

    @AfterEach
    public void closeService() throws Exception {
        closeable.close();
    }
}