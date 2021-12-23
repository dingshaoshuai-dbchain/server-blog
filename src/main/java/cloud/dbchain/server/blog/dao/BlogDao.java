package cloud.dbchain.server.blog.dao;

import cloud.dbchain.server.blog.contast.Blogs;
import cloud.dbchain.server.blog.contast.Common;
import cloud.dbchain.server.blog.contast.Discuss;
import com.gcigb.dbchain.QueriedArray;
import com.gcigb.dbchain.RestClientKt;
import com.gcigb.dbchain.bean.result.DBChainQueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class BlogDao {

    private TableDao dao;

    public BlogDao(@Autowired TableDao dao) {
        this.dao = dao;
    }

    public DBChainQueryResult getAll(byte[] privateKey, byte[] publicKey) {
        QueriedArray queriedArray = new QueriedArray("table", Blogs.tableName);
        return RestClientKt.querier(queriedArray,privateKey,publicKey);
    }

    public DBChainQueryResult getBlogs(byte[] privateKey, byte[] publicKey, String title, String createdBy) {
        QueriedArray queriedArray = new QueriedArray("table", Blogs.tableName);
        if (title != null && title.length() > 0) {
            queriedArray.findEqual(Blogs.title, title);
        }
        if (createdBy != null && createdBy.length() > 0) {
            queriedArray.findEqual(Common.created_by, createdBy);
        }
        return RestClientKt.querier(queriedArray,privateKey,publicKey);
    }

    public DBChainQueryResult getBlog(byte[] privateKey, byte[] publicKey, String id) {
        QueriedArray queriedArray = new QueriedArray("table", Blogs.tableName)
                .findById(id);
        return RestClientKt.querier(queriedArray,privateKey,publicKey);
    }

    public DBChainQueryResult getBlogDiscuss(byte[] privateKey, byte[] publicKey, String blogId) {
        QueriedArray queriedArray = new QueriedArray("table", Discuss.tableName)
                .findEqual(Discuss.blog_id, blogId);
        return RestClientKt.querier(queriedArray,privateKey,publicKey);
    }

    public boolean insert(byte[] privateKey, byte[] publicKey, String address, Map<String, String> map) {
        return dao.inertRow(privateKey, publicKey, address, Blogs.tableName, map);
    }

}
