package pro.fessional.wings.tiny.mail.service;

import pro.fessional.mirana.page.PageQuery;
import pro.fessional.mirana.page.PageResult;

/**
 * @author trydofor
 * @since 2023-01-07
 */
public interface TinyMailListService {

    /**
     * list summary of all messages, in reverse order by default.
     */
    PageResult<TinyMailPlain> listAll(PageQuery pq);

    /**
     * list summary of failed emails, in reverse order by default.
     */
    PageResult<TinyMailPlain> listFailed(PageQuery pq);

    /**
     * list summary of unsuccessful emails, in reverse order by default.
     */
    PageResult<TinyMailPlain> listUndone(PageQuery pq);

    /**
     * find summary of the email by Biz-Mark, in reverse order by default.
     */
    PageResult<TinyMailPlain> listByBizMark(String mark, PageQuery pq);

    /**
     * find summary of the email by RegExp of to/cc/bcc, reverse order by default.
     */
    PageResult<TinyMailPlain> listByRecipient(String mailRegex, PageQuery pq);

    /**
     * find summary of the email by from, in reverse order by default.
     */
    PageResult<TinyMailPlain> listBySender(String mail, PageQuery pq);

    /**
     * find summary of the email by RegExp of subject, reverse order by default.
     */
    PageResult<TinyMailPlain> listBySubject(String subjRegex, PageQuery pq);

    /**
     * get mail detail
     */
    TinyMailPlain loadDetail(long id);
}
