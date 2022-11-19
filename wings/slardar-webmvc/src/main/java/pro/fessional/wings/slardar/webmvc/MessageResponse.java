package pro.fessional.wings.slardar.webmvc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import pro.fessional.mirana.text.StringTemplate;

/**
 * @author trydofor
 * @since 2022-11-17
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class MessageResponse extends SimpleResponse {
    public static final String MessageToken = "{message}";

    @NotNull
    protected String messageBody = "";

    public String responseBody(String msg) {
        if (msg == null || msg.isEmpty()) {
            return responseBody;
        }
        else {
            return StringTemplate
                    .dyn(messageBody)
                    .bindStr(MessageToken, msg)
                    .toString();
        }
    }

    @Override
    public void fillAbsent(SimpleResponse other) {
        super.fillAbsent(other);
        if (other instanceof MessageResponse) {
            if (messageBody.isEmpty()) {
                messageBody = ((MessageResponse) other).messageBody;
            }
        }
    }
}
