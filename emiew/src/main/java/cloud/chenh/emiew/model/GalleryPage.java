package cloud.chenh.emiew.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class GalleryPage implements Serializable {

    private Integer pageNumber;

    private Integer nextPage;

    private Integer totalPages;

    private List<Book> elements;

    public GalleryPage(Integer pageNumber, Integer totalPages, List<Book> elements) {
        this.pageNumber = pageNumber;
        this.totalPages = totalPages;
        this.elements = elements;
    }

    public GalleryPage(Integer pageNumber, Integer nextPage, Integer totalPages, List<Book> elements) {
        this.pageNumber = pageNumber;
        this.nextPage = nextPage;
        this.totalPages = totalPages;
        this.elements = elements;
    }

}
