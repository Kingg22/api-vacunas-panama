package com.kingg.api_vacunas_panama.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@JsonPropertyOrder({"status", "data", "errors", "warnings", "metadata"})
public class ApiResponse extends ApiContentResponse implements IApiResponse<String, Serializable>, Serializable {
    private Map<String, Serializable> status = new LinkedHashMap<>();
    private Map<String, Serializable> metadata = new LinkedHashMap<>();

    public void addStatusCode(HttpStatus httpStatus) {
        this.status.put("code", httpStatus.value());
    }

    public void addStatus(String key, Serializable value) {
        this.status.put(key, value);
    }

    public void addStatus(String key, String message) {
        this.status.put(key, message);
    }

    public void addStatus(String message) {
        this.status.put("message", message);
    }

    public void addMetadata(String key, Serializable value) {
        this.metadata.put(key, value);
    }

    @JsonIgnore
    public int getStatusCode() {
        return Integer.parseInt(this.status.get("code").toString());
    }

}
