package com.ct08SWA.userservice.userapplicationservice.dto.outputdto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserValidationResponse {
    private String id;
    private boolean active; // Quan trọng: Khớp với field bên Order Service
}


