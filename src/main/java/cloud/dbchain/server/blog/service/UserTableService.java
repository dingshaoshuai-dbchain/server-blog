package cloud.dbchain.server.blog.service;

import cloud.dbchain.server.blog.BaseDBChainResult;
import cloud.dbchain.server.blog.contast.User;
import cloud.dbchain.server.blog.dao.TableDao;
import cloud.dbchain.server.blog.table.UserTable;
import com.gcigb.dbchain.FactoryKt;
import com.gcigb.dbchain.QueriedArray;
import com.gcigb.dbchain.RestClientKt;
import com.gcigb.dbchain.bean.Message;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

@Component
public class UserTableService {

    private final TableDao tableDao;

    public UserTableService(@Autowired TableDao tableDao) {
        this.tableDao = tableDao;
    }

    public UserTable getUser(byte[] privateKey, byte[] publicKey, String address) {
        QueriedArray queriedArray = new QueriedArray("table", User.tableName)
                .findEqual(User.dbchain_key, address);
        Type type = new TypeToken<BaseDBChainResult<UserTable>>() {
        }.getType();
        List<UserTable> objects = tableDao.queryAndParse(privateKey, publicKey, queriedArray, type);
        if (objects == null || objects.isEmpty()) return null;
        return objects.get(0);
    }

    public boolean addUser(byte[] privateKey, byte[] publicKey, String address, Map<String, String> map) {
        return tableDao.inertRow(privateKey, publicKey, address, User.tableName, map);
    }

    public boolean updateUser(byte[] privateKey, byte[] publicKey, String address, Map<String, String> user) {
        UserTable userTable = getUser(privateKey, publicKey, address);
        List<Message> messages = RestClientKt.newMessageList();
        messages.add(FactoryKt.createFreezeMessage(User.tableName, userTable.getId(), address));
        messages.add(FactoryKt.createInsertMessage(User.tableName, user, address));
        return RestClientKt.handleBatchMessage(messages, privateKey, publicKey, address);
    }
}
