package pro.fessional.wings.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pro.fessional.wings.faceless.service.journal.JournalService;

/**
 * @author trydofor
 * @since 2019-06-30
 */

@Controller
@Slf4j
@RequiredArgsConstructor
public class TestJournalController {

    private final JournalService journalService;

    @RequestMapping("/test/string.json")
    @ResponseBody
    public String string() {
        JournalService.Journal journal = journalService.commit(JournalService.class);
        return journal.toString();
    }

    @RequestMapping("/test/journal.json")
    @ResponseBody
    public JournalService.Journal journal() {
        JournalService.Journal journal = journalService.commit(JournalService.class);
        return journal;
    }
}
