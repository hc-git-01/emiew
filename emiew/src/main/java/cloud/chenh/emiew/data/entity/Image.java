package cloud.chenh.emiew.data.entity;

import cloud.chenh.emiew.data.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "image")
public class Image extends BaseEntity {

    @Column(nullable = false)
    private String bookUrl;

    private Integer index;

    private String path;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @JoinColumn(name = "download_id")
    private Download download;

}
