package cloud.chenh.emiew.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class CustomPage<T> implements Serializable {

    private Integer pageNumber;

    private Integer totalPages;

    private List<T> elements;

    public CustomPage(Integer pageNumber, Integer totalPages, List<T> elements) {
        this.pageNumber = pageNumber;
        this.totalPages = totalPages;
        this.elements = elements;
    }

}
