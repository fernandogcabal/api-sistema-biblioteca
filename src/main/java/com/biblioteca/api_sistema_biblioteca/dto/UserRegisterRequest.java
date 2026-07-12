package com.biblioteca.api_sistema_biblioteca.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterRequest {

    private String username;
    private String email;
    private char[] password; // Alta seguridad: evita el uso de String Pool en la RAM
}
