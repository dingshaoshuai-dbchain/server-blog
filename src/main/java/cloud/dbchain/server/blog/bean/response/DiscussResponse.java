package cloud.dbchain.server.blog.bean.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DiscussResponse {
    private String id;
    private String blog_id;
    private String discuss_id;
    private String text;
    private String authorName;
    private String authorPhoto;
    private String authorAddress;
    private String repliedName;
    private String repliedPhoto;
}
