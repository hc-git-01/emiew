package cloud.chenh.emiew.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class OperationResult<T> implements Serializable {

    private Integer code;

    private T data;

    private OperationResult(Integer code, T data) {
        this.code = code;
        this.data = data;
    }

    public static <T> OperationResult<T> ok(T data) {
        return new OperationResult<>(200, data);
    }

    public static <T> OperationResult<T> no(T data) {
        return new OperationResult<>(500, data);
    }

}
