package cloud.dbchain.server.blog.table;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserTable {
    private String id;
    private String name;
    private String age;
    private String dbchain_key;
    private String sex;
    private String status;
    private String photo;
    private String motto;
    private String created_at;
    private String created_by;
    private String tx_hash;
}
