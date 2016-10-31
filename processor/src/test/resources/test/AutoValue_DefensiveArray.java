package test;

import com.kevinmost.auto.value.defensive_copy.adapter.DefensiveCopy;
import java.lang.Override;
import java.lang.String;
import java.util.List;
import org.jetbrains.annotations.NotNull;

final class AutoValue_DefensiveArray extends $AutoValue_DefensiveArray {
  AutoValue_DefensiveArray(@NotNull String userName, @NotNull List<String> orderIDs, @NotNull long[] creditCards) {
    super(userName, orderIDs, creditCards);
  }

  @NotNull
  @DefensiveCopy
  @Override
  public long[] creditCards() {
    return java.util.Arrays.copyOf(super.creditCards(), super.creditCards().length);
  }
}