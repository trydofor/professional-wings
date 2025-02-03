package pro.fessional.wings.warlock.errorhandle.auto;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import pro.fessional.mirana.data.R;
import pro.fessional.mirana.i18n.I18nNotice;
import pro.fessional.wings.silencer.spring.WingsOrdered;
import pro.fessional.wings.warlock.errcode.CommonErrorEnum;
import pro.fessional.wings.warlock.errorhandle.I18nAwareHelper;

import java.util.List;


/**
 * @author trydofor
 * @since 2021-04-09
 */

@ControllerAdvice(annotations = RestController.class)
@Order(BindExceptionAdvice.ORDER)
@Slf4j
public class BindExceptionAdvice {

    public static final int ORDER = WingsOrdered.Lv4Application;

    /**
     * binding valid failed
     */
    @ExceptionHandler({ MethodArgumentNotValidException.class, BindException.class })
    public ResponseEntity<R<?>> bindException(BindException ex) {
        log.warn("bindException Advice", ex);
        List<I18nNotice> notices = I18nAwareHelper.notices(ex);
        final R<?> body = R.ngError(notices);
        body.setMessageByErrors();
        return ResponseEntity.ok(body);
    }

    /**
     * body read failed
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<R<?>> httpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.warn("httpMessageNotReadableException Advice", ex);
        I18nNotice ntc = I18nNotice.of(CommonErrorEnum.MessageUnreadable);
        ntc.setType(I18nNotice.Type.Validation.name());
        ntc.setTarget("body");
        final R<?> body = R.ngError(ntc);
        body.setMessageByErrors();
        return ResponseEntity.ok(body);
    }
}
