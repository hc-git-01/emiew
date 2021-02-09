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
@Table(name = "block")
public class Block extends BaseEntity {

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String value;

    public Block() {
    }

    public Block(String type, String value) {
        this.type = type;
        this.value = value;
    }

}
