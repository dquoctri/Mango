package com.dqtri.mango.core.controller;

import com.dqtri.mango.core.exception.ConflictException;
import com.dqtri.mango.core.model.CoreUser;
import com.dqtri.mango.core.model.dto.PageCriteria;
import com.dqtri.mango.core.model.dto.payload.ResetPasswordPayload;
import com.dqtri.mango.core.model.dto.payload.UserCreatingPayload;
import com.dqtri.mango.core.model.dto.payload.UserUpdatingPayload;
import com.dqtri.mango.core.model.enums.Role;
import com.dqtri.mango.core.repository.UserRepository;
import com.dqtri.mango.core.security.CoreUserDetails;
import com.dqtri.mango.core.security.UserPrincipal;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUsers(@Valid PageCriteria pageCriteria
    ) {
        Pageable pageable = pageCriteria.toPageable("pk");
        Page<CoreUser> users = userRepository.findAll(pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUser(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(getUserOrElseThrow(userId));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getMyProfiles(@UserPrincipal CoreUserDetails currentUser) {
        CoreUser coreUser = currentUser.getCoreUser();
        return ResponseEntity.ok(coreUser);
    }

    @PostMapping(value = "", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createUser(@RequestBody @Valid UserCreatingPayload payload) {
        checkConflictUserEmail(payload.getEmail());
        CoreUser saved = userRepository.save(createCoreUser(payload));
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    private void checkConflictUserEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException(String.format("%s is already in use", email));
        }
    }

    private CoreUser createCoreUser(@NotNull UserCreatingPayload payload) {
        CoreUser user = new CoreUser();
        user.setEmail(payload.getEmail());
        user.setPassword(passwordEncoder.encode(payload.getPassword()));
        user.setRole(payload.getRole());
        return user;
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') and hasPermission('#userId', 'nonAdminResource')")
    public ResponseEntity<?> updateUser(@PathVariable("userId") Long userId,
                                        @Valid @RequestBody UserUpdatingPayload payload) {
        CoreUser user = getUserOrElseThrow(userId);
        user.setRole(payload.getRole());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{userId}/password")
    @PreAuthorize("hasRole('ADMIN') and hasPermission('#userId', 'nonAdminResource')")
    public ResponseEntity<?> updateUserPassword(@PathVariable("userId") Long userId,
                                                @Valid @RequestBody ResetPasswordPayload payload) {
        CoreUser user = getUserOrElseThrow(userId);
        user.setPassword(passwordEncoder.encode(payload.getPassword()));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') and hasPermission('#userId', 'nonAdminResource')")
    public ResponseEntity<?> deleteUser(@PathVariable("userId") Long userId) {
        CoreUser user = getUserOrElseThrow(userId);
        user.setRole(Role.NONE);
        return ResponseEntity.ok().build();
    }

    private CoreUser getUserOrElseThrow(@NotNull Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User is not found with id: %s", id)));
    }
}
