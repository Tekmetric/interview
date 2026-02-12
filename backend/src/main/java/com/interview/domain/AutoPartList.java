package com.interview.domain;

import com.interview.model.AutoPart;
import com.interview.model.AutoPartResponse;

import java.util.ArrayList;

public class AutoPartList extends ArrayList<AutoPart> implements AutoPartResponse {
    public AutoPartList() {
        super();
    }

    public AutoPartList(java.util.Collection<? extends AutoPart> c) {
        super(c);
    }
}
