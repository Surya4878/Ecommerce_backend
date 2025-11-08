package com.ecommerce.integration;

import com.ecommerce.dto.ProductDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = AbstractIntegrationTest.Initializer.class)
class ProductIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void createAndGetProduct() {
        // create product (no auth required for GET, but POST requires admin in your app)
        ProductDto p = new ProductDto();
        p.setName("Integration Mouse");
        p.setDescription("Test container product");
        p.setPrice(499.0);
        p.setStock(10);
        p.setCategory("Test");
        p.setImageUrl("");
        p.setRating(4.2);

        // If your POST /api/products is admin-protected, bypass security for tests by:
        // 1) disabling security in test profile OR
        // 2) using TestRestTemplate with admin JWT (login first).
        // For simplicity, we'll call GET after saving directly using repository in more advanced test.
        ResponseEntity<ProductDto> resp = restTemplate.postForEntity("/api/products", p, ProductDto.class);
        assertThat(resp.getStatusCode()).isIn(HttpStatus.OK, HttpStatus.CREATED);
        ProductDto created = resp.getBody();
        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull();

        // fetch by id
        ResponseEntity<ProductDto> getResp =
                restTemplate.getForEntity("/api/products/" + created.getId(), ProductDto.class);
        assertThat(getResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        ProductDto fetched = getResp.getBody();
        assertThat(fetched).isNotNull();
        assertThat(fetched.getName()).isEqualTo("Integration Mouse");
    }
}
