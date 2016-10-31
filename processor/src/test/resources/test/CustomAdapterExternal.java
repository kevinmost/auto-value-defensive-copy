package test;

import com.google.auto.value.AutoValue;
import com.kevinmost.auto.value.defensive_copy.adapter.DefensiveCopy;
import other.DefensiveCopiers;

import java.util.Properties;

@AutoValue
public abstract class CustomAdapterExternal {
  @DefensiveCopy(copier = DefensiveCopiers.PropertiesCopier.class)
  public abstract Properties config();
}
