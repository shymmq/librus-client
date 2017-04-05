package pl.librus.client;

import dagger.Subcomponent;

/**
 * Created by robwys on 28/03/2017.
 */

@MainActivityScope
@Subcomponent(modules = MainActivityModule.class)
public interface MainActivityComponent extends BaseMainActivityComponent {

}
