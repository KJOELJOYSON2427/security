package com.sivalabs.messages.Response.ApiResponse;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
public class ApiResponse {

    private boolean success;
    private String message;
    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
