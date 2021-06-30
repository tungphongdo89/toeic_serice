package com.migi.toeic.respositories;

import com.migi.toeic.dto.RoleDTO;
import com.migi.toeic.model.Role;
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
public class RoleRepository extends HibernateRepository<Role, Long> {

	@Autowired
	private EntityManager entityManager;

	@Transactional
	public List<RoleDTO> getRoleByUserID(Long userId) {

		StringBuilder sql = new StringBuilder(
				"select r.ID roleId,r.CODE roleCode,r.NAME roleName from USERS u join USER_ROLES ur on u.USER_ID = ur.USER_ID \n" +
						"join ROLES r on r.ID = ur.ROLE_ID where r.STATUS = 1 and u.USER_ID = :userId");
		NativeQuery query = currentSession().createNativeQuery(sql.toString());

		query.addScalar("roleId", new LongType());
		query.addScalar("roleName", new StringType());
		query.addScalar("roleCode", new StringType());

		query.setParameter("userId", userId);
		query.setResultTransformer(Transformers.aliasToBean(RoleDTO.class));
		return query.list();
	}

}
