package pl.librus.client.datamodel;

import java.util.ArrayList;

/**
 * Created by szyme on 08.02.2017.
 */

public class MultipleIds extends ArrayList<HasId> {
    public static MultipleIds fromIds(Iterable<String> ids) {
        MultipleIds res = new MultipleIds();
        for (String id : ids) {
            res.add(HasId.of(id));
        }
        return res;
    }
}
