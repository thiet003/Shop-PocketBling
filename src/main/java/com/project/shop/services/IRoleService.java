package com.project.shop.services;

import com.project.shop.models.Role;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IRoleService {
    List<Role> getAllRoles();
}
