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

public class CampaignInfoFiltersTransformer {

	public static Specification<?> buildCriteria(List<FiltersDto> filters) {
		QuerySpecificationBuilder qb = new QuerySpecificationBuilder();
		if (Boolean.TRUE.equals(Utils.checkCollectionIsNotEmpty(filters))) {
			filters.forEach(filter -> {
				if (FilterKeys.VENDOR_ID_IN.equals(filter.getKey())) {
					filterByVendorIn(qb, filter.getKey(), filter.getValue());
				} else if (FilterKeys.CREATED_BY.equals(filter.getKey())) {
					filterByCreatedBy(qb, filter.getKey(), filter.getValue());
				} else if (FilterKeys.USER_ID_IN.equals(filter.getKey())) {
					filterByUserIdIn(qb, filter.getKey(), filter.getValue());
				}
			});
		}
		return qb.build();
	}

	private static void filterByUserIdIn(QuerySpecificationBuilder qb, FilterKeys key, String value) {
		if (FilterKeys.USER_ID_IN.equals(key) && Objects.nonNull(value)) {
			qb.with("associatedUsers", Arrays.asList("userId"), SearchOperation.JOIN_MULTI_LONG_LIST_IN, value);
		}

	}

	private static void filterByCreatedBy(QuerySpecificationBuilder qb, FilterKeys key, String value) {
		if (FilterKeys.CREATED_BY.equals(key) && Objects.nonNull(value)) {
			qb.with("createdBy", SearchOperation.LONGIN, value);
		}

	}

	private static void filterByVendorIn(QuerySpecificationBuilder qb, FilterKeys key, String value) {
		if (FilterKeys.VENDOR_ID_IN.equals(key) && Objects.nonNull(value)) {
			qb.with("vendorMapping", Arrays.asList("vendorId"), SearchOperation.JOIN_MULTI_LONG_LIST_IN, value);
		}
	}
}
