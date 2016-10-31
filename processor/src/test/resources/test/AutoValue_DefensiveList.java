package test;

import com.kevinmost.auto.value.defensive_copy.adapter.DefensiveCopy;
import java.lang.Override;
import java.lang.String;
import java.util.List;
import org.jetbrains.annotations.NotNull;

final class AutoValue_DefensiveList extends $AutoValue_DefensiveList {
  AutoValue_DefensiveList(@NotNull String userName, @NotNull List<String> orderIDs) {
    super(userName, orderIDs);
  }

  @NotNull
  @DefensiveCopy
  @Override
  public List<String> orderIDs() {
    return java.util.Collections.unmodifiableList(super.orderIDs());
  }
}