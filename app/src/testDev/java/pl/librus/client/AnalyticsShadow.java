package pl.librus.client;

import org.robolectric.annotation.Implements;

@Implements(AnalyticsShadow.class)
public class AnalyticsShadow {
    //No analytics to shadow in DEV
}
