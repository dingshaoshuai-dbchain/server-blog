package cloud.dbchain.server.blog.dao;

import com.gcigb.dbchain.IPrivateKeyDB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PrivateKeyDao implements IPrivateKeyDB {

    private final KeyValueDao dao;

    public PrivateKeyDao(String pathName) {
        dao = new KeyValueDao(pathName);
    }

    @Nullable
    @Override
    public byte[] loadEncryptedPrivateKey(@NotNull byte[] key) {
        return dao.get(key);
    }

    @Nullable
    @Override
    public byte[] loadSecret(@NotNull byte[] key) {
        return dao.get(key);
    }

    @Override
    public boolean saveEncryptedPrivateKey(@NotNull byte[] key, @NotNull byte[] encryptedPrivateKey) {
        dao.put(key, encryptedPrivateKey);
        return true;
    }

    @Override
    public boolean saveSecret(@NotNull byte[] key, @NotNull byte[] encryptSecret) {
        dao.put(key, encryptSecret);
        return true;
    }
}
