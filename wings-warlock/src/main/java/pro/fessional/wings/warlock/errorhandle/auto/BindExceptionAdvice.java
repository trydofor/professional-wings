package pro.fessional.wings.warlock.errorhandle.auto;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import pro.fessional.mirana.data.R;
import pro.fessional.wings.slardar.servlet.MessageHelper;
import pro.fessional.wings.warlock.enums.errcode.CommonErrorEnum;

import static pro.fessional.wings.slardar.servlet.request.RequestHelper.allErrors;

/**
 * @author trydofor
 * @since 2021-04-09
 */

@ControllerAdvice(annotations = RestController.class)
@Order(Ordered.HIGHEST_PRECEDENCE + 1000)
public class BindExceptionAdvice {

    @Setter(onMethod_ = {@Autowired})
    protected MessageSource messageSource;

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<R<?>> bindException(BindException ex) {
        final R<?> body = R.ng(allErrors(ex.getBindingResult()));
        return ResponseEntity.ok(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<R<?>> httpMessageNotReadableException(HttpMessageNotReadableException ex) {
        String msg = MessageHelper.get(messageSource, CommonErrorEnum.MessageUnreadable);
        if (msg.isEmpty()) {
            msg = ex.getMessage();
        }
        else {
            msg = msg + "\n" + ex.getMessage();
        }
        final R<?> body = R.ng(msg);
        return ResponseEntity.ok(body);
    }
}
