package cloud.dbchain.server.blog.service;

import cloud.dbchain.server.blog.dao.PrivateKeyDao;
import com.gcigb.dbchain.KeyEscrow;
import org.springframework.stereotype.Component;

@Component
public class KeyEscrowService {

    private final PrivateKeyDao keyValueDao;

    public KeyEscrowService() {
        keyValueDao = new PrivateKeyDao("D:/level-DB/KeyEscrow");
    }

    public boolean createAndSavePrivateKeyWithPassword(String userName, String password) {
        try {
            return KeyEscrow.INSTANCE.createAndSavePrivateKeyWithPassword(userName, password, keyValueDao);
        } catch (NullPointerException e) {
            return false;
        }
    }

    public byte[] loadPrivateKeyByPassword(String userName, String password) {
        try {
            return KeyEscrow.INSTANCE.loadPrivateKeyByPassword(userName, password, keyValueDao);
        } catch (NullPointerException e) {
            return null;
        }
    }

    public boolean savePrivateKeyWithRecoverWord(String userName, String recoverWord, byte[] privateKey) {
        try {
            return KeyEscrow.INSTANCE.savePrivateKeyWithRecoverWord(userName, recoverWord, privateKey, keyValueDao);
        } catch (NullPointerException e) {
            return false;
        }
    }

    public byte[] loadPrivateKeyByRecoverWord(String userName, String recoverWord) {
        try {
            return KeyEscrow.INSTANCE.loadPrivateKeyByRecoverWord(userName, recoverWord, keyValueDao);
        } catch (NullPointerException e) {
            return null;
        }
    }

    public boolean resetPasswordFromRecoverWord(String userName, String recoverWord, String newPassword) {
        try {
            return KeyEscrow.INSTANCE.resetPasswordFromRecoverWord(userName, recoverWord, newPassword, keyValueDao);
        } catch (NullPointerException e) {
            return false;
        }
    }

    public boolean resetPasswordFromOld(String userName, String oldPassword, String newPassword) {
        try {
            return KeyEscrow.INSTANCE.resetPasswordFromOld(userName, oldPassword, newPassword, keyValueDao);
        } catch (NullPointerException e) {
            return false;
        }
    }
}
