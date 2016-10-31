package test;

import com.google.auto.value.AutoValue;
import com.kevinmost.auto.value.defensive_copy.adapter.DefensiveCopier;
import com.kevinmost.auto.value.defensive_copy.adapter.DefensiveCopy;
import org.jetbrains.annotations.NotNull;

@AutoValue
public abstract class CustomAdapter {
  @DefensiveCopy(copier = CustomAdapter.Adapter.class)
  public abstract StringBuilder stringBuilder();

  public static class Adapter implements DefensiveCopier<StringBuilder> {
    @NotNull @Override public StringBuilder defensiveCopy(@NotNull StringBuilder source) {
      return new StringBuilder(source.toString());
    }
  }
}
