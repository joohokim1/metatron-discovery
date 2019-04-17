package app.metatron.discovery.domain.ipm.common.code.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPQLQuery;
import app.metatron.discovery.domain.ipm.common.code.domain.GroupCodeEntity;
import app.metatron.discovery.domain.ipm.common.code.domain.QGroupCodeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QueryDslRepositorySupport;

public class GroupCodeRepositoryImpl extends QueryDslRepositorySupport implements GroupCodeRepositoryCustom {


    public GroupCodeRepositoryImpl() {
        super(GroupCodeEntity.class);
    }


    @Override
    public Page<GroupCodeEntity> findQueryDslBySearchValuesWithPage(String type, String keyword, String column, Pageable pageable) {

        QGroupCodeEntity groupCodeEntity = QGroupCodeEntity.groupCodeEntity;
        JPQLQuery<GroupCodeEntity> query = from(groupCodeEntity);

        if (keyword != null && !"".equals(keyword)) {
            query.where(groupCodeEntity.groupNmKr.like("%" + keyword + "%"));
        }

        query = getQuerydsl().applyPagination(pageable, query);

        QueryResults<GroupCodeEntity> result = query.fetchResults();

        return new PageImpl<>(result.getResults(), pageable, result.getTotal());
    }
}
