package com.ecommerce.controller;

import com.ecommerce.dto.UserDto;
import com.ecommerce.dto.UserRegisterDto;
import com.ecommerce.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final ModelMapper modelMapper;

    public UserController(UserService userService, ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    // Register (duplicate of /api/auth/register but available here if desired)
    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody UserRegisterDto dto) {
        return ResponseEntity.ok(userService.register(dto));
    }

    // Get profile (owner or admin)
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        // allow if admin or same user - check in service/controller layer
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            var principal = auth.getPrincipal();
            // admin allowed
            if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                return ResponseEntity.ok(userService.getById(id));
            } else {
                // if same user
                Long currentUserId = ((com.ecommerce.security.UserDetailsImpl) principal).getId();
                if (currentUserId.equals(id)) {
                    return ResponseEntity.ok(userService.getById(id));
                }
            }
        }
        return ResponseEntity.status(403).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateProfile(@PathVariable Long id, @RequestBody UserDto dto) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            var principal = auth.getPrincipal();
            if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))
                    || ((com.ecommerce.security.UserDetailsImpl) principal).getId().equals(id)) {
                return ResponseEntity.ok(userService.updateProfile(id, dto));
            }
        }
        return ResponseEntity.status(403).build();
    }

    @PutMapping("/{id}/change-password")
    public ResponseEntity<?> changePassword(@PathVariable Long id,
                                            @RequestParam String oldPassword,
                                            @RequestParam String newPassword) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            var principal = auth.getPrincipal();
            if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))
                    || ((com.ecommerce.security.UserDetailsImpl) principal).getId().equals(id)) {
                userService.changePassword(id, oldPassword, newPassword);
                return ResponseEntity.ok().build();
            }
        }
        return ResponseEntity.status(403).build();
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<?> listUsers() {
        return ResponseEntity.ok(userService.listAll());
    }
}
