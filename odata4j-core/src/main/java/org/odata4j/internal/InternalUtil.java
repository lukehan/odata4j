package org.odata4j.internal;

import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.core4j.Enumerable;
import org.core4j.Func1;
import org.core4j.Funcs;
import org.core4j.ThrowingFunc1;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISOPeriodFormat;
import org.odata4j.core.Guid;
import org.odata4j.core.NamedValue;
import org.odata4j.core.ODataConstants;
import org.odata4j.core.ODataVersion;
import org.odata4j.core.OEntity;
import org.odata4j.core.OLink;
import org.odata4j.core.OProperty;
import org.odata4j.core.ORelatedEntitiesLinkInline;
import org.odata4j.core.ORelatedEntityLink;
import org.odata4j.edm.EdmType;
import org.odata4j.producer.inmemory.BeanModel;
import org.odata4j.stax2.XMLEventReader2;
import org.odata4j.stax2.XMLFactoryProvider2;
import org.odata4j.stax2.XMLInputFactory2;

public class InternalUtil {

	// Since not everybody seems to adhere to the spec, we are trying to be
	// tolerant against different formats
	// spec says:
	// Edm.DateTime: yyyy-mm-ddThh:mm[:ss[.fffffff]]
	// Edm.DateTimeOffset: yyyy-mm-ddThh:mm[:ss[.fffffff]](('+'|'-')hh':'mm)|'Z'
	private static final Pattern DATETIME_PATTERN =
			Pattern.compile("(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2})(:\\d{2})?(\\.\\d{1,7})?((?:(?:\\+|\\-)\\d{2}:\\d{2})|Z)?");

	private static final DateTimeFormatter[] DATETIME_PARSER = new DateTimeFormatter[] {
			// formatter for parsing of dateTime and dateTimeOffset
			DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm"),
			DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss"),
			null /* illegal format @see parseDateTime */,
			DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"),
			DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mmZZ"),
			DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZZ"),
			null /* illegal format @see parseDateTime */,
			DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ")
	};
	private static final DateTimeFormatter[] DATETIME_FORMATTER = new DateTimeFormatter[] {
			// formatter for formatting of dateTimeOffset
			DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm"),
			DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss"),
			DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS0000"),
			DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS0000"),
			DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mmZZ"),
			DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZZ"),
			DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS0000ZZ"),
			DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS0000ZZ")
	};

	public static DateTime parseDateTime(String value) {
		Matcher matcher = DATETIME_PATTERN.matcher(value);

		if (matcher.matches()) {
			String dateTime = matcher.group(1);
			String seconds = matcher.group(2);
			String nanoSeconds = matcher.group(3);
			String timezone = matcher.group(4);

			int idx = (seconds != null ? 1 : 0)
					+ (nanoSeconds != null ? 2 : 0)
					+ (timezone != null ? 4 : 0);

			StringBuilder valueToParse = new StringBuilder(dateTime);
			if (seconds != null)
				valueToParse.append(seconds);

			// we know only about milliseconds not nanoseconds
			if (nanoSeconds != null) {
				if (nanoSeconds.length() > 4) {
					valueToParse.append(nanoSeconds.substring(0,
							Math.min(nanoSeconds.length(), 4)));
				} else {
					valueToParse.append(nanoSeconds);
				}
			}

			if (timezone != null) {
				if ("Z".equals(timezone)) {
					timezone = "+00:00";
				}
				valueToParse.append(timezone);
			}

			DateTimeFormatter formatter = DATETIME_PARSER[idx];
			if (formatter != null) {
				return formatter.parseDateTime(valueToParse.toString());
			}
		}
		throw new IllegalArgumentException("Illegal datetime format " + value);
	}

	public static String formatDateTime(LocalDateTime dateTime) {
		if (dateTime == null)
			return null;

		int idx = dateTime.getMillisOfSecond() > 0 ? 2 : 1;
		return dateTime.toString(DATETIME_FORMATTER[idx]);
	}

	public static String formatDateTimeOffset(DateTime dateTime) {
		if (dateTime == null)
			return null;

		int idx = 4	+ (dateTime.getMillisOfSecond() > 0 ? 2 : 1);
		return dateTime.toString(DATETIME_FORMATTER[idx]);
	}

	public static XMLEventReader2 newXMLEventReader(Reader reader) {
		XMLInputFactory2 f = XMLFactoryProvider2.getInstance()
				.newXMLInputFactory2();
		return f.createXMLEventReader(reader);
	}

	public static String reflectionToString(final Object obj) {
		StringBuilder rt = new StringBuilder();
		Class<?> objClass = obj.getClass();
		rt.append(objClass.getSimpleName());
		rt.append('[');

		String content = Enumerable.create(objClass.getFields())
				.select(Funcs.wrap(new ThrowingFunc1<Field, String>() {
					public String apply(Field f) throws Exception {
						Object fValue = f.get(obj);
						return f.getName() + ":" + fValue;
					}
				})).join(",");

		rt.append(content);

		rt.append(']');
		return rt.toString();
	}

	public static String keyString(Object[] key) {

		String keyValue;
		if (key.length == 1) {
			keyValue = keyString(key[0], false);
		} else {
			keyValue = Enumerable.create(key)
					.select(new Func1<Object, String>() {
						public String apply(Object input) {
							return keyString(input, true);
						}
					}).join(",");
		}

		return "(" + keyValue + ")";
	}
	
	public static Object[] keyValues(String keyString, Class<?>[] types) {
		if (keyString == null
			|| !keyString.startsWith("(")
			|| !keyString.endsWith(")")) {
			throw new IllegalArgumentException("Illegal key string");
		}
			
		String str = keyString.substring(1, keyString.length() - 1);
		String[] key = str.split(",");
		Object[] keys = new Object[key.length];
		for (int i = 0, len = key.length; i < len; i++) {
			if (key[i].startsWith("'") && key[i].endsWith("'")) {
				key[i] = key[i].substring(1, key[i].length() - 1);
				key[i] = key[i].replace("''", "'");
				
				keys[i] = TypeConverter.convert(key[i], types[i]);
			}
		}
		
		return keys;
	}

	@SuppressWarnings("unchecked")
	private static final Set<Object> INTEGRAL_TYPES = Enumerable
			.create(Integer.class, Integer.TYPE, Long.class, Long.TYPE,
					Short.class, Short.TYPE).cast(Object.class).toSet();

	public static String keyString(Object key, boolean includePropName) {
		if (key instanceof Guid) {
			return "guid'" + key + "'";
		} else if (key instanceof String) {
			return "'" + ((String) key).replace("'", "''") + "'";
		} else if (key instanceof Long) {
			return key.toString() + "L";
		} else if (INTEGRAL_TYPES.contains(key.getClass())) {
			return key.toString();
		} else if (key instanceof NamedValue<?>) {
			NamedValue<?> namedValue = (NamedValue<?>) key;
			String value = keyString(namedValue.getValue(), false);

			if (includePropName)
				return namedValue.getName() + "=" + value;
			else
				return value;
		} else {
			return key.toString();
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T toEntity(Class<T> entityType, OEntity oe) {
		if (entityType.equals(OEntity.class))
			return (T) oe;
		else
			return (T) InternalUtil.toPojo(entityType, oe);
	}

	public static <T> T toPojo(Class<T> pojoClass, OEntity oe) {

		try {

			Constructor<T> defaultCtor = findDefaultDeclaredConstructor(pojoClass);
			if (defaultCtor == null)
				throw new RuntimeException(
						"Unable to find a default constructor for "
								+ pojoClass.getName());

			if (!defaultCtor.isAccessible())
				defaultCtor.setAccessible(true);

			T rt = defaultCtor.newInstance();

			final BeanModel beanModel = new BeanModel(pojoClass);

			for (OProperty<?> op : oe.getProperties()) {
				if (beanModel.canWrite(op.getName()))
					beanModel.setPropertyValue(rt, op.getName(), op.getValue());
			}

			for (OLink l : oe.getLinks()) {
				if (l instanceof ORelatedEntitiesLinkInline) {
					ORelatedEntitiesLinkInline ol = (ORelatedEntitiesLinkInline) l;
					final String collectionName = ol.getTitle();
					if (beanModel.canWrite(ol.getTitle())) {
						Collection<Object> relatedEntities = ol
								.getRelatedEntities() == null
								? null
								: Enumerable.create(ol.getRelatedEntities())
										.select(new Func1<OEntity, Object>() {
											@Override
											public Object apply(OEntity input) {
												return toPojo(
														beanModel
																.getCollectionElementType(collectionName),
														input);
											}
										}).toList();
						beanModel.setCollectionValue(rt, collectionName,
								relatedEntities);
					}
				} else if (l instanceof ORelatedEntityLink) {
					// TODO set entity
				}
			}

			return rt;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	@SuppressWarnings("unchecked")
	private static <T> Constructor<T> findDefaultDeclaredConstructor(
			Class<T> pojoClass) {
		for (Constructor<?> ctor : pojoClass.getDeclaredConstructors()) {
			if (ctor.getParameterTypes().length == 0)
				return (Constructor<T>) ctor;
		}
		return null;
	}

	public static String getEntityRelId(List<String> keyPropertyNames,
			final List<OProperty<?>> entityProperties, String entitySetName) {
		String key = null;
		if (keyPropertyNames != null) {
			Object[] keyProperties = Enumerable.create(keyPropertyNames)
					.select(new Func1<String, OProperty<?>>() {
						public OProperty<?> apply(String input) {
							for (OProperty<?> entityProperty : entityProperties)
								if (entityProperty.getName().equals(input))
									return entityProperty;
							throw new IllegalArgumentException("Key property '"
									+ input + "' is invalid");
						}
					}).cast(Object.class).toArray(Object.class);
			key = InternalUtil.keyString(keyProperties);
		}

		return entitySetName + key;

	}

	public static String getEntityRelId(final OEntity oe) {
		String key = null;
		if (oe.getEntitySet().type.keys != null) {
			Object[] keyProperties = Enumerable
					.create(oe.getEntitySet().type.keys)
					.select(new Func1<String, OProperty<?>>() {

						public OProperty<?> apply(final String input) {
							
							for (OProperty<?> entityProperty : oe.getProperties()) 
								if (entityProperty.getName().equals(input)) 
									return entityProperty;
									

							if (oe.getEntityKey() != null && oe.getEntityKey().isSingleValued()) {
								return new OProperty<Object>() {

									@Override
									public String getName() {
										return input;
									}

									@Override
									public Object getValue() {
										return oe.getEntityKey().asSingleValue();
									}

									@Override
									public EdmType getType() {
										return null;
									}
								};
							}

							throw new IllegalArgumentException("Key property '" + input + "' is invalid");
						}
					}).cast(Object.class).toArray(Object.class);

			key = InternalUtil.keyString(keyProperties);
		}

		return oe.getEntitySet().name + key;
	}

	public static String toString(DateTime utc) {
		return utc.toString("yyyy-MM-dd'T'HH:mm:ss'Z'");
	}
	
	public static LocalTime parseTime(String value) {
		Period period = ISOPeriodFormat.standard().parsePeriod(value);
		return new LocalTime(period.toStandardDuration().getMillis(), DateTimeZone.UTC);
	}
	
	public static String toString(LocalTime time) {
		return ISOPeriodFormat.standard().print(new Period(time.getMillisOfDay()));
	}
	
	public static void sleep(long millis){
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static ODataVersion getDataServiceVersion(String headerValue) {
        ODataVersion version = ODataConstants.DATA_SERVICE_VERSION;
        if (headerValue != null) {
        	String[] str = headerValue.split(";");
        	version = ODataVersion.parse(str[0]);
        }
        return version;
	}
}
