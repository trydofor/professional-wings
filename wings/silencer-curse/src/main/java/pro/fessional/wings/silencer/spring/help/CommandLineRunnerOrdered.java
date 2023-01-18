package pro.fessional.wings.silencer.spring.help;

import lombok.Data;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;

/**
 * @author trydofor
 * @since 2023-01-09
 */
@Data
public class CommandLineRunnerOrdered implements CommandLineRunner, Ordered {
    private final int order;
    private final CommandLineRunner runner;

    @Override
    public void run(String... args) throws Exception {
        runner.run(args);
    }

    @Override
    public int getOrder() {
        return order;
    }
}
