package pl.librus.client.data;

import android.support.annotation.Nullable;

import com.google.common.base.Joiner;

import org.immutables.value.Value;

/**
 * Created by szyme on 08.02.2017.
 */

@Value.Immutable
@Value.Style(typeAbstract = {"Abstract*"}, typeImmutable = "*")
public abstract class AbstractEntityInfo {

    @Value.Parameter
    public abstract String name();

    @Value.Default
    public String pluralName() {
        return name() + 's';
    }

    @Value.Default
    @Nullable
    public String endpointPrefix() {
        return "";
    }

    public String endpoint() {
        return endpoint(null);
    }

    public String endpoint(String id) {
        return Joiner.on('/').skipNulls().join(endpointPrefix(), single() ? name() : pluralName(), id);
    }

    @Value.Default
    public String topLevelName() {
        return single() ? name() : pluralName();
    }

    @Value.Default
    public boolean single() {
        return false;
    }

}
