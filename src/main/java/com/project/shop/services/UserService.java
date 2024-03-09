package com.project.shop.services;

import com.project.shop.components.JwtTokenUtils;
import com.project.shop.dtos.UserDTO;
import com.project.shop.excepsions.DataNotFoundExcepsion;
import com.project.shop.excepsions.PermissionDenyException;
import com.project.shop.models.Role;
import com.project.shop.models.User;
import com.project.shop.repositories.RoleRepository;
import com.project.shop.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.InvalidAlgorithmParameterException;
import java.util.Optional;
@Service
@RequiredArgsConstructor
public class UserService implements IUserService{
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtil;
    private final AuthenticationManager authenticationManager;
    @Override
    public User createUser(UserDTO userDTO) throws Exception {
        String phoneNumber = userDTO.getPhoneNumber();
        // Kiem tra co so dien thoai hay chua
        if(userRepository.existsByPhoneNumber(phoneNumber))
        {
            throw new DataIntegrityViolationException("Phone number is already exists");
        }
        Role role = roleRepository.findById(userDTO.getRoleId())
                .orElseThrow(() -> new DataNotFoundExcepsion("Role not found!"));
        if(role.getName().toUpperCase().equals("ADMIN"))
        {
            throw new PermissionDenyException("You cannot register an admin account");
        }
        //Convert
        User newUser = User.builder()
                .fullName(userDTO.getFullName())
                .phoneNumber(userDTO.getPhoneNumber())
                .password(userDTO.getPassword())
                .address(userDTO.getAddress())
                .dateOfBirth(userDTO.getDateOfBirth())
                .facebookAccountId(userDTO.getFacebookAccountId())
                .googleAccountId(userDTO.getGoogleAccountId())
                .build();
        newUser.setRole(role);

        if(userDTO.getFacebookAccountId()==0 && userDTO.getGoogleAccountId()==0)
        {
            // Xu li mat khau(security)
            String password = userDTO.getPassword();
            String encodePassword = passwordEncoder.encode(password);
            newUser.setPassword(encodePassword);
        }
        return userRepository.save(newUser);
    }

    @Override
    public String login(String phoneNumber, String password) throws DataNotFoundExcepsion, InvalidAlgorithmParameterException {
        Optional<User> optionalUser =  userRepository.findByPhoneNumber(phoneNumber);
        if (optionalUser.isEmpty())
        {
            throw new DataNotFoundExcepsion("Invalid phone-number / password");
        }
        User existingUser = optionalUser.get();
        // check password
        if(existingUser.getFacebookAccountId()==0 && existingUser.getGoogleAccountId()==0)
        {
            if(!passwordEncoder.matches(password,existingUser.getPassword())){
                throw new BadCredentialsException("Wrong phone number or password");
            }
        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                phoneNumber, password,existingUser.getAuthorities()
        );
        //authenticate
        authenticationManager.authenticate(authenticationToken);
        return jwtTokenUtil.generateToken(existingUser);
    }

    @Override
    public User getUserDetailsFromToken(String token) throws Exception {
        if(jwtTokenUtil.isTokenExpired(token))
        {
            throw new Exception("Token is exiped");
        }
        String phoneNumber = jwtTokenUtil.extractPhoneNumber(token);
        Optional<User> user = userRepository.findByPhoneNumber(phoneNumber);
        if(user.isPresent()) return user.get();
        else {
            throw new Exception("User not found");
        }
    }
}
