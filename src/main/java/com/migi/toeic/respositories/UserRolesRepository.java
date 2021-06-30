package com.migi.toeic.respositories;

import com.migi.toeic.model.UserRole;
import com.migi.toeic.respositories.common.HibernateRepository;
import org.hibernate.query.NativeQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.swing.text.html.parser.Entity;
import javax.transaction.Transactional;

@Repository
@Transactional
public class UserRolesRepository extends HibernateRepository<UserRole,Long> {
    @Autowired
    private EntityManager entityManager;

    @Transactional
    public void createUserRole(long userId,long roleId,long status,String description){
        StringBuilder sql = new StringBuilder(
                "Insert Into user_roles(USER_ID,ROLE_ID,STATUS,DESCRIPTION) " +
                        "Values(?,?,?,?)"
        );
        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.setParameter(1,userId);
        query.setParameter(2,roleId);
        query.setParameter(3,status);
        query.setParameter(4,description);
        query.executeUpdate();
    }
}
