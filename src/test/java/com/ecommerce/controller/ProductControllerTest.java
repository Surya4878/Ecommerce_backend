package com.ecommerce.controller;

import com.ecommerce.dto.ProductDto;
import com.ecommerce.service.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private ProductService productService;

    @Test
    void createProduct_admin_ok() throws Exception {
        ProductDto dto = new ProductDto();
        dto.setId(1L); dto.setName("Mouse"); dto.setPrice(699.0); dto.setStock(50);

        when(productService.create(any(ProductDto.class))).thenReturn(dto);

        String body = """
      {
        "name":"Mouse",
        "description":"desc",
        "price":699.0,
        "stock":50
      }
      """;

        mockMvc.perform(post("/api/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                        // Note: authentication not applied in this slice (would need to mock security filter)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
        verify(productService).create(any(ProductDto.class));
    }
}
