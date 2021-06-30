package com.migi.toeic.dto;


import com.migi.toeic.model.Object;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ObjectDTO {
    private Long id;
    private String title;
    private String type;
    private String icon;
    private String navLink;
    private Long status;

    public Object toModel() {
        Object object = new Object();
        object.setObjectId(this.id);
        object.setObjectTitle(this.title);
        object.setObjectType(this.type);
        object.setIcon(this.icon);
        object.setNavlink(this.navLink);
        object.setStatus(this.status);
        return object;
    }
}
