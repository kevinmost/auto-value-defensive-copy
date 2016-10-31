package other;

import com.kevinmost.auto.value.defensive_copy.adapter.DefensiveCopier;
import org.jetbrains.annotations.NotNull;

import java.util.Properties;

public class DefensiveCopiers {
  public static final class PropertiesCopier implements DefensiveCopier<Properties> {
    @NotNull @Override public Properties defensiveCopy(@NotNull Properties source) {
      final Properties properties = new Properties();
      properties.putAll(source);
      return properties;
    }
  }
}
