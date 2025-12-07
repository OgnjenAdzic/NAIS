package rs.ac.uns.acs.nais.GraphDatabaseService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseDTO<T> {
    private Boolean success;
    private String message;
    private T data;

    public static <T> ApiResponseDTO<T> success(T data) {
        return new ApiResponseDTO<>(true, "Success", data);
    }

    public static <T> ApiResponseDTO<T> success(String message, T data) {
        return new ApiResponseDTO<>(true, message, data);
    }

    public static <T> ApiResponseDTO<T> error(String message) {
        return new ApiResponseDTO<>(false, message, null);
    }
}