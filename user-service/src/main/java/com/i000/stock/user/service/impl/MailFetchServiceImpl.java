//package com.i000.stock.user.service.impl;
//
//import com.i000.stock.user.api.entity.bo.MailBo;
//import com.i000.stock.user.api.service.MailFetchService;
//import com.i000.stock.user.api.service.MailParseService;
//import com.i000.stock.user.service.impl.config.MailConfigure;
//import com.i000.stock.user.service.impl.mail.ParseToHold;
//import com.i000.stock.user.service.impl.mail.ParseToLine;
//import com.i000.stock.user.service.impl.mail.ParseToPlan;
//import com.i000.stock.user.service.impl.mail.ParseToTrade;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import javax.mail.Message;
//import javax.mail.internet.MimeMessage;
//import java.time.LocalDate;
//import java.util.Arrays;
//import java.util.Date;
//import java.util.List;
//
///**
// * @Author:qmfang
// * @Description:
// * @Date:Created in 12:44 2018/4/27
// * @Modified By:
// */
//@Slf4j
//@Component
//public class MailFetchServiceImpl implements MailFetchService {
//
//    @Autowired
//    private ParseToHold parseToHold;
//    @Autowired
//    private ParseToLine parseToLine;
//    @Autowired
//    private ParseToPlan parseToPlan;
//    @Autowired
//    private ParseToTrade parseToTrade;
//
//    private List<MailParseService> parseServiceList;
//
//    @PostConstruct
//    public void init() {
//        parseServiceList = Arrays.asList(parseToHold, parseToLine, parseToPlan, parseToTrade);
//    }
//
//    @Autowired
//    private MailConfigure mailConfigure;
//
//    @Override
//    public LocalDate initMail() throws Exception {
//        log.debug(new Date() + " Start receive the mail.......................");
//        mailConfigure.init();
//        Message[] messages = mailConfigure.createMessage();
//        LocalDate result = null;
//        for (Message message : messages) {
//            MailBo mailBo = new MailBo((MimeMessage) message);
//            System.out.println(mailBo.getSubject());
//            if (isAppointNewMail(mailBo)) {
//                mailBo.init(message);
//                String content = mailBo.getBodyText();
//                for (MailParseService mailParseService : parseServiceList) {
//                    result = mailParseService.save(content);
//                }
//            }
//        }
//        log.debug(new Date() + " mail parse end .......................");
//        return result;
//
//    }
//
//    private boolean isAppointNewMail(MailBo mailBo) throws Exception {
//        return mailBo.getFrom().contains(mailConfigure.getMonitor())
//                && LocalDate.now().equals(mailBo.getDate());
//    }
//}
