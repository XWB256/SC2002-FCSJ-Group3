package Utilities;

import java.util.List;

public interface Searchable {

    String IDstring();

    String defaultString();

    String toSearchableString();

    List<Integer> toSearchableNum();

}
