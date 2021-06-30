package com.migi.toeic.model;

import com.migi.toeic.dto.ObjectDTO;
import lombok.Data;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "OBJECTS")
public class Object implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "objects")
    @SequenceGenerator(name = "objects",sequenceName = "OBJECTS_SEQ",allocationSize = 1)
    @Column(name = "ID", nullable = false)
    private Long objectId;
    @Column(name = "TITLE", nullable = false)
    private String objectTitle;
    @Column(name = "TYPE", nullable = true)
    private String objectType;
    @Column(name = "ICON", nullable = true)
    private String icon;
    @Column(name = "NAVLINK", nullable = true)
    private String navlink;
    @Column(name = "STATUS", nullable = true)
    private Long status;
}
