package test;

import com.kevinmost.auto.value.defensive_copy.adapter.DefensiveCopy;
import java.lang.Override;
import java.lang.StringBuilder;
import org.jetbrains.annotations.NotNull;

final class AutoValue_CustomAdapter extends $AutoValue_CustomAdapter {
  @NotNull
  private static final CustomAdapter.Adapter stringBuilderCopier = new CustomAdapter.Adapter();

  AutoValue_CustomAdapter(StringBuilder stringBuilder) {
    super(stringBuilder);
  }

  @DefensiveCopy
  @Override
  public StringBuilder stringBuilder() {
    final StringBuilder tmp = super.stringBuilder();
    if (tmp == null) {
      return null;
    }
    return stringBuilderCopier.defensiveCopy(tmp);
  }
}
