package com.dqtri.mango.authentication.controller;

import com.dqtri.mango.authentication.exception.ConflictException;
import com.dqtri.mango.authentication.model.CoreUser;
import com.dqtri.mango.authentication.model.dto.PageCriteria;
import com.dqtri.mango.authentication.model.dto.payload.ResetPasswordPayload;
import com.dqtri.mango.authentication.model.dto.payload.UserCreatingPayload;
import com.dqtri.mango.authentication.model.dto.payload.UserUpdatingPayload;
import com.dqtri.mango.authentication.model.enums.Role;
import com.dqtri.mango.authentication.repository.UserRepository;
import com.dqtri.mango.authentication.security.UserPrincipal;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUsers(@RequestParam(required = false) @Valid PageCriteria pageCriteria
    ) {
        Pageable pageable = pageCriteria.toPageable("pk");
        Page<CoreUser> users = userRepository.findAll(pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUser(@PathVariable("userId") Long userId
    ) {
        CoreUser user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User is not found with id: %s", userId)));
        return ResponseEntity.ok(user);
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getMyProfiles(@UserPrincipal User currentUser
    ) {
        //TODO:
        CoreUser byEmail = userRepository.findByEmail(currentUser.getUsername()).orElse(new CoreUser());
        return ResponseEntity.ok(byEmail);
    }

    @PostMapping(value = "", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createUser(@RequestBody @Valid UserCreatingPayload payload) {
        Optional<CoreUser> byEmail = userRepository.findByEmail(payload.getEmail());
        if (byEmail.isPresent()) {
            throw new ConflictException(String.format("%s is already used", payload.getEmail()));
        }
        CoreUser user = new CoreUser();
        user.setEmail(payload.getEmail());
        user.setPassword(passwordEncoder.encode(payload.getPassword()));
        user.setRole(payload.getRole());
        CoreUser saved = userRepository.save(user);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') and hasPermission('#userId', 'nonAdminResource')")
    public ResponseEntity<?> updateUser(@PathVariable("userId") Long userId,
                                        @Valid @RequestBody UserUpdatingPayload payload) {
        CoreUser user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User is not found with id: %s", userId)));
        user.setRole(payload.getRole());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{userId}/password")
    @PreAuthorize("hasRole('ADMIN') and hasPermission('#userId', 'nonAdminResource')")
    public ResponseEntity<?> updateUserPassword(@PathVariable("userId") Long userId,
                                                @Valid @RequestBody ResetPasswordPayload payload) {
        CoreUser user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User is not found with id: %s", userId)));
        user.setPassword(passwordEncoder.encode(payload.getPassword()));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') and hasPermission('#userId', 'nonAdminResource')")
    public ResponseEntity<?> deleteUser(@PathVariable("userId") Long userId) {
        CoreUser user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User is not found with id: %s", userId)));
        user.setRole(Role.NONE);
        return ResponseEntity.ok().build();
    }
}
