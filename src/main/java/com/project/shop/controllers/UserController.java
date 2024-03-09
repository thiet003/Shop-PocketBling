package com.project.shop.controllers;

import com.project.shop.dtos.UserDTO;
import com.project.shop.dtos.UserLoginDTO;
import com.project.shop.models.User;
import com.project.shop.responses.LoginResponse;
import com.project.shop.responses.RegisterResponse;
import com.project.shop.responses.UserReponse;
import com.project.shop.services.IUserService;
import com.project.shop.components.LocalizationUtils;
import com.project.shop.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
public class UserController {
    private final IUserService usersService;
    private final LocalizationUtils localizationUtils;
    // Them nguoi dung
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> createUser(
            @Valid @RequestBody UserDTO userDTO,
            BindingResult result
    ){
        try {
            if (result.hasErrors())
            {
                List<String> errorMessages =  result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(
                        RegisterResponse.builder()
                                .message(localizationUtils.getLocalizedMessage(MessageKeys.REGISTER_HAVE_FAIL,result.getFieldError()))
                                .build()
                );
            }
            if (!userDTO.getPassword().equals(userDTO.getRetypePassword()))
            {
                return ResponseEntity.badRequest().body(RegisterResponse.builder()
                        .message(localizationUtils.getLocalizedMessage(MessageKeys.PASSWORD_NOT_MATCH))
                        .build());
            }
            User user = usersService.createUser(userDTO);
            return ResponseEntity.ok(
                    RegisterResponse.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.REGISTER_SUCCESSFULLY))
                            .user(user)
                            .build()
            );

        }catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(RegisterResponse.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.REGISTER_HAVE_FAIL,e.getMessage()))
                    .build());
        }
    }
    // Dang nhap
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody UserLoginDTO userLoginDTO
    ){
        // Kiem tra thong tin dang nhap va sinh token
        // Tra ve token trong reponse
        try {
            String token = usersService.login(userLoginDTO.getPhoneNumber(),userLoginDTO.getPassword());
            return ResponseEntity.ok(LoginResponse.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_SUCCESSFULLY))
                            .token(token)
                    .build());
        }catch (Exception e)
        {
            return ResponseEntity.badRequest().body(LoginResponse.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_FAILED, e.getMessage()))
                    .build());
        }
    }
    @PostMapping("/details")
    public ResponseEntity<UserReponse> getUserDetails(@RequestHeader("Authorization") String token)
    {
        try {
            String extractedToken = token.substring(7);
            User user = usersService.getUserDetailsFromToken(extractedToken);
            return ResponseEntity.ok(UserReponse.fromUser(user));
        }catch (Exception e)
        {
            return ResponseEntity.badRequest().build();
        }
    }
}
