package org.chy.anubis.testengine.junit.descriptor;

import org.chy.anubis.entity.CaseBriefInfo;

public class CaseTestDescriptor extends AbstractTestDescriptor {


    private final CaseBriefInfo caseBriefInfo;

    public CaseTestDescriptor(String name, CaseBriefInfo caseBriefInfo) {
        super(name);
        this.caseBriefInfo = caseBriefInfo;
    }

    @Override
    public Type getType() {
        return Type.TEST;
    }

    @Override
    public boolean isContainer() {
        return false;
    }

    @Override
    public boolean isTest() {
        return true;
    }

    @Override
    public String getDisplayName() {
        return caseBriefInfo.getName();
    }
}
