package pl.librus.client.cache;

import java.io.Serializable;

import pl.librus.client.api.LibrusAccount;

/**
 * Created by szyme on 20.01.2017.
 */
public class AccountCache implements Serializable, LibrusCache {
    private static final long serialVersionUID = -6237418752993789375L;
    private final LibrusAccount account;


    public AccountCache(LibrusAccount result) {
        this.account = result;
    }

    public LibrusAccount getAccount() {
        return account;
    }

    @Override
    public long getExpirationPeriod() {
        return 0;
    }
}
