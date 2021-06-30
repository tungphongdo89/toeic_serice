package com.migi.toeic.rest;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.migi.toeic.authen.model.RequestContact;
import com.migi.toeic.authen.model.RequestLogin;
import com.migi.toeic.exception.BusinessException;
import com.migi.toeic.service.impl.ContactServiceImpl;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(description = "Contact e-mail")
@JsonRootName("snapshot")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ContactController {

    @Autowired
    ContactServiceImpl contactService;

    @PostMapping(value = "/contact")
    public ResponseEntity<?> getConTact(@RequestBody RequestContact requestContact){
        try {
            String sendContactMessage = contactService.sendContact(requestContact);
            return new ResponseEntity<>(sendContactMessage, HttpStatus.OK);
        }catch(BusinessException e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

}
