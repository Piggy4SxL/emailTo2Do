package sxl.mp.et2d.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import sxl.mp.et2d.service.MailService;

import javax.annotation.Resource;


/**
 * @author SxL
 *         Created on 2017/12/27.
 */
@Service
public class MailServiceImpl implements MailService {
    @Value("${spring.mail.username}")
    private String from;

    @Resource
    private JavaMailSender javaMailSender;

    /*发送邮件的方法*/
    @Override
    public void sendSimple(String to, String title, String content){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from); //发送者
        message.setCc(from);
        message.setTo(to); //接受者
        message.setSubject(title); //发送标题
        message.setText(content); //发送内容
        javaMailSender.send(message);
    }
}
