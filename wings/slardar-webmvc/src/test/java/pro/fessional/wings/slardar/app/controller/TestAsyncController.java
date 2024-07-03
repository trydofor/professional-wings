package pro.fessional.wings.slardar.app.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import pro.fessional.wings.slardar.app.service.TestAsyncService;
import pro.fessional.wings.slardar.app.service.TestAsyncService.AsyncType;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

/**
 * @author trydofor
 * @since 2024-05-07
 */
@Slf4j
@RestController
public class TestAsyncController {

    @Setter(onMethod_ = { @Autowired })
    protected TestAsyncService testAsyncService;

    @RequestMapping(value = "/test/asyn-type.json")
    public CompletableFuture<String> testAsyncType(@RequestParam("err") AsyncType err) {
        log.info("testAsyncType type={}", err);
        return testAsyncService.typeAsync(err);
    }

    @RequestMapping(value = "/test/asyn-void.json")
    public CompletableFuture<String> testAsyncVoid(@RequestParam("err") AsyncType err) {
        log.info("testAsyncVoid type={}", err);
        testAsyncService.voidAsync(err);
        return CompletableFuture.completedFuture(err.name());
    }

    @RequestMapping(value = "/test/asyn-defer.json")
    public DeferredResult<String> testAsyncDefer(@RequestParam("err") AsyncType err) {
        DeferredResult<String> result = new DeferredResult<>(1000L);
        log.info("testAsyncDefer type={}", err);
        testAsyncService.deferAsync(result, err);
        return result;
    }

    @RequestMapping(value = "/test/asyn-call.json")
    public Callable<String> testAsyncCall(@RequestParam("err") AsyncType err) {
        return () -> testAsyncService.syncResult(err);
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, HttpServletRequest request) {
        log.info("TestAsyncService handleException", ex);
        return getErrResponse(request.getParameter("err"));
    }

    public static String getErrResponse(String err) {
        return "AsyncErr=" + err;
    }
}
