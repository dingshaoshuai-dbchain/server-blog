package cloud.dbchain.server.blog.dao;

import cloud.dbchain.server.blog.BaseDBChainResult;
import com.gcigb.dbchain.QueriedArray;
import com.gcigb.dbchain.RestClientKt;
import com.gcigb.dbchain.bean.result.DBChainQueryResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.List;
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

    public List<Object> queryAndParse(byte[] privateKey, byte[] publicKey, QueriedArray queriedArray) {
        DBChainQueryResult result = RestClientKt.querier(queriedArray, privateKey, publicKey);
        if (!result.isSuccess()) return null;
        Gson gson = new Gson();
        Type type = new TypeToken<BaseDBChainResult<Object>>() {
        }.getType();
        BaseDBChainResult<Object> o = gson.fromJson(result.getContent(), type);
        if (o == null) return null;
        return o.getResult();
    }

    public <T> List<T> queryAndParse(byte[] privateKey, byte[] publicKey, QueriedArray queriedArray, Type type) {
        DBChainQueryResult result = RestClientKt.querier(queriedArray, privateKey, publicKey);
        if (!result.isSuccess()) return null;
        Gson gson = new Gson();
        BaseDBChainResult<T> o = gson.fromJson(result.getContent(), type);
        if (o == null) return null;
        return o.getResult();
    }
}
