package cloud.chenh.emiew.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ImageFile implements Serializable {

    private String name;

    private byte[] bytes;

    public ImageFile() {
    }

    public ImageFile(String name, byte[] bytes) {
        this.name = name;
        this.bytes = bytes;
    }

}
