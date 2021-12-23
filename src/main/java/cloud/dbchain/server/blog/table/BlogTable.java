package cloud.dbchain.server.blog.table;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BlogTable {
    private String title;
    private String body;
    private String img;
    private String tx_hash;
}
