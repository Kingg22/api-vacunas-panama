package com.kingg.api_vacunas_panama.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiContentResponse implements IApiContentResponse, Serializable {
    private Map<String, Serializable> data = new LinkedHashMap<>();
    private List<ApiFailed> errors = new ArrayList<>();
    private List<ApiFailed> warnings = new ArrayList<>();

    public void addData(String key, Serializable value) {
        this.data.put(key, value);
    }

    public void addData(Map<String, Serializable> data) {
        this.data.putAll(data);
    }

    public void addError(String code, String message) {
        this.errors.add(new ApiFailed(code, message));
    }

    public void addError(String code, String property, String message) {
        this.errors.add(new ApiFailed(code, property, message));
    }

    public void addError(ApiResponseCode code, String property, String message) {
        this.errors.add(new ApiFailed(code, property, message));
    }

    public void addError(ApiResponseCode apiResponseCode) {
        this.errors.add(new ApiFailed(apiResponseCode));
    }

    public void addError(ApiResponseCode apiResponseCode, @NotNull Object message) {
        this.errors.add(new ApiFailed(apiResponseCode, message.toString()));
    }

    public void addError(ApiResponseCode apiResponseCode, String message) {
        this.errors.add(new ApiFailed(apiResponseCode, message));
    }

    public void addError(String code) {
        this.errors.add(new ApiFailed(code));
    }

    public void addErrors(@NotNull List<?> errorsList) {
        for (Object error : errorsList) {
            if (error instanceof ApiFailed failed) {
                this.errors.add(failed);
            } else {
                throw new IllegalArgumentException("This implementation only accepts ApiFailed");
            }
        }
    }

    public void addWarning(String code, String message) {
        this.warnings.add(new ApiFailed(code, message));
    }

    public void addWarning(ApiResponseCode apiResponseCode, String message) {
        this.warnings.add(new ApiFailed(apiResponseCode, message));
    }

    public void addWarning(ApiResponseCode apiResponseCode, String property, String message) {
        this.warnings.add(new ApiFailed(apiResponseCode, property, message));
    }

    public void addWarnings(@NotNull List<?> warningsList) {
        for (Object warning : warningsList) {
            if (warning instanceof ApiFailed failed) {
                this.warnings.add(failed);
            } else {
                throw new IllegalArgumentException("This implementation only accepts ApiFailed");
            }
        }
    }

    public boolean hasErrors() {
        return !this.errors.isEmpty();
    }

    public boolean hasWarnings() {
        return !this.warnings.isEmpty();
    }

}
