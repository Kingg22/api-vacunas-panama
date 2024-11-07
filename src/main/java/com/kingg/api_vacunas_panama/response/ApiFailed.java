package com.kingg.api_vacunas_panama.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApiFailed implements Serializable {
    String code;
    String property;
    String message;

    public ApiFailed(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public ApiFailed(String code) {
        this.code = code;
    }

    public ApiFailed(ApiResponseCode code) {
        this.code = code.toString();
    }

    public ApiFailed(ApiResponseCode code, String message) {
        this.code = code.toString();
        this.message = message;
    }

    public ApiFailed(ApiResponseCode code, String property, String message) {
        this.code = code.toString();
        this.property = property;
        this.message = message;
    }

}
