package com.i000.stock.user.api.entity.bo;

import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Objects;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 12:56 2018/4/27
 * @Modified By:
 */
public class MailBo {
    private MimeMessage mimeMessage;
    private StringBuffer bodyText = new StringBuffer();

    public MailBo(MimeMessage mimeMessage) {
        this.mimeMessage = mimeMessage;
    }


    public String getSubject() throws Exception {
        return mimeMessage.getSubject();
    }

    /**
     * 获取发件人
     *
     * @return
     * @throws Exception
     */
    public String getFrom() throws Exception {
        InternetAddress[] address = (InternetAddress[]) mimeMessage.getFrom();
        String from = address[0].getAddress();
        String personal = address[0].getPersonal();
        return Objects.nonNull(from) || Objects.nonNull(personal)
                ? personal + "<" + from + ">" : "";
    }

    /**
     * 获取邮件的日期
     *
     * @return
     * @throws Exception
     */
    public LocalDate getDate() throws Exception {
        Instant instant = mimeMessage.getSentDate().toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        return instant.atZone(zoneId).toLocalDate();
    }

    /**
     * 将邮件的内容保存到StringBuffer中
     *
     * @param part
     * @throws Exception
     */
    public void init(Part part) throws Exception {
        String contentType = part.getContentType();
        int nameIndex = contentType.indexOf("name");
        boolean conName = false;
        if (nameIndex != -1) {
            conName = true;
        }
        if (part.isMimeType("text/plain") && conName == false) {
            bodyText.append((String) part.getContent()).append("\r\n");
        } else if (part.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) part.getContent();
            int counts = multipart.getCount();
            for (int i = 0; i < counts; i++) {
                init(multipart.getBodyPart(i));
            }
        }
    }

    /**
     * 获取邮件的正文
     *
     * @return
     */
    public String getBodyText() {
        return bodyText.toString();
    }

}
