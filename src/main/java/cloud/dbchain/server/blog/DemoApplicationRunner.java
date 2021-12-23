package cloud.dbchain.server.blog;

import com.gcigb.dbchain.DBChainKt;
import com.gcigb.dbchain.ILog;
import com.gcigb.dbchain.MnemonicClientKt;
import com.gcigb.network.util.LogKt;
import dbchain.client.java.sm2.SM2Encrypt;
import okhttp3.Interceptor;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class DemoApplicationRunner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) {
        String appCode = "GGPJWXRSC6";
        String chainId = "testnet02";
        String baseUrl = "https://controlpanel.dbchain.cloud/relay02/";
        ArrayList<Interceptor> interceptors = new ArrayList<>();
        DBChainKt.init(
                appCode,
                baseUrl,
                chainId,
                new SM2Encrypt() /* or new Secp256k1Encrypt() */,
                new LogImpl(),
                200000,
                true,
                "test_tag",
                "error_tag",
                "http_tag",
                interceptors
        );
        LogKt.logI("DemoApplicationRunner$run()");
        ArrayList<String> mnemonic = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            mnemonic.add("list");
        }
        AdministratorKt.dbChainKey = MnemonicClientKt.importMnemonic(mnemonic);
        AdministratorKt.privateKey = AdministratorKt.dbChainKey.getPrivateKeyBytes();
        AdministratorKt.publicKey = AdministratorKt.dbChainKey.getPublicKeyBytes33();
        AdministratorKt.address = AdministratorKt.dbChainKey.getAddress();
    }

    private static class LogImpl implements ILog {

        @Override
        public void logD(@NotNull String s, @NotNull String s1) {
            System.out.println(" ================== " + s + " : " + s1);
        }

        @Override
        public void logE(@NotNull String s) {
            System.out.println(" ================== " + "error : " + s);
        }

        @Override
        public void logE(@NotNull String s, @NotNull String s1) {
            System.out.println(" ================== " + s + " : " + s1);
        }

        @Override
        public void logHttp(@NotNull String s) {
            System.out.println(" ================== " + "test : " + s);
        }

        @Override
        public void logI(@NotNull Object o) {
            System.out.println(" ================== " + "test : " + o);
        }

        @Override
        public void logI(@NotNull String s) {
            System.out.println(" ================== " + "test : " + s);
        }

        @Override
        public void logI(@NotNull String s, @NotNull String s1) {
            System.out.println(" ================== " + s + " : " + s1);
        }

        @Override
        public void logV(@NotNull String s, @NotNull String s1) {
            System.out.println(" ================== " + s + " : " + s1);
        }

        @Override
        public void logW(@NotNull String s, @NotNull String s1) {
            System.out.println(" ================== " + s + " : " + s1);
        }
    }
}
