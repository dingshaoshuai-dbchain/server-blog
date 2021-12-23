package cloud.dbchain.server.blog.table;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DiscussTable {
    private String id;
    private String blog_id;
    private String discuss_id;
    private String text;
    private String created_at;
    private String created_by;
    private String tx_hash;
}
