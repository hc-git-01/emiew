package cloud.chenh.emiew.model;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@Getter
@Setter
public class EhCookie implements Serializable {

    private String ipbPassHash = "";
    private String ipbMemberId = "";
    private String igneous = "";

    public EhCookie() {
    }

    public EhCookie(String ipbPassHash, String ipbMemberId, String igneous) {
        this.ipbPassHash = ipbPassHash == null ? "" : ipbPassHash;
        this.ipbMemberId = ipbMemberId == null ? "" : ipbMemberId;
        this.igneous = igneous == null ? "" : igneous;
    }

    public Boolean loaded() {
        return StringUtils.isNotBlank(ipbPassHash) &&
                StringUtils.isNotBlank(ipbMemberId) &&
                StringUtils.isNotBlank(igneous);
    }

}
