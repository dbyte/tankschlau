package de.fornalik.tankschlau.util;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Customized apache.commons.lang3 {@link ToStringBuilder} used by this application.
 * @see ToStringBuilder
 */
public class MyToStringBuilder extends ToStringBuilder {

  /**
   * @see ToStringBuilder
   */
  public MyToStringBuilder(Object object) {
    super(object, MyToStringStyle.getInstance());
  }
}

/**
 * Thread-safe-lazy-loading Singleton which provides customized settings for the
 * apache.commons.lang3 {@link ToStringBuilder}.
 */
class MyToStringStyle extends ToStringStyle {
  private static MyToStringStyle instance;

  private MyToStringStyle() {
    super();
  }

  public static synchronized MyToStringStyle getInstance() {
    if (instance == null)
      instance = new MyToStringStyle();

    // Hide package paths
    instance.setUseShortClassName(true);

    return instance;
  }
}