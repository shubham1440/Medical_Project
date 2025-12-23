package com.healthcare.service;

import com.healthcare.dto.request.LoginRequest;
import com.healthcare.dto.request.RegisterRequest;
import com.healthcare.dto.response.AuthResponse;

public interface AuthService {

    public AuthResponse register(RegisterRequest request) throws Exception;

    public AuthResponse login(LoginRequest request) throws Exception;


}