package com.oemspring.bookz.controllers;

import com.oemspring.bookz.SpringBookzPro;
import com.oemspring.bookz.models.ERole;
import com.oemspring.bookz.models.Role;
import com.oemspring.bookz.models.User;
import com.oemspring.bookz.models.VerificationToken;
import com.oemspring.bookz.repos.RoleRepository;
import com.oemspring.bookz.repos.UserRepository;
import com.oemspring.bookz.repos.VerificationTokenRepository;
import com.oemspring.bookz.requests.LoginRequest;
import com.oemspring.bookz.requests.SignupRequest;
import com.oemspring.bookz.responses.JwtResponse;
import com.oemspring.bookz.responses.MessageResponse;
import com.oemspring.bookz.security.jwt.JwtUtils;
import com.oemspring.bookz.security.services.UserDetailsImpl;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@SecurityRequirement(name = "bookzapi")

public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;
    @Autowired
    VerificationTokenRepository verificationTokenRepository;
    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());
        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }

    @GetMapping("/verify/{token}")
    public String verify(@PathVariable String token) {
        try {
            User user = verificationTokenRepository.findByToken(token).get().getUser();
            user.setEnabled(true);
            userRepository.save(user);
            SpringBookzPro.logger.info("Aktivasyon Sağlandı");
            return "good";
        } catch (Exception e) {
            SpringBookzPro.logger.info("Aktivasyon Sağlanamadı");
            return "bad";
        }
    }


    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }
        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();
        AtomicReference<Boolean> is_seller = new AtomicReference<>(false);
        is_seller.set(false);
        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);

                        break;
                    case "seller":
                        is_seller.set(true);

                        Role sellerRole = roleRepository.findByName(ERole.ROLE_SELLER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(sellerRole);

                        break;


                    case "mod":
                        Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);

                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        SpringBookzPro.logger.info(is_seller.get().toString() + "-> kayit yapan seller");
        User u;
        if (is_seller.get()) {
            if (!signUpRequest.getBusinessadress().isEmpty()) {
                user.setBusinessAdress(signUpRequest.getBusinessadress());
                u = userRepository.save(user);
            } else return ResponseEntity.ok(new MessageResponse(" businessAdress is required."));

        } else u = userRepository.save(user);


        VerificationToken vt = new VerificationToken();
        vt.setUser(u);
        verificationTokenRepository.save(vt);
        return ResponseEntity.ok(new MessageResponse("User registered successfully! -->>  /verify/" + vt.getToken()));
    }
}
