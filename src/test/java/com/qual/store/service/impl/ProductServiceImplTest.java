package com.qual.store.service.impl;

import com.qual.store.converter.ProductConverter;
import com.qual.store.dto.ProductDto;
import com.qual.store.dto.paginated.PaginatedProductResponse;
import com.qual.store.dto.request.ProductRequestDto;
import com.qual.store.exceptions.DeleteProductException;
import com.qual.store.exceptions.ProductNotFoundException;
import com.qual.store.model.*;
import com.qual.store.repository.*;
import com.qual.store.utils.validators.Validator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

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

        Authentication authentication = new UsernamePasswordAuthenticationToken(new AppUser(), new Object());
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // when
        when(productConverter.convertRequestToModel(productRequestDto)).thenReturn(product);
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(appUserRepository.findUserByUsername(anyString())).thenReturn(new AppUser());
        when(imageRepository.save(any(ImageModel.class))).thenReturn(new ImageModel());

        productService.saveProductCategory(productRequestDto, categoryId);

        // then
        verify(validator, times(1)).validate(product);
        verify(productRepository, times(1)).save(product);
        verify(imageRepository, times(0)).save(any(ImageModel.class));
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

        assertEquals(2, result.size());
        verify(productRepository, times(1)).findAllWithCategoryAndReviewsAndImages();
    }

    @Test
    void getAllProductsByDiscount() {
        // given
        Product productOne = Product.builder().name("Product 1").discountPercentage(0.0).build();
        productOne.setId(1L);

        Product productTwo = Product.builder().name("Product 2").discountPercentage(20.0).build();
        productTwo.setId(2L);

        List<Product> products = new ArrayList<>();
        products.add(productOne);
        products.add(productTwo);

        // when
        when(productRepository.findAllWithCategoryAndReviewsAndImages()).thenReturn(products);
        when(productConverter.convertModelToDto(any(Product.class))).thenReturn(new ProductDto());

        // then
        List<Product> result = productService.getAllProductsByDiscount();

        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getId());
        verify(productRepository, times(1)).findAllWithCategoryAndReviewsAndImages();
    }

    @Test
    void getAllProductsByPriceRange() {
        // given
        double minPrice = 10.0;
        double maxPrice = 20.0;

        Product productOne = Product.builder().name("Product 1").price(5.0).build();
        productOne.setId(1L);

        Product productTwo = Product.builder().name("Product 2").price(15.0).build();
        productTwo.setId(2L);

        Product productThree = Product.builder().name("Product 3").price(25.0).build();
        productThree.setId(3L);

        List<Product> products = new ArrayList<>();
        products.add(productOne);
        products.add(productTwo);
        products.add(productThree);

        // when
        when(productRepository.findAllWithCategoryAndReviewsAndImages()).thenReturn(products);
        when(productConverter.convertModelToDto(any(Product.class))).thenReturn(new ProductDto());

        // then
        List<Product> result = productService.getAllProductsByPriceRange(minPrice, maxPrice);

        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getId());
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

    @Test
    public void addToFavoritesTest() {
        // given
        Long productId = 123L;
        Product product = Product.builder()
                .favoriteByUsers(new HashSet<>())
                .build();
        product.setId(productId);

        String currentUsername = "testUser";
        AppUser appUser = AppUser.builder()
                .username(currentUsername)
                .favoriteProducts(new HashSet<>())
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(new AppUser(), new Object());
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // when
        when(appUserRepository.findUserByUsername(anyString())).thenReturn(appUser);
        when(productRepository.findAllWithCategoryAndReviewsAndImages()).thenReturn(Collections.singletonList(product));
        when(appUserRepository.save(appUser)).thenReturn(appUser);

        productService.addToFavorites(productId);

        // then
        assertEquals(1, appUser.getFavoriteProducts().size());
        verify(appUserRepository, times(1)).findUserByUsername(anyString());
        verify(productRepository, times(1)).findAllWithCategoryAndReviewsAndImages();
        verify(appUserRepository, times(1)).save(any(AppUser.class));
        verifyNoMoreInteractions(appUserRepository);
        verifyNoMoreInteractions(productRepository);
    }


    @Test
    public void removeFromFavoritesTest() {
        // given
        Long productId = 123L;
        Product product = Product.builder()
                .favoriteByUsers(new HashSet<>())
                .build();
        product.setId(productId);

        String currentUsername = "testUser";
        AppUser appUser = AppUser.builder()
                .username(currentUsername)
                .favoriteProducts(new HashSet<>())
                .build();
        appUser.addFavoriteProduct(product);

        Authentication authentication = new UsernamePasswordAuthenticationToken(new AppUser(), new Object());
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // when
        when(appUserRepository.findUserByUsername(anyString())).thenReturn(appUser);
        when(productRepository.findAllWithCategoryAndReviewsAndImages()).thenReturn(Collections.singletonList(product));
        when(appUserRepository.save(appUser)).thenReturn(appUser);

        productService.removeFromFavorites(productId);

        // then
        assertEquals(0, appUser.getFavoriteProducts().size());
        verify(appUserRepository, times(1)).findUserByUsername(anyString());
        verify(productRepository, times(1)).findAllWithCategoryAndReviewsAndImages();
        verify(appUserRepository, times(1)).save(any(AppUser.class));
        verifyNoMoreInteractions(appUserRepository);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    public void getFavProductsByLoggedInUserTest() {
        // given
        Long productId1 = 123L;
        Long productId2 = 456L;

        Product product1 = new Product();
        product1.setId(productId1);

        Product product2 = new Product();
        product2.setId(productId2);

        String currentUsername = "testUser";
        AppUser appUser = AppUser.builder()
                .username(currentUsername)
                .favoriteProducts(new HashSet<>())
                .build();
        appUser.addFavoriteProduct(product1);
        appUser.addFavoriteProduct(product2);

        ProductDto productDto = ProductDto.builder()
                .favUserIds(List.of(1L))
                .build();
        productDto.setId(1L);

        Authentication authentication = new UsernamePasswordAuthenticationToken(new AppUser(), new Object());
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // when
        when(appUserRepository.findUserByUsername(anyString())).thenReturn(appUser);
        when(productRepository.findAllWithCategoryAndReviewsAndImages()).thenReturn(Arrays.asList(product1, product2));
        when(productConverter.convertModelToDto(any(Product.class))).thenReturn(productDto);

        productService.getFavProductsByLoggedInUser();

        // then
        verify(appUserRepository, times(1)).findUserByUsername(anyString());
        verify(productRepository, times(1)).findAllWithCategoryAndReviewsAndImages();
        verifyNoMoreInteractions(appUserRepository);
        verifyNoMoreInteractions(productRepository);
        verifyNoMoreInteractions(productConverter);
    }

    @Test
    public void searchProductByName_MatchingProductsTest() {
        // given
        String searchQuery = "Test";
        int pageNumber = 0;
        int pageSize = 10;
        String sortBy = "id";

        Product product1 = Product.builder().name("Test Product 1").build();
        product1.setId(1L);
        Product product2 = Product.builder().name("Another Test Product").build();
        product2.setId(2L);

        List<Product> productList = Arrays.asList(product1, product2);
        Page<Product> productPage = new PageImpl<>(productList);

        when(productRepository
                .findAllByNameContainingIgnoreCase(searchQuery, PageRequest.of(pageNumber, pageSize, Sort.by(sortBy))))
                .thenReturn(productPage);

        ProductDto productDto1 = ProductDto.builder().name("Test Product 1").build();
        productDto1.setId(1L);
        ProductDto productDto2 = ProductDto.builder().name("Another Test Product").build();
        productDto2.setId(2L);

        when(productConverter.convertModelToDto(product1)).thenReturn(productDto1);
        when(productConverter.convertModelToDto(product2)).thenReturn(productDto2);

        // when
        PaginatedProductResponse result =
                productService.searchProductByName(searchQuery, pageNumber, pageSize, sortBy);

        // then
        assertEquals(2, result.getProducts().size());
        assertEquals(productDto1.getName(), result.getProducts().get(0).getName());
        assertEquals(productDto2.getName(), result.getProducts().get(1).getName());
    }

    @Test
    void searchProductByName_NoMatchingProductsTest() {
        // given
        String searchQuery = "XYZ";
        int pageNumber = 0;
        int pageSize = 10;
        String sortBy = "id";

        List<Product> productList = Collections.emptyList();
        Page<Product> productPage = new PageImpl<>(productList);

        when(productRepository
                .findAllByNameContainingIgnoreCase(searchQuery, PageRequest.of(pageNumber, pageSize, Sort.by(sortBy))))
                .thenReturn(productPage);

        // when
        PaginatedProductResponse result =
                productService.searchProductByName(searchQuery, pageNumber, pageSize, sortBy);

        // then
        assertEquals(0, result.getProducts().size());
    }

    @Test
    void searchProductByName_EmptyQueryTest() {
        // given
        String searchQuery = "";
        int pageNumber = 0;
        int pageSize = 10;
        String sortBy = "id";

        Product product1 = Product.builder().name("Test Product 1").build();
        product1.setId(1L);
        Product product2 = Product.builder().name("Another Test Product").build();
        product2.setId(2L);

        List<Product> productList = Arrays.asList(product1, product2);
        Page<Product> productPage = new PageImpl<>(productList);

        when(productRepository.findAllByNameContainingIgnoreCase(searchQuery, PageRequest.of(pageNumber, pageSize, Sort.by(sortBy))))
                .thenReturn(productPage);

        ProductDto productDto1 = ProductDto.builder().name("Test Product 1").build();
        productDto1.setId(1L);
        ProductDto productDto2 = ProductDto.builder().name("Another Test Product").build();
        productDto2.setId(2L);

        when(productConverter.convertModelToDto(product1)).thenReturn(productDto1);
        when(productConverter.convertModelToDto(product2)).thenReturn(productDto2);

        // when
        PaginatedProductResponse result = productService.searchProductByName(searchQuery, pageNumber, pageSize, sortBy);

        // then
        assertEquals(2, result.getProducts().size());
        assertEquals(productDto1.getName(), result.getProducts().get(0).getName());
        assertEquals(productDto2.getName(), result.getProducts().get(1).getName());
    }

    @AfterEach
    public void closeService() throws Exception {
        closeable.close();
    }
}