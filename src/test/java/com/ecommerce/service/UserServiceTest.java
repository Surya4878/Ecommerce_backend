package com.ecommerce.service;

import com.ecommerce.dto.UserRegisterDto;
import com.ecommerce.entity.Role;
import com.ecommerce.entity.User;
import com.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @InjectMocks private UserService userService;
    private ModelMapper modelMapper = new ModelMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository, passwordEncoder, modelMapper);
    }

    @Test
    void register_success() {
        UserRegisterDto dto = new UserRegisterDto();
        dto.setName("Test");
        dto.setEmail("test@example.com");
        dto.setPassword("pass123");

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("hashed");
        User saved = User.builder().id(10L).name("Test").email("test@example.com").password("hashed").role(Role.ROLE_CUSTOMER).build();
        when(userRepository.save(any(User.class))).thenReturn(saved);

        var result = userService.register(dto);

        assertNotNull(result);
        assertEquals(saved.getId(), result.getId());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_duplicateEmailThrows() {
        UserRegisterDto dto = new UserRegisterDto();
        dto.setName("Test");
        dto.setEmail("dup@example.com");
        dto.setPassword("pass123");
        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        var ex = assertThrows(IllegalArgumentException.class, () -> userService.register(dto));
        assertTrue(ex.getMessage().contains("Email"));
        verify(userRepository, never()).save(any());
    }
}
