package ARSystem;

import java.util.Collections;
import java.util.List;

@Deprecated
public class Ranker {
    /**
     * ranks the dokuments by score, for now something very basic
     *
     * @param arrayList
     */
    public static List<Argument> rank(List<Argument> arrayList) {
        arrayList.sort(Collections.reverseOrder());
        return arrayList;
    }
}

