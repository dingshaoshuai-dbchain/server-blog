package cloud.dbchain.server.blog.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserInfo {
    private String userName;
    private byte[] privateKey;
    private byte[] publicKey33;
    private String address;
}
