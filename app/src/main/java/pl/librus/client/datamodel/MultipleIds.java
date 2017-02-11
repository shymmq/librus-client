package pl.librus.client.datamodel;

import com.google.common.collect.Lists;

import java.util.ArrayList;

/**
 * Created by szyme on 08.02.2017.
 */

public class MultipleIds extends ArrayList<HasId> {

    public static MultipleIds fromIds(String... ids){
        return fromIds(Lists.newArrayList(ids));
    }

    public static MultipleIds fromIds(Iterable<String> ids) {
        MultipleIds res = new MultipleIds();
        for (String id : ids) {
            res.add(HasId.of(id));
        }
        return res;
    }
}
