package com.organizacion.dto;

import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClienteRequest {

    private String nombres;
    private Long documento;
    private String email;
    private String password;

}
