package cloud.chenh.emiew.data.entity;

import cloud.chenh.emiew.data.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "download")
public class Download extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String url;

    private String title;

    private String coverUrl;

    private String cover;

    private String category;

    private Integer pages;

    private String uploader;

    private String uploadTime;

    private String language;

    private Status status = Status.NORMAL;

    @JsonIgnore
    @OneToMany(mappedBy = "download", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @OrderBy("index ASC")
    private List<Image> images;

    public String getKey() {
        if (StringUtils.isBlank(url)) {
            return null;
        }

        String prefix = url.contains("exhentai") ? "x" : "h";
        String id = url.split("/")[4];
        String token = url.split("/")[5];

        return prefix + "_" + id + "_" + token;
    }

    public Boolean getFinished() {
        if (status != Status.NORMAL) {
            return false;
        }

        return images.stream().noneMatch(image -> StringUtils.isBlank(image.getPath()));
    }

    public Long getProgress() {
        return images.stream()
                .filter(image -> StringUtils.isNotBlank(image.getPath()))
                .count();
    }

    @Getter
    public enum Status {
        NORMAL, FAILED, PAUSED
    }

}
