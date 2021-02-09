package cloud.chenh.emiew.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class Book implements Serializable {

    private String url;

    private String parentUrl;

    private String title;

    private String subtitle;

    private String coverUrl;

    private String category;

    private Double rating;

    private Integer pages;

    private String uploader;

    private String uploadTime;

    private String language;

    private List<TagGroup> tagGroups;

    private List<Comment> comments;

    private List<String> thumbUrls;

    private List<String> imageUrls;

    private Boolean downloaded;

    @Getter
    @Setter
    public static class TagGroup {

        private String name;

        private List<String> tags;

    }

    @Getter
    @Setter
    public static class Comment {

        private String publisher;

        private Boolean isUploader;

        private String publishTime;

        private Integer score;

        private String content;

    }


}
