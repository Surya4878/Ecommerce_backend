package com.ecommerce.integration;

import com.ecommerce.dto.*;
import com.ecommerce.entity.PaymentMode;
import com.ecommerce.entity.OrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = AbstractIntegrationTest.Initializer.class)
class CheckoutIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    // Inject JdbcTemplate to promote admin in test DB
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void fullCheckoutFlow() {
        // 1) Register customer
        UserRegisterDto userReg = new UserRegisterDto();
        userReg.setName("it-test-user");
        userReg.setEmail("it-user@example.com");
        userReg.setPassword("TestPass123");

        ResponseEntity<UserDto> regResp = restTemplate.postForEntity("/api/auth/register", userReg, UserDto.class);
        assertThat(regResp.getStatusCode()).isEqualTo(HttpStatus.OK);

        // 2) Register admin candidate
        UserRegisterDto adminReg = new UserRegisterDto();
        adminReg.setName("it-admin");
        adminReg.setEmail("it-admin@example.com");
        adminReg.setPassword("AdminPass123");
        restTemplate.postForEntity("/api/auth/register", adminReg, UserDto.class);

        // Promote admin in the test DB using JdbcTemplate
        jdbcTemplate.update("UPDATE users SET role = 'ROLE_ADMIN' WHERE email = ?", "it-admin@example.com");

        // 3) Login as admin to create product
        UserLoginDto adminLogin = new UserLoginDto();         // <- use UserLoginDto
        adminLogin.setEmail("it-admin@example.com");
        adminLogin.setPassword("AdminPass123");
        ResponseEntity<AuthResponse> adminLoginResp = restTemplate.postForEntity("/api/auth/login", adminLogin, AuthResponse.class);
        assertThat(adminLoginResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        String adminToken = adminLoginResp.getBody().getToken();

        // 4) Create product using admin token
        ProductDto product = new ProductDto();
        product.setName("IT Mouse");
        product.setDescription("for integration test");
        product.setPrice(100.0);
        product.setStock(10);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ProductDto> productReq = new HttpEntity<>(product, headers);
        ResponseEntity<ProductDto> prodResp = restTemplate.exchange("/api/products", HttpMethod.POST, productReq, ProductDto.class);
        assertThat(prodResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        Long productId = prodResp.getBody().getId();

        // 5) Login as customer to get JWT
        UserLoginDto login = new UserLoginDto();              // <- use UserLoginDto
        login.setEmail("it-user@example.com");
        login.setPassword("TestPass123");
        ResponseEntity<AuthResponse> loginResp = restTemplate.postForEntity("/api/auth/login", login, AuthResponse.class);
        assertThat(loginResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        String customerToken = loginResp.getBody().getToken();

        // 6) Add product to cart as customer
        HttpHeaders custHeaders = new HttpHeaders();
        custHeaders.setBearerAuth(customerToken);
        HttpEntity<Void> addReq = new HttpEntity<>(custHeaders);
        ResponseEntity<CartDto> addResp = restTemplate.exchange("/api/cart/add/" + productId + "?quantity=2", HttpMethod.POST, addReq, CartDto.class);
        assertThat(addResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(addResp.getBody().getTotalPrice()).isEqualTo(200.0);

        // 7) Checkout
        CheckoutRequest cr = new CheckoutRequest();
        cr.setPaymentMode(PaymentMode.CREDIT_CARD);
        cr.setSimulateSuccess(true);
        HttpEntity<CheckoutRequest> checkoutReq = new HttpEntity<>(cr, custHeaders);
        ResponseEntity<OrderDto> orderResp = restTemplate.postForEntity("/api/orders/checkout", checkoutReq, OrderDto.class);
        assertThat(orderResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(orderResp.getBody().getOrderStatus()).isEqualTo(OrderStatus.PLACED);
    }
}
