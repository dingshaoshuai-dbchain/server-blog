package cloud.dbchain.server.blog.dao;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.Iq80DBFactory;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

public class KeyValueDao {

    private DB db;

    public KeyValueDao(String pathName) {
        try {
            File file = new File(pathName);
            db = Iq80DBFactory.factory.open(file, new Options().createIfMissing(true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void put(byte[] key, byte[] value) {
        db.put(key, value);
    }

    @Nullable
    public byte[] get(byte[] key) {
        return db.get(key);
    }
}
