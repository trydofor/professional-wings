package pro.fessional.wings.example.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pro.fessional.wings.faceless.database.autogen.tables.SysCommitJournalTable;
import pro.fessional.wings.faceless.service.journal.JournalService;

/**
 * @author trydofor
 * @since 2019-06-30
 */

@Controller
@AllArgsConstructor
@Slf4j
public class TestJournalController {

    private final JournalService journalService;

    @RequestMapping("/string.json")
    @ResponseBody
    public String string() {
        JournalService.Journal journal = journalService.commit(SysCommitJournalTable.class);
        return journal.toString();
    }

    @RequestMapping("/journal.json")
    @ResponseBody
    public JournalService.Journal journal() {
        JournalService.Journal journal = journalService.commit(SysCommitJournalTable.class);
        return journal;
    }
}
