package cloud.dbchain.server.blog.service;

import cloud.dbchain.server.blog.dao.KeyValueDao;
import com.gcigb.dbchain.util.coding.HashUtilKt;
import org.springframework.stereotype.Component;

@Component
public class UserPasswordService {

    private final KeyValueDao dao;

    public UserPasswordService() {
        dao = new KeyValueDao("D:/level-DB/User");
    }

    public byte[] get(String userName){
        return dao.get(userName.getBytes());
    }

    public boolean isExistAccount(String userName) {
        byte[] value = dao.get(userName.getBytes());
        return value != null;
    }

    public void add(String userName, String password) {
        dao.put(userName.getBytes(), HashUtilKt.hash256(password.getBytes()));
    }
}
