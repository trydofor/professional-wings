package pro.fessional.wings.tiny.mail.service;

import pro.fessional.mirana.page.PageQuery;
import pro.fessional.mirana.page.PageResult;

/**
 * @author trydofor
 * @since 2023-01-07
 */
public interface TinyMailListService {

    /**
     * 获取全部邮件的简要信息，默认倒序
     */
    PageResult<TinyMailPlain> listAll(PageQuery pq);

    /**
     * 获取失败邮件的简要信息，默认倒序
     */
    PageResult<TinyMailPlain> listFailed(PageQuery pq);

    /**
     * 获取未成功邮件的简要信息，默认倒序
     */
    PageResult<TinyMailPlain> listUndone(PageQuery pq);

    /**
     * 根据Biz-Mark获取邮件的简要信息，默认倒序
     */
    PageResult<TinyMailPlain> listByBizMark(String mark, PageQuery pq);

    /**
     * 根据正则比较收件人to/cc/bcc获取邮件的简要信息，默认倒序
     */
    PageResult<TinyMailPlain> listByRecipient(String mailRegex, PageQuery pq);

    /**
     * 根据收件人from获取邮件的简要信息，默认倒序
     */
    PageResult<TinyMailPlain> listBySender(String mail, PageQuery pq);

    /**
     * 根据正则比较邮件标题获取邮件的简要信息，默认倒序
     */
    PageResult<TinyMailPlain> listBySubject(String subjRegex, PageQuery pq);

    /**
     * 获取邮件详情
     */
    TinyMailPlain loadDetail(long id);
}
