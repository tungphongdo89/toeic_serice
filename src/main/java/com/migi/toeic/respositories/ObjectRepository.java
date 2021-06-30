package com.migi.toeic.respositories;


import com.migi.toeic.dto.ObjectDTO;
import com.migi.toeic.model.Object;
import com.migi.toeic.respositories.common.HibernateRepository;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class ObjectRepository extends HibernateRepository<Object, Long> {
    @Autowired
    private EntityManager entityManager;

    @Transactional
    public List<ObjectDTO> getObjectByUserID(Long userId) {

        StringBuilder sql = new StringBuilder(
                "SELECT\n" +
                "    role_objects.object_id AS id,\n" +
                "    objects.TITLE AS title,\n" +
                "    objects.TYPE AS type,\n" +
                "    objects.ICON AS icon,\n" +
                "    objects.NAVLINK AS navLink,\n" +
                "    objects.STATUS AS status\n" +
                "FROM role_objects join objects\n" +
                "ON role_objects.object_id = objects.id\n" +
                "join \n" +
                "    (SELECT roles.id AS role_id FROM roles\n" +
                "        join user_roles on roles.id = user_roles.role_id\n" +
                "        join users on user_roles.user_id = users.user_id\n" +
                "        WHERE users.user_id= :userId AND roles.status=1\n" +
                "    ) roles\n" +
                "on role_objects.role_id = roles.role_id\n" +
                "WHERE objects.status=1\n");

        NativeQuery query = currentSession().createNativeQuery(sql.toString());

        query.addScalar("id", new LongType());
        query.addScalar("title", new StringType());
        query.addScalar("type", new StringType());
        query.addScalar("icon", new StringType());
        query.addScalar("navLink", new StringType());
        query.addScalar("status", new LongType());

        query.setParameter("userId", userId);
        query.setResultTransformer(Transformers.aliasToBean(ObjectDTO.class));
        return query.list();
    }

}
