package com.sismics.docs.core.util.jpa;

import java.util.Map.Entry;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import com.sismics.util.context.ThreadLocalContext;

public class QueryUtil {
    public static Query getNativeQuery(QueryParam queryParam) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        Query query = em.createNativeQuery(queryParam.getQueryString());
        for (Entry<String, Object> entry : queryParam.getParameterMap().entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        return query;
    }
    
    public static QueryParam getSortedQueryParam(QueryParam queryParam, SortCriteria sortCriteria) {
        StringBuilder sb = new StringBuilder(queryParam.getQueryString());
        if (sortCriteria != null) {
            String queryString = queryParam.getQueryString().toLowerCase();
            
            String orderColumn;
            if (queryString.contains("from user_activity ua")) {
                // User Activity queries
                switch (sortCriteria.getColumn()) {
                    case 0: orderColumn = "ua.ID"; break;
                    case 1: orderColumn = "ua.USER_ID"; break;
                    case 2: orderColumn = "u.USE_USERNAME_C"; break;
                    case 4: orderColumn = "ua.ENTITY_ID"; break;
                    case 5: orderColumn = "d.DOC_TITLE_C"; break;
                    case 6: orderColumn = "ua.PROGRESS"; break;
                    case 7: orderColumn = "ua.DEADLINE"; break;
                    case 8: orderColumn = "ua.UTA_COMPLETED_DATE"; break;
                    case 9: orderColumn = "ua.CREATE_DATE"; break;
                    default: orderColumn = "ua.CREATE_DATE";
                }
            } else if (queryString.contains("from t_tag t")) {
                // Tag queries
                switch (sortCriteria.getColumn()) {
                    case 0: orderColumn = "t.TAG_ID_C"; break;
                    case 1: orderColumn = "t.TAG_NAME_C"; break;
                    case 2: orderColumn = "t.TAG_COLOR_C"; break;
                    case 3: orderColumn = "t.TAG_IDPARENT_C"; break;
                    case 4: orderColumn = "u.USE_USERNAME_C"; break;
                    default: orderColumn = "t.TAG_NAME_C";
                }
            } else if (queryString.contains("from t_document d")) {
                // Document queries
                switch (sortCriteria.getColumn()) {
                    case 0: orderColumn = "d.DOC_ID_C"; break;
                    case 1: orderColumn = "d.DOC_TITLE_C"; break;
                    case 2: orderColumn = "d.DOC_DESCRIPTION_C"; break;
                    case 3: orderColumn = "d.DOC_CREATEDATE_D"; break;
                    case 4: orderColumn = "d.DOC_UPDATEDATE_D"; break;
                    case 5: orderColumn = "u.USE_USERNAME_C"; break;
                    default: orderColumn = "d.DOC_CREATEDATE_D";
                }
            } else {
                orderColumn = "c" + sortCriteria.getColumn();
            }
            
            sb.append(" order by ");
            sb.append(orderColumn);
            sb.append(sortCriteria.isAsc() ? " asc" : " desc");
        }
        
        return new QueryParam(sb.toString(), queryParam.getParameterMap());
    }
}
