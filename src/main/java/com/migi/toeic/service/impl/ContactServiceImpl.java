package com.migi.toeic.service.impl;

import com.migi.toeic.authen.model.RequestContact;
import com.migi.toeic.exception.BusinessException;
import com.migi.toeic.service.ContactService;
import com.migi.toeic.utils.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class ContactServiceImpl implements ContactService {

    @Autowired
    EmailServiceImpl emailService;

    @Autowired
    Environment environment;

    @Override
    @Transactional
    public String sendContact(RequestContact requestContact) {
            if(requestContact.getEmail().matches("^[a-z][a-z0-9_\\.]{0,32}@[a-z0-9]{2,}(\\.[a-z0-9]{2,4}){1,2}$")){
                String contextForm = "Dear mr/ms "+ requestContact.getName().trim() + ".\n" +
                        "Your inquiry has been sent to us:\n" +
                        "\""+requestContact.getContext().trim()+"\""+"\n" +
                        "We will send you the answer as soon as possible.\n" +
                        "Sincerely thank.";
                String lstCcTo[] = {environment.getProperty("adminEmail")};
                emailService.sendEmailMessageWithCC(requestContact.getEmail(), "Contact from Toeic_Web", contextForm,lstCcTo,requestContact.getEmail());
//                return environment.getProperty("contact_success");
                return MessageUtils.getMessage("send_contact_success");
            }else{
                String s = environment.getProperty("error_format_email");
                throw new BusinessException(MessageUtils.getMessage("send_contact_success"));
            }
    }
}
