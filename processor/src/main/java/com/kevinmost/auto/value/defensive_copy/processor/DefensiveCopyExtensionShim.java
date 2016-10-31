package com.kevinmost.auto.value.defensive_copy.processor;

import com.google.auto.service.AutoService;
import com.google.auto.value.extension.AutoValueExtension;

// We need this because AutoValue silently ignores Kotlin classes :(
@AutoService(AutoValueExtension.class)
public final class DefensiveCopyExtensionShim extends DefensiveCopyExtension {}
