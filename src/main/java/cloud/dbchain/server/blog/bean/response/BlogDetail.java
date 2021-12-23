package cloud.dbchain.server.blog.bean.response;

import cloud.dbchain.server.blog.bean.DiscussBundle;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BlogDetail {
    private String title;
    private String body;
    private List<DiscussBundle> discuss;
}
