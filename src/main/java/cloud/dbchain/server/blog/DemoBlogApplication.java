package cloud.dbchain.server.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class})
public class DemoBlogApplication {

    public static void main(String[] args) {
        System.out.println("DemoBlogApplication$main()");
        SpringApplication.run(DemoBlogApplication.class, args);
    }
}
