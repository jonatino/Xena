package org.xena.cs;

class GameObject implements Addressed {

    private int address;

    @Override
    public int address() {
        return address;
    }

    @Override
    public void setAddress(int address) {
        this.address = address;
    }

    @Override
    public int hashCode() {
        return address;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof GameObject && hashCode() == o.hashCode();
    }

}
