package cloud.dbchain.server.blog.bean;

import cloud.dbchain.server.blog.bean.response.DiscussResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DiscussBundle {
    private DiscussResponse discuss;
    private List<DiscussBundle> repliedList;
}
