package com.asc.auth.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.asc.auth.model.enums.SearchOperation;


/**
 * The Class SearchCriteria.
 */
public class SearchCriteria implements Serializable {

	private static final long serialVersionUID = -3953995848021626367L;

	/** The key. */
	private String key;

	/** The join key. */
	private String joinKey;

	/** The operation. */
	private SearchOperation operation;

	/** The value. */
	private Object value;

	/** The is or predicate. */
	private Boolean isOrPredicate = Boolean.FALSE;

	private List<String> multiJoinKeys = new ArrayList<>();

	private Date dateOption;

	/**
	 * Instantiates a new search criteria.
	 *
	 * @param key           the key
	 * @param operation     the operation
	 * @param value         the value
	 * @param isOrPredicate the is or predicate
	 */
	public SearchCriteria(String key, SearchOperation operation, Object value, Boolean isOrPredicate) {
		this.key = key;
		this.operation = operation;
		this.value = value;
		this.isOrPredicate = isOrPredicate;
	}

	public SearchCriteria(String key, List<String> multiJoinKeys, SearchOperation operation, Boolean isOrPredicate,
			Date dateOption) {
		this.key = key;
		this.multiJoinKeys = multiJoinKeys;
		this.operation = operation;
		this.isOrPredicate = isOrPredicate;
		this.dateOption = dateOption;
	}

	/**
	 * Instantiates a new search criteria.
	 *
	 * @param key           the key
	 * @param joinKey       the join key
	 * @param operation     the operation
	 * @param value         the value
	 * @param isOrPredicate the is or predicate
	 */
	public SearchCriteria(String key, String joinKey, SearchOperation operation, Object value, Boolean isOrPredicate) {
		this.key = key;
		this.joinKey = joinKey;
		this.operation = operation;
		this.value = value;
		this.isOrPredicate = isOrPredicate;
	}

	public SearchCriteria(String key, String joinKey, SearchOperation operation, Object value, Boolean isOrPredicate,
			List<String> multiJoinKeys) {
		this.key = key;
		this.joinKey = joinKey;
		this.operation = operation;
		this.value = value;
		this.isOrPredicate = isOrPredicate;
		this.multiJoinKeys = multiJoinKeys;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public SearchOperation getOperation() {
		return operation;
	}

	public void setOperation(SearchOperation operation) {
		this.operation = operation;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Boolean getIsOrPredicate() {
		return isOrPredicate;
	}

	public void setIsOrPredicate(Boolean isOrPredicate) {
		this.isOrPredicate = isOrPredicate;
	}

	public String getJoinKey() {
		return joinKey;
	}

	public void setJoinKey(String joinKey) {
		this.joinKey = joinKey;
	}

	public List<String> getMultiJoinKeys() {
		return multiJoinKeys;
	}

	public void setMultiJoinKeys(List<String> multiJoinKeys) {
		this.multiJoinKeys = multiJoinKeys;
	}

	public Date getDateOption() {
		return dateOption;
	}

	public void setDateOption(Date dateOption) {
		this.dateOption = dateOption;
	}

	@Override
	public String toString() {
		return "SearchCriteria [key=" + key + ", joinKey=" + joinKey + ", operation=" + operation + ", value=" + value
				+ ", isOrPredicate=" + isOrPredicate + ", multiJoinKeys=" + multiJoinKeys + ", dateOption=" + dateOption
				+ "]";
	}

}