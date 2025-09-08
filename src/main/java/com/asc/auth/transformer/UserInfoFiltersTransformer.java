package com.asc.auth.transformer;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.springframework.data.jpa.domain.Specification;

import com.asc.auth.dto.FiltersDto;
import com.asc.auth.model.enums.FilterKeys;
import com.asc.auth.model.enums.SearchOperation;
import com.asc.auth.model.enums.UserType;
import com.asc.auth.utils.QuerySpecificationBuilder;
import com.asc.auth.utils.Utils;

public class UserInfoFiltersTransformer {

	public static Specification<?> buildCriteria(List<FiltersDto> filters) {
		QuerySpecificationBuilder qb = new QuerySpecificationBuilder();
		if (Boolean.TRUE.equals(Utils.checkCollectionIsNotEmpty(filters))) {
			filters.forEach(filter -> {
				if (FilterKeys.VENDOR_ID_IN.equals(filter.getKey())) {
					filterByVendorIn(qb, filter.getKey(), filter.getValue());
				} else if (FilterKeys.ACCOUNT_ID_IN.equals(filter.getKey())) {
					filterByAccountIdIn(qb, filter.getKey(), filter.getValue());
				} else if (FilterKeys.USER_ID_IN.equals(filter.getKey())) {
					filterByUserIdIn(qb, filter.getKey(), filter.getValue());
				} else if (FilterKeys.USER_TYPE.equals(filter.getKey())) {
					filterByUserType(qb, filter.getKey(), filter.getValue());
				} else if (FilterKeys.SKILL_IN.equals(filter.getKey())) {
					filterBySkillIdIn(qb, filter.getKey(), filter.getValue());
				} else if (FilterKeys.USER_NAME.equals(filter.getKey())) {
					filterByUserName(qb, filter.getKey(), filter.getValue());
				} else if (FilterKeys.EXPERIENCE_IN.equals(filter.getKey())) {
					filterByExperience(qb, filter.getKey(), filter.getValue());
				}

			});
		}
		return qb.build();
	}

	private static void filterByExperience(QuerySpecificationBuilder qb, FilterKeys key, String value) {
		if (FilterKeys.EXPERIENCE_IN.equals(key) && Objects.nonNull(value)) {
			qb.with("yearsOfExperience", SearchOperation.INTEGERIN, value);
		}
	}

	private static void filterByUserName(QuerySpecificationBuilder qb, FilterKeys key, String value) {
		if (FilterKeys.USER_NAME.equals(key) && Objects.nonNull(value)) {
			qb.with("name", SearchOperation.CONTAINS, value);
		}

	}

	private static void filterBySkillIdIn(QuerySpecificationBuilder qb, FilterKeys key, String value) {
		if (FilterKeys.SKILL_IN.equals(key) && Objects.nonNull(value)) {
			qb.with("id", SearchOperation.LONGIN, value);
		}

	}

	private static void filterByUserType(QuerySpecificationBuilder qb, FilterKeys key, String value) {
		if (FilterKeys.USER_TYPE.equals(key) && Objects.nonNull(value)) {
			UserType userType = UserType.fromString(value.trim());
			qb.with("userType", SearchOperation.EQUALITY, userType);
		}

	}

	private static void filterByAccountIdIn(QuerySpecificationBuilder qb, FilterKeys key, String value) {
		if (FilterKeys.ACCOUNT_ID_IN.equals(key) && Objects.nonNull(value)) {
			qb.with("accountMapping", Arrays.asList("accountId"), SearchOperation.JOIN_MULTI_LONG_LIST_IN, value);
		}

	}

	private static void filterByUserIdIn(QuerySpecificationBuilder qb, FilterKeys key, String value) {
		if (FilterKeys.USER_ID_IN.equals(key) && Objects.nonNull(value)) {
			qb.with("id", SearchOperation.LONGIN, value);
		}
	}

	private static void filterByVendorIn(QuerySpecificationBuilder qb, FilterKeys key, String value) {
		if (FilterKeys.VENDOR_ID_IN.equals(key) && Objects.nonNull(value)) {
			qb.with("vendorMapping", Arrays.asList("vendorId"), SearchOperation.JOIN_MULTI_LONG_LIST_IN, value);
		}
	}
}
