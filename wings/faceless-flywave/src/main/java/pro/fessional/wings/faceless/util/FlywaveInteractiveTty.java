package pro.fessional.wings.faceless.util;

import java.util.Scanner;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author trydofor
 * @since 2020-07-07
 */
@SuppressWarnings("CanBeFinal")
public class FlywaveInteractiveTty {

    public static Function<String, Boolean> askYes = it -> true;
    public static Function<String, Boolean> askNo = it -> false;

    public static Function<String, Boolean> askTty() {
        final Scanner scanner = new Scanner(System.in);
        return msg -> {
            System.out.println("=== 😺😸😹😻😼😽🙀😿😾😺😸😹😻😼😽🙀😿😾 ===");
            System.out.println(msg);
            System.out.print("=== 😺😸😹😻😼😽🙀 continue or not ? [y|n]>");
            while (scanner.hasNext()) {
                String ans = scanner.next();
                if ("y".equalsIgnoreCase(ans)) {
                    return true;
                }
                else if ("n".equalsIgnoreCase(ans)) {
                    return false;
                }
            }

            return false;
        };
    }

    public static final BiConsumer<String, String> logNil = (s, s2) -> {};

    public static void main(String[] args) {
        final Function<String, Boolean> log = askTty();
        log.apply("ask tty test");
        System.out.println("done");
    }
}
