package com.asc.auth.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Configuration;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Utils {
	private static final String[] HEADERS_TO_TRY = { "X-Forwarded-For", "X-Real-IP", "REMOTE_ADDR", "Proxy-Client-IP",
			"WL-Proxy-Client-IP", "HTTP_X_FORWARDED_FOR", "HTTP_X_FORWARDED", "HTTP_X_CLUSTER_CLIENT_IP",
			"HTTP_CLIENT_IP", "HTTP_FORWARDED_FOR", "HTTP_FORWARDED", "HTTP_VIA" };

	public static String getClientIpAddress(HttpServletRequest request) {
		for (String header : HEADERS_TO_TRY) {
			String ip = request.getHeader(header.toLowerCase());
			if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
				return ip;
			}
		}
		return request.getRemoteAddr();
	}

	public static boolean checkCollectionIsNotEmpty(Collection<?> collection) {
		return Boolean.FALSE.equals(CollectionUtils.isEmpty(collection));
	}

	public static String getUserBeaerToken(String strUserAuthUrl, String user, String credentials) {
		try {
			ObjectReader reader = new ObjectMapper().readerFor(Map.class);
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			headers.setContentType(MediaType.APPLICATION_JSON);
			Map<String, String> userAuth = new HashMap<>();
			userAuth.put("user", user);
			userAuth.put("credentials", credentials);
			HttpEntity<?> request = new HttpEntity<>(userAuth, headers);
			ResponseEntity<String> result = null;
			result = restTemplate.exchange(strUserAuthUrl, HttpMethod.POST, request, String.class);
			if (result.getStatusCode().is2xxSuccessful()
					&& Boolean.TRUE.equals(Objects.nonNull(result.getBody().getClass()))) { // NOSONAR
				Map<String, String> map = reader.readValue(result.getBody());
				log.debug("token: {}", map.get("accessToken"));
				return "Bearer " + map.get("accessToken");
			} else {
				return null;
			}
		} catch (Exception exp) {
			log.error(exp.getMessage());
			return null;
		}
	}

	public static String downloadFile(String sampleUrl) {
		URL urlObj = null;
		String filePath = System.getProperty("java.io.tmpdir") + Constants.FILE_PATH_DELIMETER + generateRandomId()
				+ ".pdf";
		try {
			urlObj = new URL(sampleUrl);
		} catch (MalformedURLException exp) {
			log.error(exp.getMessage());
		}
		try (FileOutputStream fOutStream1 = new FileOutputStream(filePath);
				ReadableByteChannel rbcObj1 = Channels.newChannel(urlObj.openStream());) {
			fOutStream1.getChannel().transferFrom(rbcObj1, 0, Long.MAX_VALUE);
			log.debug("! File {} Successfully Downloaded From The Url {} !", filePath, sampleUrl);
			return filePath;
		} catch (NullPointerException | IOException exp) {
			log.error(exp.getMessage());
			return null;
		}
	}

	public static String downloadFileAndReturnBase64Str(String sampleUrl) {
		URL urlObj = null;
		String filePath = System.getProperty("java.io.tmpdir") + Constants.FILE_PATH_DELIMETER + generateRandomId()
				+ ".pdf";
		Path filePathObj = Paths.get(filePath);
		if (Boolean.FALSE.equals(Files.exists(filePathObj))) {
			try {
				urlObj = new URL(sampleUrl);
			} catch (MalformedURLException exp) {
				log.error(exp.getMessage());
			}
			try (FileOutputStream fOutStream1 = new FileOutputStream(filePath);
					ReadableByteChannel rbcObj1 = Channels.newChannel(urlObj.openStream());) {
				fOutStream1.getChannel().transferFrom(rbcObj1, 0, Long.MAX_VALUE);
				log.debug("! File {} Successfully Downloaded From The Url {} !", filePath, sampleUrl);
			} catch (NullPointerException | IOException exp) {
				log.error(exp.getMessage());
			}
			File file = new File(filePath);
			byte[] bytes = null;
			try {
				bytes = Files.readAllBytes(file.toPath());
			} catch (IOException exp) {
				log.error(exp.getMessage());
			}
			String b64 = Base64.getEncoder().encodeToString(bytes);
			log.debug("Temp File delete: {}", file.delete());
			return b64;
		} else {
			log.error("File Already Present! Please Check!");
			return null;
		}
	}

	public static List<Date> getMinMaxDate(Date date) {
		List<Date> dateList = Arrays.asList(
				new DateTime(date).hourOfDay().withMinimumValue().minuteOfHour().withMinimumValue().secondOfMinute()
						.withMinimumValue().millisOfSecond().withMinimumValue().toDate(),
				new DateTime(date).hourOfDay().withMaximumValue().minuteOfHour().withMaximumValue().secondOfMinute()
						.withMaximumValue().millisOfSecond().withMaximumValue().toDate());
		log.debug("{}", dateList);
		return dateList;
	}

	public static Boolean compare(int x, int y) {
		return !(x < y) && (!(x == y));
	}

	public static String generateRandomId() {
		return UUID.randomUUID().toString();
	}

	public static void copyProperties(Object source, Object target) {
		BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
	}

	private static String[] getNullPropertyNames(Object source) {
		final BeanWrapper src = new BeanWrapperImpl(source);
		java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();
		Set<String> emptyNames = new HashSet<String>();
		for (java.beans.PropertyDescriptor pd : pds) {
			Object srcValue = src.getPropertyValue(pd.getName());
			if (srcValue == null)
				emptyNames.add(pd.getName());
		}
		String[] result = new String[emptyNames.size()];
		return emptyNames.toArray(result);
	}

	public static String trimSurrogateUnicodeCharacters(String text) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < text.length(); i++) {
			char ch = text.charAt(i);
			if (!Character.isHighSurrogate(ch) && !Character.isLowSurrogate(ch)) {
				sb.append(ch);
			}
		}
		return sb.toString();
	}

	public static boolean isBetween(double x, double lower, double upper) {
		return lower <= x && x <= upper;
	}

	public static Date getTimeWithoutDate(Date dateTime) {
		DateFormat formatter = new SimpleDateFormat("HH:mm:SS");
		Date timeWithoutDate = new Date();
		try {
			timeWithoutDate = formatter.parse(formatter.format(dateTime));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return timeWithoutDate;
	}

	public static Date getDateTimeFromString(String strDate, String formatter) {
		log.info("date String: {}, pattern: {}", strDate, formatter);
		try {
			Date dateTime = new SimpleDateFormat(formatter).parse(strDate);
			log.debug("dateTime: {}", dateTime);
			return dateTime;
		} catch (Exception e) {
			log.error(e.getMessage());
			return null;
		}
	}

	public static String getDateFormateByStringDate(String dateStr) {
		List<String> patterns = List.of("yyyy-MM-dd HH:mm:ss", Constants.REQUEST_DATE_PATTEREN,
				Constants.REQUEST_TIME_PATTEREN, Constants.REQUEST_DATE_PATTEREN_WITHOUT_TZ,
				Constants.REQUEST_TIME_PATTEREN_WITHOUT_TZ, Constants.REQUEST_DATETIME_PATTEREN, "dd-MMM-yy",
				"dd/MMM/yy", "dd.MM.yyyy", "dd-MM-yyyy HH:mm:ss");
		String formatter = patterns.parallelStream().map(pattern -> {
			SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
			try {
				dateFormat.parse(dateStr);
				return pattern;
			} catch (ParseException ignored) {
				return null;
			}
		}).filter(dateFormat -> dateFormat != null).findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Unknown date format: " + dateStr));

		return formatter;
	}

	public static String formatDateString(Date inputDate, String newPatternStr) {
		log.info("Original date to be formatted: {}", inputDate);
		DateTimeFormatter newPattern = DateTimeFormatter.ofPattern(newPatternStr);
		LocalDateTime datetime = LocalDateTime.ofInstant(inputDate.toInstant(), ZoneId.systemDefault());
		String output = datetime.format(newPattern);
		log.info("Formatted date Stirng: {}", output);
		return output;
	}

	public static Date getDateTimeFromEpoch(Long epoch) {
		log.debug("epoch: {}", epoch);
		LocalDateTime ldt = Instant.ofEpochMilli(epoch).atZone(ZoneId.of(Constants.REQUEST_TIMEZONE)).toLocalDateTime();
		return Date.from(ldt.atZone(ZoneId.of(Constants.REQUEST_TIMEZONE)).toInstant());
	}

	public static Boolean setFilePermissions(String fileName) throws IOException {
		Path path = Paths.get(fileName);
		if (!Files.exists(path))
			Files.createFile(path);
		Set<PosixFilePermission> perms = Files.readAttributes(path, PosixFileAttributes.class).permissions();
		perms.add(PosixFilePermission.OWNER_WRITE);
		perms.add(PosixFilePermission.OWNER_READ);
		perms.add(PosixFilePermission.OWNER_EXECUTE);
		perms.add(PosixFilePermission.GROUP_WRITE);
		perms.add(PosixFilePermission.GROUP_READ);
		perms.add(PosixFilePermission.GROUP_EXECUTE);
		perms.add(PosixFilePermission.OTHERS_WRITE);
		perms.add(PosixFilePermission.OTHERS_READ);
		perms.add(PosixFilePermission.OTHERS_EXECUTE);
		Files.setPosixFilePermissions(path, perms);
		return true;
	}

	public static String createSHACheckSum(String sourceString) throws NoSuchAlgorithmException, IOException {
		log.info("String for hash: {}", sourceString);
		MessageDigest md = MessageDigest.getInstance("SHA-512");
		byte[] dataBytes = sourceString.getBytes();
		md.update(dataBytes);
		byte[] mdbytes = md.digest(); // convert the byte to hex format method
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < mdbytes.length; i++) {
			sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
		}
		return sb.toString();
	}

	public static Validator createValidator() {
		Configuration<?> config = Validation.byDefaultProvider().configure();
		ValidatorFactory factory = config.buildValidatorFactory();
		Validator validator = factory.getValidator();
		factory.close();
		return validator;
	}

	public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
		Map<Object, Boolean> uniqueMap = new ConcurrentHashMap<>();
		return t -> uniqueMap.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

	public static String getTransactionId() {
		return UUID.randomUUID().toString();
	}

	public static Long getDifferenceHours(Date start, Date end) {
		long diff = end.getTime() - start.getTime();
		return TimeUnit.HOURS.convert(diff, TimeUnit.MILLISECONDS);
	}

	public static Long getDifferenceDays(Date start, Date end) {
		long diff = end.getTime() - start.getTime();
		return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
	}

	public static String toJsonString(Object... objects) {
		ObjectMapper mapperObj = new ObjectMapper();
		try {
			return mapperObj.writeValueAsString(objects);
		} catch (Exception e) {
			log.error("{}", e.getLocalizedMessage());
		}
		return null;
	}

	public static Long calculateMillisecond(Date start, Date end) {
		return (end.getTime() - start.getTime());
	}

	public static <T> List<List<T>> partitionList(List<T> list, int batchSize) {
		List<List<T>> batches = new ArrayList<>();
		for (int i = 0; i < list.size(); i += batchSize) {
			int end = Math.min(i + batchSize, list.size());
			batches.add(list.subList(i, end));
		}
		return batches;
	}

	public static <T> List<T> aggregateListBasedOnField(List<T> records, String sumField, Class<T> clazz) {
		try {
			// Get all declared fields of the class and filter out the sumField
			List<String> groupByFields = Arrays.stream(clazz.getDeclaredFields()).map(Field::getName)
					.filter(fieldName -> !fieldName.equals(sumField)).collect(Collectors.toList());

			// Determine the data type of the sumField
			Field sumFieldReflect = clazz.getDeclaredField(sumField);
			sumFieldReflect.setAccessible(true);
			Class<?> sumFieldType = sumFieldReflect.getType();

			// Group by the specified fields and sum the specified field
			Map<List<Object>, Number> groupedMap = records.stream()
					.collect(Collectors.groupingBy(
							record -> groupByFields.stream().map(fieldName -> getFieldValue(record, fieldName))
									.collect(Collectors.toList()),
							Collectors.reducing((Number) getDefaultValue(sumFieldType),
									record -> (Number) getFieldValue(record, sumField),
									(num1, num2) -> sumNumbers(num1, num2, sumFieldType))));

			// Create a new list of aggregated records
			List<T> aggregatedRecords = groupedMap.entrySet().stream().map(entry -> {
				try {
					// Create a new instance of the object
					T newInstance = clazz.getDeclaredConstructor().newInstance();

					// Set the group-by fields
					List<Object> key = entry.getKey();
					for (int i = 0; i < groupByFields.size(); i++) {
						setFieldValue(newInstance, groupByFields.get(i), key.get(i));
					}

					// Set the summed field
					setFieldValue(newInstance, sumField, entry.getValue());

					return newInstance;
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}).collect(Collectors.toList());

			return aggregatedRecords;

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static Object getFieldValue(Object record, String fieldName) {
		try {
			Field field = record.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			return field.get(record);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static void setFieldValue(Object record, String fieldName, Object value) {
		try {
			Field field = record.getClass().getDeclaredField(fieldName);
			// Skip static fields
			if ((field.getModifiers() & java.lang.reflect.Modifier.STATIC) != 0) {
				return;
			}
			field.setAccessible(true);
			field.set(record, value);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static Number getDefaultValue(Class<?> type) {
		if (type == Integer.class) {
			return 0;
		} else if (type == Long.class) {
			return 0L;
		} else if (type == Double.class) {
			return 0.0;
		} else if (type == Float.class) {
			return 0.0f;
		} else if (type == Short.class) {
			return 0;
		} else if (type == BigDecimal.class) {
			return BigDecimal.ZERO;
		} else {
			throw new IllegalArgumentException("Unsupported sum field type: " + type);
		}
	}

	private static Number sumNumbers(Number num1, Number num2, Class<?> type) {
		if (type == Integer.class) {
			return num1.intValue() + num2.intValue();
		} else if (type == Long.class) {
			return num1.longValue() + num2.longValue();
		} else if (type == Double.class) {
			return num1.doubleValue() + num2.doubleValue();
		} else if (type == Float.class) {
			return num1.floatValue() + num2.floatValue();
		} else if (type == Short.class) {
			return num1.shortValue() + num2.shortValue();
		} else if (type == BigDecimal.class) {
			return ((BigDecimal) num1).add((BigDecimal) num2);
		} else {
			throw new IllegalArgumentException("Unsupported sum field type: " + type);
		}
	}
}
