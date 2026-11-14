package com.linearappmaker.authentication;

import com.linearappmaker.users.User;
import com.linearappmaker.users.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.Locale;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
 public record RegisterRequest(@NotBlank @Email @Size(max=320) String email,@NotBlank @Size(min=12,max=72) String password,@NotBlank @Size(max=80) String displayName){}
 public record LoginRequest(@NotBlank @Email String email,@NotBlank String password){}
 public record UserResponse(UUID id,String email,String displayName){}
 private final UserRepository users; private final PasswordEncoder encoder; private final AuthenticationManager manager; private final SecurityContextRepository contexts;
 public AuthenticationController(UserRepository users,PasswordEncoder encoder,AuthenticationManager manager,SecurityContextRepository contexts){this.users=users;this.encoder=encoder;this.manager=manager;this.contexts=contexts;}
 @PostMapping("/register") @ResponseStatus(HttpStatus.CREATED)
 public UserResponse register(@Valid @RequestBody RegisterRequest r){String email=r.email().trim().toLowerCase(Locale.ROOT);if(users.existsByEmail(email))throw new ResponseStatusException(HttpStatus.CONFLICT,"Account already exists");String name=r.displayName().trim();if(name.isEmpty())throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Display name cannot be blank");return dto(users.save(new User(email,encoder.encode(r.password()),name)));}
 @PostMapping("/login") public UserResponse login(@Valid @RequestBody LoginRequest r,HttpServletRequest request,HttpServletResponse response){Authentication auth=manager.authenticate(new UsernamePasswordAuthenticationToken(r.email().trim().toLowerCase(Locale.ROOT),r.password()));SecurityContext context=SecurityContextHolder.createEmptyContext();context.setAuthentication(auth);SecurityContextHolder.setContext(context);contexts.saveContext(context,request,response);return users.findByEmail(auth.getName()).map(this::dto).orElseThrow();}
 @PostMapping("/logout") @ResponseStatus(HttpStatus.NO_CONTENT) public void logout(HttpServletRequest request){var session=request.getSession(false);if(session!=null)session.invalidate();SecurityContextHolder.clearContext();}
 @GetMapping("/me") public UserResponse me(Authentication auth){return users.findByEmail(auth.getName()).map(this::dto).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));}
 private UserResponse dto(User u){return new UserResponse(u.getId(),u.getEmail(),u.getDisplayName());}
}
