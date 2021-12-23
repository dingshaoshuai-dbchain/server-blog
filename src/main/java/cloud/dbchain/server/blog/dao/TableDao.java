package cloud.dbchain.server.blog.dao;

import com.gcigb.dbchain.QueriedArray;
import com.gcigb.dbchain.RestClientKt;
import com.gcigb.dbchain.bean.result.DBChainQueryResult;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TableDao {

    public boolean inertRow(byte[] privateKey, byte[] publicKey, String address, String tableName, Map<String, String> map) {
        return RestClientKt.insertRow(tableName, map, privateKey, publicKey, address);
    }

    public boolean freezeRow(byte[] privateKey, byte[] publicKey, String address, String tableName, String id) {
        return RestClientKt.freezeRow(tableName, id, privateKey, publicKey, address);
    }

    public DBChainQueryResult query(byte[] privateKey, byte[] publicKey, QueriedArray queriedArray) {
        return RestClientKt.querier(queriedArray, privateKey, publicKey);
    }
}
