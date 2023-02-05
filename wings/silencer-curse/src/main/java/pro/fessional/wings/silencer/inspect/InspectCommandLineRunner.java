package pro.fessional.wings.silencer.inspect;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;

/**
 * run in CommandLineRunner
 *
 * @author trydofor
 * @since 2023-02-05
 */
@RequiredArgsConstructor
public class InspectCommandLineRunner implements BeanNameAware, Ordered {

    @Getter
    private final int order;
    private final CommandLineRunner runner;

    @Getter
    private String name;

    /**
     * @see org.springframework.boot.CommandLineRunner#run(String...)
     */
    public void run(String... args) throws Exception {
        runner.run(args);
    }

    @Override
    public void setBeanName(@NotNull String name) {
        this.name = name;
    }
}
