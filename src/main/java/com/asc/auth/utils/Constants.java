package com.asc.auth.utils;

import java.math.BigDecimal;

public class Constants {
	public static final String DEVICE_TYPE = "DEVICE-TYPE";
	public static final String APP_VERSION = "VER";
	public static final String SUCCESS = "Success";
	public static final String NOT_FOUND = "Not Found";
	public static final Integer USER = 3;
	public static final Integer SUPER_ADMIN = 1;
	public static final String REQUEST_DATE_PATTEREN = "dd-MM-yyyy Z";
	public static final String REQUEST_TIME_PATTEREN = "HH:mm:ss Z";
	public static final String REQUEST_DATE_PATTEREN_WITHOUT_TZ = "dd-MM-yyyy";
	public static final String REQUEST_TIME_PATTEREN_WITHOUT_TZ = "HH:mm:ss";
	public static final String REQUEST_DATETIME_PATTEREN = "dd-MM-yyyy HH:mm:ss Z";
	public static final String REQUEST_DATETIME_PATTEREN_DTDC = "ddMMyyyy HHmm";
	public static final String REQUEST_TIMEZONE = "Asia/Kolkata";
	public static final String FAIL = "Request Failed";
	public static final Integer PENDING = 0;
	public static final Integer CREATED = 1;
	public static final String accessApiKey = "13D7BB322BE5D315576406";
	public static final String REQUEST_MORE_THAN_PERMITTED = "Requested Operation Not Allowed with current value.";
	public static final String EMPTY_OR_INCOMPLETE_REQUEST_BODY = "Incomplete or empty request received";
	public static final String PARTIAL_CONTENT_AVAILABLE = "Partial or No Success for the request";
	public static final String REQUEST_SIZE_GREATER_THAN_ALLOWED = "Request Size is greater than allowed size.";
	public static final int ONE = 1;
	public static final Integer HTTP_OK = 200;
	public static final Integer HTTP_FORBIDDEN = 403;
	public static final Integer HTTP_NOT_FOUND = 404;
	public static final String FILE_PATH_DELIMETER = "/";
	public static final String[] SHIPMENT_UPLOAD_SCHEMA = { "Order ID", "CustomerId", "Tracking ID", "Carrier", "Route",
			"Order Type", "First Name", "Last Name", "Address Type", "Address1", "Address2", "Zipcode", "City", "State",
			"Country", "Customer Email", "Customer Phone", "Special Instructions", "Delivery Target Date (MM/dd/yyyy)",
			"Delivery Target Time", "Product", "Quantity", "Bill To Name", "Bill To Address1", "Bill To Address2",
			"Bill To City", "Bill To State", "Bill To Country", "Bill To Zipcode", "Bill To Email", "Bill To Contact",
			"Service Type", "Company Name", "LOB", "Ship By Date", "Pick Date" };
	public static final Integer SHIPMENT_UPLOAD_SCHEMA_LENGTH = SHIPMENT_UPLOAD_SCHEMA.length;
	public static final Integer MINUS_ONE_INT = -1;
	public static final Integer ZERO_INT = 0;
	public static final Integer ONE_INT = 1;
	public static final Float ZERO_FLOAT = 0f;
	public static final Float ONE_FLOAT = 1f;
	public static final Integer ACTIVE = 1;
	public static final String USER_NOT_FOUND = "Bad Credentials or User not found. Please try again.";
	public static final String LMS_ERROR_CODE_2002 = "2002";
	public static final Integer TEN_INT = 10;
	public static final String CART_EMPTY = "User Cart is empty. No record to display.";
	public static final Long ZERO_LONG = 0L;
	public static final Integer ROLE_USER = 3;

	public static final String NULL_BUSINESSTYPE = "-1";
	public static final BigDecimal ZERO_BIG = BigDecimal.ZERO;
	public static final String LABEL_GENERATION_FAILED = "Label generation failed, please verify order details and try again.";
	public static final String ERROR = "Error in processing the request";
	public static final String RET_ORDER_PREFEIX = "RET";
	public static final Integer FORWARD_ORDER = 1;
	public static final Integer RETURN_ORDER = 2;

	public static final Integer INT_ZERO = 0;
	public static final Integer INT_ONE = 1;
	public static final Integer INT_TWO = 2;
	public static final Integer INT_THREE = 3;
	public static final Integer INT_FOUR = 4;
	public static final Integer INT_FIVE = 5;
	public static final Integer INT_SIX = 6;
	public static final Integer INT_SEVEN = 7;
	public static final Integer INT_EIGHT = 8;
	public static final Integer INT_NINE = 9;

	public static final Float FLOAT_ZERO = 0F;
	public static final Float FLOAT_ONE = 1F;
	public static final Float FLOAT_TWO = 2F;
	public static final Float FLOAT_THREE = 3F;
	public static final Float FLOAT_FOUR = 4F;
	public static final Float FLOAT_FIVE = 5F;
	public static final Float FLOAT_SIX = 6F;
	public static final Float FLOAT_SEVEN = 7F;
	public static final Float FLOAT_EIGHT = 8F;
	public static final Float FLOAT_NINE = 9F;

	public static final Long LONG_ZERO = 0L;
	public static final Long LONG_ONE = 1L;
	public static final Long LONG_TWO = 2L;
	public static final Long LONG_THREE = 3L;
	public static final Long LONG_FOUR = 4L;
	public static final Long LONG_FIVE = 5L;
	public static final Long LONG_SIX = 6L;
	public static final Long LONG_SEVEN = 7L;
	public static final Long LONG_EIGHT = 8L;
	public static final Long LONG_NINE = 9L;
	public static final String CONTAINER_NOT_VALIDATED = "Container cannot be validated. Please use another container.";

	public static final Integer INVENTORY_RCV_PENDING = 0;
	public static final Integer INVENTORY_RCV_PROCESSING = 1;
	public static final Integer INVENTORY_RECEIVED = 2;
	public static final Integer INVENTORY_AVAILABLE = 3;
	public static final Integer INVENTORY_ALLOCATED = 4;
	public static final Integer INVENTORY_ICQA = 5;
	public static final Integer INVENTORY_FEEZED = 6;

	public static final Integer STAGE_IN_ACTIVE_BIN = 1;
	public static final Integer STAGE_IN_CONTAINER = 2;
	public static final Integer STAGE_IN_BACKORDER_BIN = 3;
	public static final Integer STAGE_IN_HOLD_BIN = 4;
	public static final Integer STAGE_IN_STAGING_AREA = 5;

	public static final String PRODUCT_NULL_BATCH = "-1";
}
