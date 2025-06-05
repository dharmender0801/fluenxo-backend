package com.asc.auth.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import com.asc.auth.model.enums.SearchOperation;

public class QuerySpecificationBuilder {

	private List<SearchCriteria> params = new ArrayList<>();

	private Logger log = LoggerFactory.getLogger(this.getClass());

	public QuerySpecificationBuilder with(String key, SearchOperation op, Object value) {
		if (Objects.nonNull(op)) {
			params.add(new SearchCriteria(key, op, value, Boolean.FALSE));
		}
		return this;
	}

	public QuerySpecificationBuilder with(String key, List<String> joinKeys, SearchOperation op, Object value) {
		if (Objects.nonNull(op)) {
			params.add(new SearchCriteria(key, null, op, value, Boolean.FALSE, joinKeys));
		}
		return this;
	}

	public QuerySpecificationBuilder with(String key, List<String> joinKeys, SearchOperation op, Date dateOption) {
		if (Objects.nonNull(op)) {
			params.add(new SearchCriteria(key, joinKeys, op, Boolean.FALSE, dateOption));
		}
		return this;
	}

	public QuerySpecificationBuilder or(String key, SearchOperation op, Object value) {
		if (Objects.nonNull(op)) {
			params.add(new SearchCriteria(key, op, value, Boolean.TRUE));
		}
		return this;
	}

	public Specification<QuerySpecificationBuilder> build() {
		if (CollectionUtils.isEmpty(params)) {
			return null;
		}

		Specification<QuerySpecificationBuilder> result = null;
		for (SearchCriteria criteria : params) {
			log.debug("{}", criteria);
			if (Objects.isNull(result))
				result = new QuerySearchCriteria<>(criteria);
			else
				result = Boolean.TRUE.equals(criteria.getIsOrPredicate())
						? Specification.where(result).or(new QuerySearchCriteria<>(criteria))
						: Specification.where(result).and(new QuerySearchCriteria<>(criteria));
		}

		return result;
	}

}
