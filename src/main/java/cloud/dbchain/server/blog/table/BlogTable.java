package cloud.dbchain.server.blog.table;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BlogTable {
    private String id;
    private String title;
    private String body;
    private String img;
    private String created_at;
    private String created_by;
    private String tx_hash;
}
