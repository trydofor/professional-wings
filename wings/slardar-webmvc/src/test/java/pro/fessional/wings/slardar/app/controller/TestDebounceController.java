package pro.fessional.wings.slardar.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.View;
import pro.fessional.mirana.data.Q;
import pro.fessional.mirana.data.R;
import pro.fessional.mirana.pain.DebugException;
import pro.fessional.wings.slardar.concur.Debounce;
import pro.fessional.wings.slardar.servlet.response.view.PlainTextView;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author trydofor
 * @since 2021-02-01
 */
@RestController
public class TestDebounceController {

    private final AtomicInteger seq = new AtomicInteger(0);

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<String> handleError(RuntimeException ex) {
        return ResponseEntity.ok().body(ex.getMessage());
    }

    @PostMapping("/test/debounce-nore.json")
    @Debounce(waiting = 600, header = {"User-Agent"}, reuse = false)
    public View debounceNore(@RequestParam String p, @RequestBody Q<String> q) {
        return new PlainTextView("plain/text", p + "::" + seq.getAndIncrement() + "::" + q.getQ());
    }

    @PostMapping("/test/debounce-view.json")
    @Debounce(waiting = 600, header = {"User-Agent"}, reuse = true)
    public View debounceView(@RequestParam String p, @RequestBody Q<String> q) {
        return new PlainTextView("plain/text", p + "::" + seq.getAndIncrement() + "::" + q.getQ());
    }

    @PostMapping("/test/debounce-error.json")
    @Debounce(waiting = 600, header = {"User-Agent"}, reuse = true)
    public View debounceError() {
        throw new DebugException("error-seq:" + seq.getAndIncrement());
    }

    @PostMapping("/test/debounce-body.json")
    @Debounce(waiting = 600, header = {"User-Agent"}, body = true, reuse = true)
    public R<Object> debounceBody(@RequestParam String p, @RequestBody Q<String> q) {
        return R.ok(p + "::" + seq.getAndIncrement() + "::" + q.getQ());
    }
}
