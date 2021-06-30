package com.migi.toeic.respositories;

import com.migi.toeic.base.ResultDataDTO;
import com.migi.toeic.dto.SysUserDTO;
import com.migi.toeic.model.SysUser;
import com.migi.toeic.respositories.common.HibernateRepository;
import com.migi.toeic.utils.ValidateUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Repository
@Transactional
public class UserRepository extends HibernateRepository<SysUser, Long> {

    @Autowired
    private EntityManager entityManager;

    @Transactional
    public ResultDataDTO getUsers(SysUserDTO obj) {

        StringBuilder sql = new StringBuilder(
                "SELECT \n" +
                        "USERS.USER_ID userId, \n" +
                        "USERS.USER_NAME userName ,\n" +
                        "USERS.STATUS status,\n" +
                        "USERS.USER_SHOW_NAME userShowName,\n" +
                        "USERS.USER_AVATAR userAvatar,\n" +
                        "USERS.CREATE_DATE createDate,\n" +
                        "USERS.UPDATE_DATE updateDate,\n" +
                        "USERS.NEAREST_LOGIN_DATE nearestLoginDate,\n" +
                        "USERS.TARGET target,\n" +
                        "ROLES.NAME userType\n" +
                        "FROM \n" +
                        "\tUSERS inner join USER_ROLES\n" +
                        "\ton USERS.USER_ID = USER_ROLES.USER_ID\n" +
                        "    inner join ROLES\n" +
                        "    on USER_ROLES.ROLE_ID = ROLES.ID\n" +
                        "where 1=1  ");

        if (StringUtils.isNotBlank(obj.getUserName())) {
            sql.append(" and USER_NAME  like :userName  escape '&'");
        }

        if (obj.getStatus() != null) {
            sql.append(" and status  =:status ");
        }

        if (StringUtils.isNotBlank(obj.getKeySearch())) {
            sql.append(" and USER_NAME  like :keySearch escape '&'");
        }

        if (StringUtils.isNotBlank(obj.getUserShowName())) {
            sql.append(" and USER_SHOW_NAME  like :userShowName escape '&'");
        }

        if (obj.getCreateDate() != null) {
            sql.append(" and CREATE_DATE  = :createDate ");
        }

        if (obj.getUpdateDate() != null) {
            sql.append(" and UPDATE_DATE  = :updateDate ");
        }

        if (obj.getNearestLoginDate() != null) {
            sql.append(" and NEAREST_LOGIN_DATE  = :nearestLoginDate ");
        }

        if (obj.getTypeId() != null) {
            sql.append(" and ROLES.ID = :typeId ");
        }

        if (obj.getTarget() != null) {
            sql.append(" and TARGET  = :target ");
        }


        sql.append(" order by USERS.USER_ID");
        NativeQuery query = currentSession().createNativeQuery(sql.toString());


        StringBuilder sqlCount = new StringBuilder("SELECT COUNT(*) FROM (");
        sqlCount.append(sql.toString());
        sqlCount.append(") total");

        NativeQuery queryCount = currentSession().createNativeQuery(sqlCount.toString());

        query.addScalar("userId", new LongType());
        query.addScalar("userName", new StringType());
        query.addScalar("status", new LongType());
        query.addScalar("userShowName", new StringType());
        query.addScalar("userAvatar", new StringType());
        query.addScalar("createDate", new TimestampType());
        query.addScalar("updateDate", new TimestampType());
        query.addScalar("nearestLoginDate", new TimestampType());
        query.addScalar("target", new LongType());
        query.addScalar("userType", new StringType());

        if (StringUtils.isNotEmpty(obj.getKeySearch())) {
            query.setParameter("keySearch", "%" + ValidateUtils.validateKeySearch(obj.getKeySearch()) + "%");
            queryCount.setParameter("keySearch", "%" + ValidateUtils.validateKeySearch(obj.getKeySearch()) + "%");
        }

        if (StringUtils.isNotBlank(obj.getUserName())) {
            query.setParameter("userName", "%" + ValidateUtils.validateKeySearch(obj.getUserName()) + "%");
            queryCount.setParameter("userName", "%" + ValidateUtils.validateKeySearch(obj.getUserName()) + "%");
        }

        if (StringUtils.isNotBlank(obj.getUserShowName())) {
            query.setParameter("userShowName", "%" + ValidateUtils.validateKeySearch(obj.getUserShowName()) + "%");
            queryCount.setParameter("userShowName", "%" + ValidateUtils.validateKeySearch(obj.getUserShowName()) + "%");
        }

        if (obj.getStatus() != null) {
            query.setParameter("status", obj.getStatus());
            queryCount.setParameter("status", obj.getStatus());
        }

        if (obj.getCreateDate() != null) {
            query.setParameter("createDate", obj.getCreateDate());
            queryCount.setParameter("createDate", obj.getCreateDate());
        }

        if (obj.getUpdateDate() != null) {
            query.setParameter("updateDate", obj.getUpdateDate());
            queryCount.setParameter("updateDate", obj.getUpdateDate());
        }

        if (obj.getNearestLoginDate() != null) {
            query.setParameter("nearestLoginDate", obj.getNearestLoginDate());
            queryCount.setParameter("nearestLoginDate", obj.getNearestLoginDate());
        }

        if (obj.getTarget() != null) {
            query.setParameter("target", obj.getTarget());
            queryCount.setParameter("target", obj.getTarget());
        }

        if (obj.getTypeId() != null) {
            query.setParameter("typeId", obj.getTypeId());
            queryCount.setParameter("typeId", obj.getTypeId());
        }

        if (obj.getPage() != null && obj.getPageSize() != null) {
            query.setFirstResult((obj.getPage().intValue() - 1) * obj.getPageSize().intValue());
            query.setMaxResults(obj.getPageSize().intValue());
        }
        query.setResultTransformer(Transformers.aliasToBean(SysUserDTO.class));
        obj.setTotalRecord(((BigDecimal) queryCount.uniqueResult()).intValue());
        ResultDataDTO resultDataDTO = new ResultDataDTO();
        List<SysUserDTO> lst = query.list();
        resultDataDTO.setData(lst);
        resultDataDTO.setTotal(obj.getTotalRecord());
        return resultDataDTO;
    }

    @Transactional
    public SysUserDTO getUserById(long id) {
        StringBuilder sql = new StringBuilder(
                "SELECT \n" +
                        "USERS.USER_ID userId, \n" +
                        "USERS.USER_NAME userName ,\n" +
                        "USERS.STATUS status,\n" +
                        "USERS.PASS_WORD password, " +
                        "USERS.USER_SHOW_NAME userShowName,\n" +
                        "USERS.USER_AVATAR userAvatar,\n" +
                        "USERS.CREATE_DATE createDate,\n" +
                        "USERS.UPDATE_DATE updateDate,\n" +
                        "USERS.NEAREST_LOGIN_DATE nearestLoginDate,\n" +
                        "USERS.BIRTHDAY birthday, " +
                        "USERS.LEVEL_CODE levelCode, " +
                        "USERS.PAID_A_FEE paidAFee, " +
                        "USERS.PAYMENT_DATE paymentDate, " +
                        "USERS.TIME_EXPIRED timeExpired, " +
                        "USERS.PHONE phone, " +
                        "USERS.CURRENT_SCORE currentScore, " +
                        "USERS.PAYMENT_STATUS paymentStatus, " +
                        "USERS.VERIFIED verified, " +
                        "USERS.TARGET target,\n" +
                        "USERS.CODE_USER userCode, " +
                        "ROLES.NAME userType\n" +
                        "FROM \n" +
                        "\tUSERS inner join USER_ROLES\n" +
                        "\ton USERS.USER_ID = USER_ROLES.USER_ID\n" +
                        "    inner join ROLES\n" +
                        "    on USER_ROLES.ROLE_ID = ROLES.ID WHERE USERS.USER_ID = :userId"
        );
        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("userId", new LongType());
        query.addScalar("userName", new StringType());
        query.addScalar("status", new LongType());
        query.addScalar("password", new StringType());
        query.addScalar("userShowName", new StringType());
        query.addScalar("userAvatar", new StringType());
        query.addScalar("createDate", new DateType());
        query.addScalar("updateDate", new DateType());
        query.addScalar("nearestLoginDate", new DateType());
        query.addScalar("birthday", new DateType());
        query.addScalar("levelCode", new LongType());
        query.addScalar("paidAFee", new DoubleType());
        query.addScalar("paymentDate", new DateType());
        query.addScalar("timeExpired", new DateType());
        query.addScalar("phone", new StringType());
        query.addScalar("currentScore", new LongType());
        query.addScalar("paymentStatus", new LongType());
        query.addScalar("target", new LongType());
        query.addScalar("userCode", new StringType());
        query.addScalar("userType", new StringType());
        query.addScalar("verified", new LongType());
        query.setParameter("userId", id);
        query.setResultTransformer(Transformers.aliasToBean(SysUserDTO.class));
        SysUserDTO user = (SysUserDTO) query.uniqueResult();
        return user;
    }

    @Transactional
    public SysUserDTO getProfileDetailById(long id) {
        StringBuilder sql = new StringBuilder(
                "SELECT \n" +
                        "USERS.USER_ID userId, \n" +
                        "USERS.USER_NAME userName ,\n" +
                        "USERS.USER_SHOW_NAME userShowName,\n" +
                        "USERS.USER_AVATAR userAvatar,\n" +
                        "USERS.TARGET target,\n" +
                        "USERS.CURRENT_SCORE currentScore,\n" +
                        "USERS.PAYMENT_STATUS paymentStatus,\n" +
                        "USERS.PHONE phone,\n" +
                        "ROLES.NAME userType\n" +
                        "FROM \n" +
                        "\tUSERS inner join USER_ROLES\n" +
                        "\ton USERS.USER_ID = USER_ROLES.USER_ID\n" +
                        "    inner join ROLES\n" +
                        "    on USER_ROLES.ROLE_ID = ROLES.ID WHERE USERS.USER_ID = :userId"
        );
        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("userId", new LongType());
        query.addScalar("userName", new StringType());
        query.addScalar("userShowName", new StringType());
        query.addScalar("userAvatar", new StringType());
        query.addScalar("target", new LongType());
        query.addScalar("currentScore", new LongType());
        query.addScalar("paymentStatus", new LongType());
        query.addScalar("phone", new StringType());
        query.addScalar("userType", new StringType());
        query.setParameter("userId", id);
        query.setResultTransformer(Transformers.aliasToBean(SysUserDTO.class));
        SysUserDTO user = (SysUserDTO) query.uniqueResult();
        return user;
    }

    @Transactional
    public SysUserDTO isUserNameExist(SysUserDTO obj, boolean isCreate) {
        StringBuilder sql = new StringBuilder(
                "Select USER_NAME as userName FROM users WHERE USER_NAME = :userName "
        );
        if (!isCreate) {
            sql.append("AND USER_ID <> :userId");
        }
        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("userName", new StringType());
        query.setResultTransformer(Transformers.aliasToBean(SysUserDTO.class));
        query.setParameter("userName", obj.getUserName());
        if (!isCreate) {
            query.setParameter("userId", obj.getUserId());
        }
        return (SysUserDTO) query.uniqueResult();
    }

    @Transactional
    public ResultDataDTO getListAllUser(SysUserDTO sysUserDTO) {
        StringBuilder sql = new StringBuilder("Select distinct " +
                "us.USER_ID userId," +
                "us.CODE_USER userCode," +
                "us.USER_NAME userName," +
                "r.NAME userType," +
                "r.ID roleID," +
                "r.CODE codeRole," +
                "us.USER_SHOW_NAME userShowName," +
                "us.PHONE phone," +
                "us.USER_AVATAR userAvatar," +
                "us.STATUS status," +
                "us.PAYMENT_STATUS paymentStatus," +
                "us.PASS_WORD password," +
                "us.CREATE_DATE createDate," +
                "us.UPDATE_DATE updateDate," +
                "us.NEAREST_LOGIN_DATE nearestLoginDate," +
                "us.BIRTHDAY birthday," +
                "us.LEVEL_CODE levelCode," +
                "us.PAID_A_FEE paidAFee," +
                "us.PAYMENT_DATE paymentDate," +
                "us.TIME_EXPIRED timeExpired," +
                "us.TARGET target," +
                "us.CURRENT_SCORE currentScore, " +
                "us.VERIFIED verified " +
                "from USERS us left join USER_ROLES ur on us.user_id=ur.user_id inner " +
                "join ROLES r on r.id=ur.role_id where 1=1 ");
        if (StringUtils.isNotBlank(sysUserDTO.getKeySearch())) {
            sql.append(" and ( upper(us.CODE_USER) like upper(:userCode) escape '&' " +
                    " or upper(us.USER_NAME) like upper(:userName) escape '&' )" );
        }
        if (sysUserDTO.getStatus() != null) {
            if (sysUserDTO.getStatus() != 2L) {
                sql.append(" and us.STATUS =:status ");
            }
        }
        if (sysUserDTO.getRoleID() != null) {
            if (sysUserDTO.getRoleID().longValue() != 0l) {
                sql.append(" and r.ID =:roleID");
            }
        }
        if (sysUserDTO.getPaymentStatus() != null) {
            if (sysUserDTO.getPaymentStatus() != 2L) {
                sql.append(" and us.PAYMENT_STATUS =:paymentStatus ");
            }
        }
        if (sysUserDTO.getCurrentScore() != null) {
            sql.append(" and us.CURRENT_SCORE =:currentScore ");
        }
        if(sysUserDTO.getTarget() != null){
            sql.append(" and us.TARGET =:target ");
        }
        sql.append(" order by us.USER_ID DESC ");
        NativeQuery query = currentSession().createNativeQuery(sql.toString());

        StringBuilder sqlCount = new StringBuilder("SELECT COUNT(*) FROM (");
        sqlCount.append(sql.toString());
        sqlCount.append(") total");
        NativeQuery queryCount = currentSession().createNativeQuery(sqlCount.toString());
        query.addScalar("userId", new LongType());
        query.addScalar("userCode", new StringType());
        query.addScalar("codeRole", new StringType());
        query.addScalar("roleID", new LongType());
        query.addScalar("userName", new StringType());
        query.addScalar("userType", new StringType());
        query.addScalar("userShowName", new StringType());
        query.addScalar("phone", new StringType());
        query.addScalar("userAvatar", new StringType());
        query.addScalar("status", new LongType());
        query.addScalar("paymentStatus", new LongType());
        query.addScalar("password", new StringType());
        query.addScalar("createDate", new TimestampType());
        query.addScalar("updateDate", new TimestampType());
        query.addScalar("nearestLoginDate", new TimestampType());
        query.addScalar("birthday", new DateType());
        query.addScalar("levelCode", new LongType());
        query.addScalar("paidAFee", new DoubleType());
        query.addScalar("paymentDate", new TimestampType());
        query.addScalar("timeExpired", new DateType());
        query.addScalar("target", new LongType());
        query.addScalar("currentScore", new LongType());
        query.addScalar("verified", new LongType());
        if (StringUtils.isNotBlank(sysUserDTO.getKeySearch())) {
            query.setParameter("userCode", "%" + ValidateUtils.validateKeySearch(sysUserDTO.getKeySearch()) + "%");
            queryCount.setParameter("userCode", "%" + ValidateUtils.validateKeySearch(sysUserDTO.getKeySearch()) + "%");
            query.setParameter("userName", "%" + ValidateUtils.validateKeySearch(sysUserDTO.getKeySearch()) + "%");
            queryCount.setParameter("userName", "%" + ValidateUtils.validateKeySearch(sysUserDTO.getKeySearch()) + "%");
        }
        if (sysUserDTO.getStatus() != null) {
            if (sysUserDTO.getStatus().longValue() != 2L) {
                query.setParameter("status", sysUserDTO.getStatus());
                queryCount.setParameter("status", sysUserDTO.getStatus());
            }
        }
        if (sysUserDTO.getRoleID() != null) {
            if (sysUserDTO.getRoleID().longValue() != 0l) {
                query.setParameter("roleID", sysUserDTO.getRoleID());
                queryCount.setParameter("roleID", sysUserDTO.getRoleID());
            }
        }
        if (sysUserDTO.getPaymentStatus() != null) {
            if (sysUserDTO.getPaymentStatus() != 2L) {
                query.setParameter("paymentStatus", sysUserDTO.getPaymentStatus());
                queryCount.setParameter("paymentStatus", sysUserDTO.getPaymentStatus());
            }
        }
        if(sysUserDTO.getCurrentScore() != null){
            query.setParameter("currentScore", sysUserDTO.getCurrentScore());
            queryCount.setParameter("currentScore", sysUserDTO.getCurrentScore());
        }
        if(sysUserDTO.getTarget() != null){
            query.setParameter("target", sysUserDTO.getTarget());
            queryCount.setParameter("target", sysUserDTO.getTarget());
        }
        if (sysUserDTO.getPage() != null && sysUserDTO.getPageSize() != null) {
            query.setFirstResult((sysUserDTO.getPage().intValue() - 1) * sysUserDTO.getPageSize().intValue());
            query.setMaxResults(sysUserDTO.getPageSize().intValue());
        }
        query.setResultTransformer(Transformers.aliasToBean(SysUserDTO.class));
        sysUserDTO.setTotalRecord(((BigDecimal) queryCount.uniqueResult()).intValue());
        ResultDataDTO resultDataDTO = new ResultDataDTO();
        List<SysUserDTO> lst = query.list();
        resultDataDTO.setData(lst);
        resultDataDTO.setTotal(sysUserDTO.getTotalRecord());
        return resultDataDTO;
    }

	public SysUserDTO getUserByName(String name) {

		StringBuilder sql = new StringBuilder(
				"SELECT \n" +
						"USERS.USER_ID userId, \n" +
						"USERS.USER_NAME userName ,\n" +
						"USERS.STATUS status,\n" +
						"USERS.USER_SHOW_NAME userShowName,\n" +
						"USERS.USER_AVATAR userAvatar,\n" +
						"USERS.CREATE_DATE createDate,\n" +
						"USERS.UPDATE_DATE updateDate,\n" +
						"USERS.PASS_WORD password,\n" +
						"USERS.NEAREST_LOGIN_DATE nearestLoginDate,\n" +
						"USERS.TARGET target,\n" +
						"ROLES.NAME userType,\n" +
						"ROLES.CODE codeRole " +
						"FROM \n" +
						"\tUSERS inner join USER_ROLES\n" +
						"\ton USERS.USER_ID = USER_ROLES.USER_ID\n" +
						"    inner join ROLES\n" +
						"    on USER_ROLES.ROLE_ID = ROLES.ID\n" +
						"where 1=1  ");

		if (StringUtils.isNotBlank(name)) {
			sql.append(" and USER_NAME  like :userName  escape '&'");
		}

		sql.append(" order by USERS.USER_ID");
		NativeQuery query = currentSession().createNativeQuery(sql.toString());

		query.addScalar("userId", new LongType());
		query.addScalar("userName", new StringType());
		query.addScalar("status", new LongType());
		query.addScalar("userShowName", new StringType());
		query.addScalar("userAvatar", new StringType());
		query.addScalar("createDate", new TimestampType());
		query.addScalar("updateDate", new TimestampType());
		query.addScalar("nearestLoginDate", new TimestampType());
		query.addScalar("target", new LongType());
		query.addScalar("userType", new StringType());
		query.addScalar("codeRole", new StringType());
		query.addScalar("password", new StringType());
		if (StringUtils.isNotBlank(name)) {
			query.setParameter("userName", "%" + ValidateUtils.validateKeySearch(name) + "%");
		}
		query.setResultTransformer(Transformers.aliasToBean(SysUserDTO.class));
		List<SysUserDTO> lst = query.list();
		return (SysUserDTO) query.uniqueResult();
	}

}
