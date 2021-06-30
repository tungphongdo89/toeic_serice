package com.migi.toeic.respositories;

import com.migi.toeic.base.ResultDataDTO;
import com.migi.toeic.dto.BannerDTO;
import com.migi.toeic.model.Banner;
import com.migi.toeic.respositories.common.HibernateRepository;
import com.migi.toeic.utils.ValidateUtils;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DateType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.hibernate.type.TimestampType;
import org.springframework.stereotype.Repository;
import org.apache.commons.lang3.StringUtils;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Repository
@Transactional
public class BannerRepository extends HibernateRepository<Banner, Long> {

    @Transactional
    public ResultDataDTO getBanners(BannerDTO obj) {

        StringBuilder sql = new StringBuilder(
                "select ID as bannerId," +
                        " NAME as bannerName ," +
                        " PATH_IMAGE as pathImage , " +
                        " POST_LINK as postLink," +
                        " ORDER_BANNER as orderBanner," +
                        " STATUS as status," +
                        " DESCRIPTION as description, " +
                        " CREATED_TIME as createdTime, " +
                        " LAST_UPDATE as lastUpdate " +
                        " from BANNERS where 1=1 ");

        if (StringUtils.isNotEmpty( obj.getBannerName() )) {
            sql.append( " and upper(NAME) LIKE upper(:nameSearch) escape '&'  " );
        }
        if (StringUtils.isNotEmpty( obj.getPathImage() )) {
            sql.append( " and upper(PATH_IMAGE) LIKE upper(:pathSearch) escape '&'  " );
        }

        if (StringUtils.isNotEmpty( obj.getPostLink() )) {
            sql.append( " and upper(POST_LINK) LIKE upper(:linkSearch) escape '&'  " );
        }

        if (obj.getOrderBanner() != null) {
            sql.append( " and ORDER_BANNER = :orderSearch " );
        }

        if (obj.getStatus() != null) {
            sql.append( " and STATUS = :status " );
        }
        if(obj.getCreatedTime()!=null){
            sql.append(" and CREATED_TIME = :createdTime ");
        }
        if(obj.getLastUpdate()!=null){
            sql.append(" and LAST_UPDATE = :lastUpdate ");
        }

        // Sắp xếp tăng dần theo order by
        sql.append(" order by ORDER_BANNER asc ");

        StringBuilder sqlCount = new StringBuilder("SELECT COUNT(*) FROM (" );
        sqlCount.append(sql.toString());
        sqlCount.append(" )");

        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        NativeQuery queryCount = currentSession().createNativeQuery(sqlCount.toString());

        query.addScalar("bannerId", new LongType());
        query.addScalar("bannerName", new StringType());
        query.addScalar("pathImage", new StringType());
        query.addScalar("postLink", new StringType());
        query.addScalar("description", new StringType());
        query.addScalar("orderBanner", new LongType());
        query.addScalar("status", new LongType());
        query.addScalar("createdTime", new TimestampType());
        query.addScalar("lastUpdate", new TimestampType());

        query.setResultTransformer(Transformers.aliasToBean(BannerDTO.class));

        if (obj.getPage() != null && obj.getPageSize() != null) {
            query.setFirstResult((obj.getPage().intValue() - 1) * obj.getPageSize().intValue());
            query.setMaxResults(obj.getPageSize().intValue());
        }

        if (StringUtils.isNotEmpty( obj.getBannerName() )) {
            query.setParameter( "nameSearch", "%" + ValidateUtils.validateKeySearch(obj.getBannerName()) + "%" );
            queryCount.setParameter( "nameSearch", "%" + ValidateUtils.validateKeySearch(obj.getBannerName()) + "%" );
        }

        if (StringUtils.isNotEmpty( obj.getPathImage() )) {
            query.setParameter( "pathSearch", "%" + ValidateUtils.validateKeySearch(obj.getPathImage()) + "%" );
            queryCount.setParameter( "pathSearch", "%" + ValidateUtils.validateKeySearch(obj.getPathImage()) + "%" );
        }

        if (StringUtils.isNotEmpty( obj.getPostLink() )) {
            query.setParameter( "linkSearch", "%" + ValidateUtils.validateKeySearch(obj.getPostLink()) + "%" );
            queryCount.setParameter( "linkSearch", "%" + ValidateUtils.validateKeySearch(obj.getPostLink()) + "%" );
        }

        if (obj.getOrderBanner() != null) {
            query.setParameter( "orderSearch", obj.getOrderBanner() );
            queryCount.setParameter( "orderSearch", obj.getOrderBanner() );
        }

        if (obj.getStatus() != null) {
            query.setParameter( "status", obj.getStatus() );
            queryCount.setParameter( "status", obj.getStatus() );
        }

        if (obj.getCreatedTime() != null) {
            query.setParameter( "createdTime", obj.getCreatedTime() );
            queryCount.setParameter( "createdTime", obj.getCreatedTime() );
        }

        if (obj.getOrderBanner() != null) {
            query.setParameter( "lastUpdate", obj.getLastUpdate() );
            queryCount.setParameter( "lastUpdate", obj.getLastUpdate() );
        }

        ResultDataDTO resultDataDTO = new ResultDataDTO();
        List<BannerDTO> lst = query.list();
        resultDataDTO.setData(lst);
        obj.setTotalRecord(((BigDecimal) queryCount.uniqueResult()).intValue());
        resultDataDTO.setTotal(obj.getTotalRecord());
        return resultDataDTO;
    }

    @Transactional
    public ResultDataDTO getBannersActive() {

        StringBuilder sql = new StringBuilder(
                "select ID as bannerId," +
                        " NAME as bannerName ," +
                        " PATH_IMAGE as pathImage , " +
                        " POST_LINK as postLink," +
                        " ORDER_BANNER as orderBanner," +
                        " STATUS as status," +
                        " LAST_UPDATE as lastUpdate, " +
                        " DESCRIPTION as description " +
                        " from BANNERS where 1=1 ");


        // Sắp xếp tăng dần theo order by
        sql.append(" and STATUS = 1 order by ORDER_BANNER asc ");

        StringBuilder sqlCount = new StringBuilder("SELECT COUNT(*) FROM (");
        sqlCount.append(sql.toString());
        sqlCount.append(")");

        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        NativeQuery queryCount = currentSession().createNativeQuery(sqlCount.toString());

        query.addScalar("bannerId", new LongType());
        query.addScalar("bannerName", new StringType());
        query.addScalar("pathImage", new StringType());
        query.addScalar("postLink", new StringType());
        query.addScalar("description", new StringType());
        query.addScalar("orderBanner", new LongType());
        query.addScalar("status", new LongType());
        query.addScalar("lastUpdate", new TimestampType());

        query.setResultTransformer(Transformers.aliasToBean(BannerDTO.class));


        ResultDataDTO resultDataDTO = new ResultDataDTO();
        List<BannerDTO> lst = query.list();
        resultDataDTO.setData(lst);
        resultDataDTO.setTotal(((BigDecimal) queryCount.uniqueResult()).intValue());
        return resultDataDTO;
    }


    @Transactional
    public ResultDataDTO getBannersNotActive(BannerDTO bannerDTO) {

        StringBuilder sql = new StringBuilder(
                "select ID as bannerId," +
                        " NAME as bannerName ," +
                        " PATH_IMAGE as pathImage , " +
                        " POST_LINK as postLink," +
                        " ORDER_BANNER as orderBanner," +
                        " STATUS as status," +
                        " CREATED_TIME as createdTime," +
                        " LAST_UPDATE as lastUpdate, " +
                        " DESCRIPTION as description " +
                        " from BANNERS where 1=1 ");


        if (StringUtils.isNotBlank(bannerDTO.getKeySearch())) {
            sql.append(" and (upper(banners.NAME)  like upper(:keySearch)  escape '&' )");
        }
        if ( bannerDTO.getCreateFrom() == null && bannerDTO.getCreateTo() != null){
            sql.append(" and  TO_CHAR(BANNERS.CREATED_TIME, 'YYYYMMDD') <= :createTo ");
        }
        if ( bannerDTO.getCreateFrom() != null && bannerDTO.getCreateTo() == null ){
            sql.append(" and  TO_CHAR(BANNERS.CREATED_TIME, 'YYYYMMDD') >= :createFrom ");
        }
        if ( bannerDTO.getCreateTo() != null && bannerDTO.getCreateFrom() != null){
            sql.append(" and  TO_CHAR(BANNERS.CREATED_TIME, 'YYYYMMDD') BETWEEN  :createFrom and  :createTo ");
        }
        // Sắp xếp tăng dần theo order by
        sql.append(" and STATUS = 0 order by NAME asc ");

        StringBuilder sqlCount = new StringBuilder("SELECT COUNT(*) FROM (");
        sqlCount.append(sql.toString());
        sqlCount.append(")");

        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        NativeQuery queryCount = currentSession().createNativeQuery(sqlCount.toString());

        query.addScalar("bannerId", new LongType());
        query.addScalar("bannerName", new StringType());
        query.addScalar("pathImage", new StringType());
        query.addScalar("postLink", new StringType());
        query.addScalar("description", new StringType());
        query.addScalar("orderBanner", new LongType());
        query.addScalar("status", new LongType());
        query.addScalar("createdTime", new TimestampType());
        query.addScalar("lastUpdate", new TimestampType());

        query.setResultTransformer(Transformers.aliasToBean(BannerDTO.class));

        if (bannerDTO.getPage() != null && bannerDTO.getPageSize() != null) {
            query.setFirstResult((bannerDTO.getPage().intValue() - 1) * bannerDTO.getPageSize().intValue());
            query.setMaxResults(bannerDTO.getPageSize().intValue());
        }

        if (StringUtils.isNotBlank(bannerDTO.getKeySearch())) {
            query.setParameter("keySearch", "%" + ValidateUtils.validateKeySearch(bannerDTO.getKeySearch()) + "%");
            queryCount.setParameter("keySearch", "%" + ValidateUtils.validateKeySearch(bannerDTO.getKeySearch()) + "%");
        }

        if ( bannerDTO.getCreateTo() != null){
            query.setParameter("createTo", bannerDTO.getCreateToString());
            queryCount.setParameter("createTo", bannerDTO.getCreateToString());
        }
        if (bannerDTO.getCreateFrom() != null){
            query.setParameter("createFrom", bannerDTO.getCreateFromString());
            queryCount.setParameter("createFrom", bannerDTO.getCreateFromString());
        }
        if ( bannerDTO.getCreateTo() != null && bannerDTO.getCreateFrom() != null){
            query.setParameter("createFrom", bannerDTO.getCreateFromString());
            queryCount.setParameter("createFrom", bannerDTO.getCreateFromString());
            query.setParameter("createTo", bannerDTO.getCreateToString());
            queryCount.setParameter("createTo", bannerDTO.getCreateToString());
        }

        ResultDataDTO resultDataDTO = new ResultDataDTO();
        List<BannerDTO> lst = query.list();
        resultDataDTO.setData(lst);
        resultDataDTO.setTotal(((BigDecimal) queryCount.uniqueResult()).intValue());
        return resultDataDTO;
    }

    @Transactional
    public ResultDataDTO getBannersActive(BannerDTO obj) {

        StringBuilder sql = new StringBuilder(
                "select ID as bannerId," +
                        " NAME as bannerName ," +
                        " PATH_IMAGE as pathImage , " +
                        " POST_LINK as postLink," +
                        " ORDER_BANNER as orderBanner," +
                        " STATUS as status," +
                        " DESCRIPTION as description, " +
                        " CREATED_TIME as createdTime, " +
                        " LAST_UPDATE as lastUpdate " +
                        " from BANNERS where 1=1 ");

        if (StringUtils.isNotEmpty( obj.getBannerName() )) {
            sql.append( " and upper(NAME) LIKE upper(:nameSearch) escape '&'  " );
        }
        if (StringUtils.isNotEmpty( obj.getPathImage() )) {
            sql.append( " and upper(PATH_IMAGE) LIKE upper(:pathSearch) escape '&'  " );
        }
        if (StringUtils.isNotEmpty( obj.getPostLink() )) {
            sql.append( " and upper(POST_LINK) LIKE upper(:linkSearch) escape '&'  " );
        }
        if (obj.getOrderBanner() != null) {
            sql.append( " and ORDER_BANNER = :orderSearch " );
        }
        if(obj.getCreatedTime()!=null){
            sql.append(" and CREATED_TIME = :createdTime ");
        }
        if(obj.getLastUpdate()!=null){
            sql.append(" and LAST_UPDATE = :lastUpdate ");
        }
        // Sắp xếp tăng dần theo order by
        sql.append(" and rownum <= 5 and STATUS = 1 order by ORDER_BANNER asc ");

        StringBuilder sqlCount = new StringBuilder("SELECT COUNT(*) FROM (");
        sqlCount.append(sql.toString());
        sqlCount.append(")");

        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        NativeQuery queryCount = currentSession().createNativeQuery(sqlCount.toString());

        query.addScalar("bannerId", new LongType());
        query.addScalar("bannerName", new StringType());
        query.addScalar("pathImage", new StringType());
        query.addScalar("postLink", new StringType());
        query.addScalar("description", new StringType());
        query.addScalar("orderBanner", new LongType());
        query.addScalar("status", new LongType());
        query.addScalar("createdTime", new TimestampType());
        query.addScalar("lastUpdate", new TimestampType());

        query.setResultTransformer(Transformers.aliasToBean(BannerDTO.class));

        if (obj.getPage() != null && obj.getPageSize() != null) {
            query.setFirstResult((obj.getPage().intValue() - 1) * obj.getPageSize().intValue());
            query.setMaxResults(obj.getPageSize().intValue());
        }

        if (StringUtils.isNotEmpty( obj.getBannerName() )) {
            query.setParameter( "nameSearch", "%" + ValidateUtils.validateKeySearch(obj.getBannerName()) + "%" );
            queryCount.setParameter( "nameSearch", "%" + ValidateUtils.validateKeySearch(obj.getBannerName()) + "%" );
        }

        if (StringUtils.isNotEmpty( obj.getPathImage() )) {
            query.setParameter( "pathSearch", "%" + ValidateUtils.validateKeySearch(obj.getPathImage()) + "%" );
            queryCount.setParameter( "pathSearch", "%" + ValidateUtils.validateKeySearch(obj.getPathImage()) + "%" );
        }

        if (StringUtils.isNotEmpty( obj.getPostLink() )) {
            query.setParameter( "linkSearch", "%" + ValidateUtils.validateKeySearch(obj.getPostLink()) + "%" );
            queryCount.setParameter( "linkSearch", "%" + ValidateUtils.validateKeySearch(obj.getPostLink()) + "%" );
        }

        if (obj.getOrderBanner() != null) {
            query.setParameter( "orderSearch", obj.getOrderBanner() );
            queryCount.setParameter( "orderSearch", obj.getOrderBanner() );
        }

        if (obj.getCreatedTime() != null) {
            query.setParameter( "createdTime", obj.getCreatedTime() );
            queryCount.setParameter( "createdTime", obj.getCreatedTime() );
        }

        if (obj.getOrderBanner() != null) {
            query.setParameter( "lastUpdate", obj.getLastUpdate() );
            queryCount.setParameter( "lastUpdate", obj.getLastUpdate() );
        }

        ResultDataDTO resultDataDTO = new ResultDataDTO();
        List<BannerDTO> lst = query.list();
        resultDataDTO.setData(lst);
        obj.setTotalRecord(((BigDecimal) queryCount.uniqueResult()).intValue());
        resultDataDTO.setTotal(obj.getTotalRecord());
        return resultDataDTO;
    }

    public BannerDTO getDetail(Long id){
        StringBuilder sql = new StringBuilder(
                "select ID as bannerId," +
                        " NAME as bannerName ," +
                        " PATH_IMAGE as pathImage , " +
                        " POST_LINK as postLink," +
                        " ORDER_BANNER as orderBanner," +
                        " STATUS as status," +
                        " DESCRIPTION as description, " +
                        " CREATED_TIME as createdTime, " +
                        " LAST_UPDATE as lastUpdate " +
                        " from BANNERS where 1=1 and ID = :idSearch ");
        NativeQuery query = currentSession().createNativeQuery(sql.toString());

        query.addScalar("bannerId", new LongType());
        query.addScalar("bannerName", new StringType());
        query.addScalar("pathImage", new StringType());
        query.addScalar("postLink", new StringType());
        query.addScalar("description", new StringType());
        query.addScalar("orderBanner", new LongType());
        query.addScalar("status", new LongType());
        query.addScalar("createdTime", new TimestampType());
        query.addScalar("lastUpdate", new TimestampType());

        query.setResultTransformer(Transformers.aliasToBean(BannerDTO.class));

        query.setParameter( "idSearch", id );

        return (BannerDTO) query.uniqueResult();
    }

    public List<BannerDTO> checkBannerName(String bannerName) {
        StringBuilder sql = new StringBuilder(
                "select ID as bannerId," +
                        " NAME as bannerName ," +
                        " PATH_IMAGE as pathImage , " +
                        " POST_LINK as postLink," +
                        " ORDER_BANNER as orderBanner," +
                        " STATUS as status," +
                        " DESCRIPTION as description, " +
                        " CREATED_TIME as createdTime, " +
                        " LAST_UPDATE as lastUpdate " +
                        " from BANNERS where upper(NAME) = upper (:bannerName) ");
        NativeQuery query = currentSession().createNativeQuery(sql.toString());

        query.addScalar("bannerId", new LongType());
        query.addScalar("bannerName", new StringType());
        query.addScalar("pathImage", new StringType());
        query.addScalar("postLink", new StringType());
        query.addScalar("description", new StringType());
        query.addScalar("orderBanner", new LongType());
        query.addScalar("status", new LongType());
        query.addScalar("createdTime",new TimestampType());
        query.addScalar("lastUpdate", new TimestampType());

        query.setResultTransformer(Transformers.aliasToBean(BannerDTO.class));

        query.setParameter("bannerName", bannerName );

        return query.list();
    }

    @Transactional
    public ResultDataDTO getListBanner() {
    	  StringBuilder sql = new StringBuilder(
                  "select ID as bannerId," +
                          " NAME as bannerName ," +
                          " PATH_IMAGE as pathImage , " +
                          " POST_LINK as postLink," +
                          " ORDER_BANNER as orderBanner," +
                          " STATUS as status," +
                          " DESCRIPTION as description " +
                          " from BANNERS");

    	    NativeQuery query = currentSession().createNativeQuery(sql.toString());

            query.addScalar("bannerId", new LongType());
            query.addScalar("bannerName", new StringType());
            query.addScalar("pathImage", new StringType());
            query.addScalar("postLink", new StringType());
            query.addScalar("description", new StringType());
            query.addScalar("orderBanner", new LongType());
            query.addScalar("status", new LongType());

            StringBuilder sqlCount = new StringBuilder("SELECT COUNT(*) FROM (");
            sqlCount.append(sql.toString());
            sqlCount.append(") total");
            NativeQuery queryCount = currentSession().createNativeQuery(sqlCount.toString());

            query.setResultTransformer(Transformers.aliasToBean(BannerDTO.class));

            ResultDataDTO resultDataDTO = new ResultDataDTO();
            resultDataDTO.setData(query.list());
            resultDataDTO.setTotal(((BigDecimal)queryCount.uniqueResult()).intValue());

            return resultDataDTO;
    }

    public List<BannerDTO> checkOrderBanner(Long orderBaner) {
        StringBuilder sql = new StringBuilder(
                "select ID as bannerId," +
                        " NAME as bannerName ," +
                        " PATH_IMAGE as pathImage , " +
                        " POST_LINK as postLink," +
                        " ORDER_BANNER as orderBanner," +
                        " STATUS as status," +
                        " DESCRIPTION as description, " +
                        " CREATED_TIME as createdTime, " +
                        " LAST_UPDATE as lastUpdate " +
                        " from BANNERS where ORDER_BANNER = :orderBaner ");
        NativeQuery query = currentSession().createNativeQuery(sql.toString());

        query.addScalar("bannerId", new LongType());
        query.addScalar("bannerName", new StringType());
        query.addScalar("pathImage", new StringType());
        query.addScalar("postLink", new StringType());
        query.addScalar("description", new StringType());
        query.addScalar("orderBanner", new LongType());
        query.addScalar("status", new LongType());
        query.addScalar("createdTime", new TimestampType());
        query.addScalar("lastUpdate",new TimestampType());

        query.setResultTransformer(Transformers.aliasToBean(BannerDTO.class));

        query.setParameter("orderBaner", orderBaner );

        return query.list();
    }

}
