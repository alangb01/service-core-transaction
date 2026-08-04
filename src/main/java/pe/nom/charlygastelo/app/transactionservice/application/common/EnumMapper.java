package pe.nom.charlygastelo.app.transactionservice.application.common;

public class EnumMapper {

    public static <E extends Enum<E>> E safeValueOf(Class<E> enumClass, String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return Enum.valueOf(enumClass, value.toUpperCase());
        } catch (Exception ex) {
            return null;
        }
    }
}
