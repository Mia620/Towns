package xaos.property;

/**
 * A setting in the towns.ini file with it's appropriate typing information.
 *
 * @author Florian Frankenberger
 */
public record Property<T>(PropertyFile propertyFile, String key, PropertyWrapper<T> propertyWrapper) {

}
