package com.project.shop.services;

import com.project.shop.dtos.UserDTO;
import com.project.shop.excepsions.DataNotFoundExcepsion;
import com.project.shop.models.User;
import org.springframework.stereotype.Service;

import java.security.InvalidAlgorithmParameterException;

//@Service
public interface IUserService {
    User createUser(UserDTO userDTO) throws Exception;
    String login(String phoneNumber, String password) throws DataNotFoundExcepsion, InvalidAlgorithmParameterException;

    User getUserDetailsFromToken(String token) throws Exception;
}
