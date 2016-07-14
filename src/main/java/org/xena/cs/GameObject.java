package org.xena.cs;

class GameObject implements Addressed {

    private long address;

    @Override
    public long address() {
        return address;
    }

    @Override
    public void setAddress(long address) {
        this.address = address;
    }

    @Override
    public int hashCode() {
        return (int) address;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof GameObject && hashCode() == o.hashCode();
    }

}
