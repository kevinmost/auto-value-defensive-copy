package test;

import com.kevinmost.auto.value.defensive_copy.adapter.DefensiveCopy;
import java.lang.Override;
import java.util.Properties;
import org.jetbrains.annotations.NotNull;
import other.DefensiveCopiers;

final class AutoValue_CustomAdapterExternal extends $AutoValue_CustomAdapterExternal {
  @NotNull
  private static final DefensiveCopiers.PropertiesCopier configCopier = new DefensiveCopiers.PropertiesCopier();

  AutoValue_CustomAdapterExternal(Properties config) {
    super(config);
  }

  @DefensiveCopy
  @Override
  public Properties config() {
    final Properties tmp = super.config();
    if (tmp == null) {
      return null;
    }
    return configCopier.defensiveCopy(tmp);
  }
}
