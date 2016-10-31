# AutoValue: Defensive Copiers

An extension for Google's [AutoValue](https://github.com/google/auto) that
hides mutable objects behind defensive copiers, creating true immutable objects.

## Usage

Annotate any properties in your `@AutoValue`-annotated class
that should be defensively copied when accessed with `@DefensiveCopy`

```java
@AutoValue public abstract class Customer {

  // immutable, needs no defensive copying
  public abstract String firstName();

  // could be mutated by external sources; should be hidden using @DefensiveCopy
  @DefensiveCopy public abstract List<String> orderIDs();
}
```

This supports a small set of built-in Java classes (currently, all Collections,
as well as `java.util.Date`). Other classes require a custom implementation
by the user. This can be accomplished by using `@DefensiveCopy(copier = MyCopier.class)`,
where `MyCopier` implements `DefensiveCopier<T>` (where `T` is the property's
return type)

```java
@AutoValue public abstract class Location {
  public abstract String streetAddress();
  public abstract int zipCode();

  @DefensiveCopy(copier = PointCopier.class) public abstract java.awt.Point coordinates();
}
```

And then implement `PointCopier` as such:

```java
public class PointCopier extends DefensiveCopier<Point> {
  @NotNull @Override public Point defensiveCopy(@NotNull Point source) {
    return new Point(source); // copy-constructor ensures the caller can never modify the source
  }
}
```

## Download

Add JitPack at the end of your list of repositories if you haven't already:

```groovy
repositories {
  ... // jcenter(), mavenCentral(), etc
  maven { url "https://jitpack.io" }
}
```

Now add the dependencies:

```groovy
dependencies {
  compileOnly "com.github.kevinmost.auto-value-defensive-copy:adapter:[version]"
  apt "com.github.kevinmost.auto-value-defensive-copy:processor:[version]"
}
```
