package com.asc.auth.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class QuerySearchCriteria<T> implements Specification<T> {

	private static final long serialVersionUID = -7918496306157839185L;

	private SearchCriteria criteria;

	private transient Logger log = LoggerFactory.getLogger(this.getClass());

	public QuerySearchCriteria(SearchCriteria searchCriteria) {
		this.criteria = searchCriteria;
	}

	@Override
	public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
		query.distinct(true);
		switch (criteria.getOperation()) {
		case DISTINCT:
			query.distinct(true);
			return null;
		case EQUALITY:
			return builder.equal(root.get(criteria.getKey()), criteria.getValue());
		case NEGATION:
			return builder.notEqual(root.get(criteria.getKey()), criteria.getValue());
		case GREATER_THAN_EQUALS:
			return builder.greaterThanOrEqualTo(root.<String>get(criteria.getKey()), criteria.getValue().toString());
		case GREATER_THAN_EQUALS_INT:
			return builder.greaterThanOrEqualTo(root.<Integer>get(criteria.getKey()), (Integer) criteria.getValue());
		case DATE_EQUALS:
			return builder.equal(root.<Date>get(criteria.getKey()), (Date) criteria.getValue());
		case DATE_GREATER_THAN_EQUALS:
			return builder.greaterThanOrEqualTo(root.<Date>get(criteria.getKey()), (Date) criteria.getValue());
		case DATE_GREATER_THAN:
			return builder.greaterThan(root.<Date>get(criteria.getKey()), (Date) criteria.getValue());
		case JOIN_MULTI_DATE_GREATER_THAN:
			Path<Date> datepath = root.get(criteria.getKey());
			if (!CollectionUtils.isEmpty(criteria.getMultiJoinKeys())) {
				for (String key : criteria.getMultiJoinKeys()) {
					datepath = datepath.get(key);
				}
			}
			log.info(criteria.getDateOption().toString());
			return builder.greaterThanOrEqualTo(datepath, criteria.getDateOption());
		case JOIN_MULTI_DATE_LESS_THAN:
			Path<Date> datepathless = root.get(criteria.getKey());
			if (!CollectionUtils.isEmpty(criteria.getMultiJoinKeys())) {
				for (String key : criteria.getMultiJoinKeys()) {
					datepathless = datepathless.get(key);
				}
			}
			log.info(criteria.getDateOption().toString());
			return builder.lessThanOrEqualTo(datepathless, criteria.getDateOption());
		case GREATER_THAN:
			return builder.greaterThan(root.<String>get(criteria.getKey()), criteria.getValue().toString());
		case LESS_THAN_EQUAL:
			return builder.lessThanOrEqualTo(root.<String>get(criteria.getKey()), criteria.getValue().toString());
		case DATE_LESS_THAN_EQUAL:
			return builder.lessThanOrEqualTo(root.<Date>get(criteria.getKey()), (Date) criteria.getValue());
		case DATE_LESS_THAN:
			return builder.lessThan(root.<Date>get(criteria.getKey()), (Date) criteria.getValue());
		case LESS_THAN:
			return builder.lessThan(root.<String>get(criteria.getKey()), criteria.getValue().toString());
		case LIKE:
			return builder.like(root.<String>get(criteria.getKey()), criteria.getValue().toString());
		case STARTS_WITH:
			return builder.like(root.<String>get(criteria.getKey()), criteria.getValue() + "%");
		case ENDS_WITH:
			return builder.like(root.<String>get(criteria.getKey()), "%" + criteria.getValue());
		case CONTAINS:
			return builder.like(root.<String>get(criteria.getKey()), "%" + criteria.getValue() + "%");
		case CONTAINS_FLOAT:
			return builder.like(root.<String>get(criteria.getKey()), "'%" + criteria.getValue().toString() + "%'");
		case NOT_NULL:
			return builder.isNotNull(root.<String>get(criteria.getKey()));
		case IS_NULL:
			return builder.isNull(root.<String>get(criteria.getKey()));
		case GROUP_BY:
			query.groupBy(root.<String>get(criteria.getKey()));
			return null;
		case LONGIN:
			String[] values = String.valueOf(criteria.getValue()).split(",");
			log.info(criteria.getValue().toString());
			List<Long> searchValues = new ArrayList<>();
			for (String val : values)
				searchValues.add(Long.valueOf(val));
			log.info(searchValues.toString());
			return builder.in(root.get(criteria.getKey())).value(searchValues);
		case INTEGERIN:
			String[] intValues = String.valueOf(criteria.getValue()).split(",");
			log.info(criteria.getValue().toString());
			List<Integer> intSearchValues = new ArrayList<>();
			for (String val : intValues)
				intSearchValues.add(Integer.valueOf(val));
			log.info(intSearchValues.toString());
			return builder.in(root.get(criteria.getKey())).value(intSearchValues);
		case STRINGIN:
			return builder.in(root.get(criteria.getKey()))
					.value(Arrays.asList(String.valueOf(criteria.getValue()).split(",")));
		case STRING_NOT_IN:
			return builder
					.not(root.get(criteria.getKey()).in(Arrays.asList(String.valueOf(criteria.getValue()).split(","))));
		case LONG_NOT_IN:

			String[] longValues = String.valueOf(criteria.getValue()).split(",");
			log.info(criteria.getValue().toString());
			List<Long> longNotSearchValues = new ArrayList<>();
			for (String val : longValues)
				longNotSearchValues.add(Long.valueOf(val));
			log.info(longNotSearchValues.toString());
			return builder.not(root.get(criteria.getKey()).in(longNotSearchValues));

		case STRING_NOT_IN_MULTI_JOIN:
			Path<String> path = root.get(criteria.getKey());
			if (!CollectionUtils.isEmpty(criteria.getMultiJoinKeys())) {
				for (String key : criteria.getMultiJoinKeys()) {
					path = path.get(key);
				}
			}
			return builder.not(path.in(Arrays.asList(String.valueOf(criteria.getValue()).split(","))));
		case JOIN_MULTI_STRING_LIST:
			path = root.get(criteria.getKey());
			if (!CollectionUtils.isEmpty(criteria.getMultiJoinKeys())) {
				for (String key : criteria.getMultiJoinKeys()) {
					path = path.get(key);
				}
			}
			return builder.like(path, criteria.getValue().toString() + "%");
		case JOIN_MULTI_INTEGER_LIST:
		case JOIN_MULTI_STRING:
			path = root.get(criteria.getKey());
			if (!CollectionUtils.isEmpty(criteria.getMultiJoinKeys())) {
				for (String key : criteria.getMultiJoinKeys()) {
					path = path.get(key);
				}
			}
			return builder.equal(path, criteria.getValue());
		case JOIN_MULTI_INTEGER_LIST_IN:
			Path<Object> pathObj = root.get(criteria.getKey());
			if (!CollectionUtils.isEmpty(criteria.getMultiJoinKeys())) {
				for (String key : criteria.getMultiJoinKeys()) {
					pathObj = pathObj.get(key);
				}
			}
			intSearchValues = new ArrayList<>();
			intValues = String.valueOf(criteria.getValue()).split(",");
			for (String val : intValues)
				intSearchValues.add(Integer.valueOf(val));
			return builder.in(pathObj).value(intSearchValues);
		case JOIN_MULTI_LONG_LIST_IN:
			List<Long> longSearchValues = new ArrayList<>();
			pathObj = root.get(criteria.getKey());
			if (!CollectionUtils.isEmpty(criteria.getMultiJoinKeys())) {
				for (String key : criteria.getMultiJoinKeys()) {
					pathObj = pathObj.get(key);
				}
			}
			longSearchValues = new ArrayList<>();
			intValues = String.valueOf(criteria.getValue()).split(",");
			for (String val : intValues)
				longSearchValues.add(Long.valueOf(val));
			return builder.in(pathObj).value(longSearchValues);
		case JOIN_MULTI_STRING_IN:
			pathObj = root.get(criteria.getKey());
			if (!CollectionUtils.isEmpty(criteria.getMultiJoinKeys())) {
				for (String key : criteria.getMultiJoinKeys()) {
					pathObj = pathObj.get(key);
				}
			}
			return builder.in(pathObj).value(Arrays.asList(((String) criteria.getValue()).split(",")));
		case JOIN_MULTI_INTEGER_LIST_NOT_EQ:
			pathObj = root.get(criteria.getKey());
			if (!CollectionUtils.isEmpty(criteria.getMultiJoinKeys())) {
				for (String key : criteria.getMultiJoinKeys()) {
					pathObj = pathObj.get(key);
				}
			}
			return builder.notEqual(pathObj, criteria.getValue());
		case JOIN_MULTI_STRING_LIST_NOT_EQ:
			pathObj = root.get(criteria.getKey());
			if (!CollectionUtils.isEmpty(criteria.getMultiJoinKeys())) {
				for (String key : criteria.getMultiJoinKeys()) {
					pathObj = pathObj.get(key);
				}
			}
			return builder.notEqual(pathObj, criteria.getValue());
		case JOIN_MULTI_INTEGER_LIST_NOT_IN:
			pathObj = root.get(criteria.getKey());
			if (!CollectionUtils.isEmpty(criteria.getMultiJoinKeys())) {
				for (String key : criteria.getMultiJoinKeys()) {
					pathObj = pathObj.get(key);
				}
			}
			intSearchValues = new ArrayList<>();
			intValues = String.valueOf(criteria.getValue()).split(",");
			for (String val : intValues)
				intSearchValues.add(Integer.valueOf(val));
			return builder.not(pathObj.in(intSearchValues));
		default:
			return null;
		}
	}
}
