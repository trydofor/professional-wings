package pro.fessional.wings.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pro.fessional.mirana.data.DataResult;
import pro.fessional.mirana.data.R;

/**
 * @author trydofor
 * @since 2020-06-21
 */
@ControllerAdvice
@Slf4j
public class ErrorController extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public DataResult<String> handleAnyException(Exception ex) {
        String c = ex.getClass().getSimpleName();
        log.info("wings handle exception, " + c, ex);
        return R.ng("unknown exception, " + c + ":" + ex.getMessage());
    }
}
