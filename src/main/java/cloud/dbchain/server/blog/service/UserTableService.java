package cloud.dbchain.server.blog.service;

import cloud.dbchain.server.blog.BaseDBChainResult;
import cloud.dbchain.server.blog.contast.User;
import cloud.dbchain.server.blog.dao.TableDao;
import cloud.dbchain.server.blog.table.UserTable;
import com.gcigb.dbchain.FactoryKt;
import com.gcigb.dbchain.QueriedArray;
import com.gcigb.dbchain.RestClientKt;
import com.gcigb.dbchain.bean.Message;
import com.gcigb.dbchain.bean.result.DBChainQueryResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserTableService {

    private Map<String, UserTable> userMap = new HashMap<>();
    private TableDao tableDao;

    public UserTableService(@Autowired TableDao tableDao) {
        this.tableDao = tableDao;
    }

    public void initAllUser(byte[] privateKey, byte[] publicKey) {
        QueriedArray queriedArray = new QueriedArray("table", User.tableName);
        DBChainQueryResult result = tableDao.query(privateKey, publicKey, queriedArray);
        String json = result.getContent();
        Gson gson = new Gson();
        Type type = new TypeToken<BaseDBChainResult<UserTable>>() {
        }.getType();
        BaseDBChainResult<UserTable> baseResult = gson.fromJson(json, type);
        List<UserTable> list = baseResult.getResult();
        for (UserTable userTable : list) {
            userMap.put(userTable.getDbchain_key(), userTable);
        }
    }

    public UserTable getUser(String address) {
        return userMap.get(address);
    }

    public boolean addUser(byte[] privateKey, byte[] publicKey, String address, Map<String, String> map) {
        UserTable userTable = new UserTable("", map.get("name"), "0", address, "男", "", "", "", "", "", "");
        boolean insert = tableDao.inertRow(privateKey, publicKey, address, User.tableName, map);
        if (insert) {
            userMap.put(userTable.getDbchain_key(), userTable);
            return true;
        } else {
            return false;
        }
    }

    public boolean updateUser(byte[] privateKey, byte[] publicKey, String address, Map<String, String> user) {
        UserTable userTable = userMap.get(address);
        List<Message> messages = RestClientKt.newMessageList();
        messages.add(FactoryKt.createFreezeMessage(User.tableName, userTable.getId(), address));
        messages.add(FactoryKt.createInsertMessage(User.tableName, user, address));
        return RestClientKt.handleBatchMessage(messages, privateKey, publicKey, address);
    }
}
