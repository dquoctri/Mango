package com.dqtri.mango.submission.controller;

import com.dqtri.mango.submission.exception.ConflictException;
import com.dqtri.mango.submission.model.SubmissionUser;
import com.dqtri.mango.submission.model.dto.PageCriteria;
import com.dqtri.mango.submission.model.dto.payload.UserCreatingPayload;
import com.dqtri.mango.submission.model.dto.payload.UserUpdatingPayload;
import com.dqtri.mango.submission.model.enums.Role;
import com.dqtri.mango.submission.repository.UserRepository;
import com.dqtri.mango.submission.security.UserPrincipal;
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
    private final UserRepository userRepository;

    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUsers(@RequestParam(required = false) @Valid PageCriteria pageCriteria
    ) {
        Pageable pageable = pageCriteria.toPageable("pk");
        Page<SubmissionUser> users = userRepository.findAll(pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUser(@PathVariable("userId") Long userId
    ) {
        SubmissionUser user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User is not found with id: %s", userId)));
        return ResponseEntity.ok(user);
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getMyProfiles(@UserPrincipal User currentUser
    ) {
        //TODO:
        SubmissionUser byEmail = userRepository.findByEmail(currentUser.getUsername()).orElse(new SubmissionUser());
        return ResponseEntity.ok(byEmail);
    }

    @PostMapping(value = "", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createUser(@RequestBody @Valid UserCreatingPayload payload) {
        Optional<SubmissionUser> byEmail = userRepository.findByEmail(payload.getEmail());
        if (byEmail.isPresent()) {
            throw new ConflictException(String.format("%s is already used", payload.getEmail()));
        }
        SubmissionUser user = new SubmissionUser();
        user.setEmail(payload.getEmail());
        user.setRole(payload.getRole());
        SubmissionUser saved = userRepository.save(user);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') and hasPermission('#userId', 'nonAdminResource')")
    public ResponseEntity<?> updateUser(@PathVariable("userId") Long userId,
                                        @Valid @RequestBody UserUpdatingPayload payload) {
        SubmissionUser user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User is not found with id: %s", userId)));
        user.setRole(payload.getRole());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') and hasPermission('#userId', 'nonAdminResource')")
    public ResponseEntity<?> deleteUser(@PathVariable("userId") Long userId) {
        SubmissionUser user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User is not found with id: %s", userId)));
        user.setRole(Role.NONE);
        return ResponseEntity.ok().build();
    }
}
