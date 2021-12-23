package cloud.dbchain.server.blog.dao;

import cloud.dbchain.server.blog.contast.User;
import com.gcigb.dbchain.QueriedArray;
import com.gcigb.dbchain.RestClientKt;
import com.gcigb.dbchain.bean.result.DBChainQueryResult;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class UserTableDao {

    public DBChainQueryResult getAllUsers(byte[] privateKey, byte[] publicKey) {
        QueriedArray queriedArray = new QueriedArray("table", User.tableName);
        return RestClientKt.querier(queriedArray, privateKey, publicKey);
    }

    public boolean insert(byte[] privateKey, byte[] publicKey, String address, Map<String, String> map) {
        return RestClientKt.insertRow(User.tableName, map, privateKey, publicKey, address);
    }
}
