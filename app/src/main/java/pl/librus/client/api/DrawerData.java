package pl.librus.client.api;

import com.google.common.base.Optional;

import org.immutables.value.Value;

import pl.librus.client.datamodel.LuckyNumber;
import pl.librus.client.datamodel.Me;

@Value.Immutable
public interface DrawerData {
    @Value.Parameter
    Me me();

    @Value.Parameter
    Optional<LuckyNumber> luckyNumber();
}
