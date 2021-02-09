package cloud.chenh.emiew.data.entity;

import cloud.chenh.emiew.data.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "config")
public class Config extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String key;

    private String value;

    public Config() {
    }

    public Config(String key, String value) {
        this.key = key;
        this.value = value;
    }

}
