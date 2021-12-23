package cloud.dbchain.server.blog;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BaseDBChainResult<T> {
    private String height;
    private List<T> result;
}
