package com.linghua.hdds.meta;

public class Hcolumn {
	
	public static enum Type {
	    RECORD, ENUM, ARRAY, MAP, UNION, FIXED, STRING, BYTES,
	      INT, LONG, FLOAT, DOUBLE, BOOLEAN, NULL;
	    private String name;
	    private Type() { this.name = this.name().toLowerCase(); }
	    public String getName() { return name; }
	  };

	private byte[] family;
	private Type type;
	
	private byte[] qualifier;

	public Hcolumn(byte[] family, byte[] qualifier,Type type) {
		super();
		this.family = family;
		this.qualifier = qualifier;
		this.type=type;
	}
	
	

	public Hcolumn(byte[] family,Type type) {
		super();
		this.family = family;
		this.type=type;
	}



	public byte[] getFamily() {
		return family;
	}

	public void setFamily(byte[] family) {
		this.family = family;
	}

	public byte[] getQualifier() {
		return qualifier;
	}

	public void setQualifier(byte[] qualifier) {
		this.qualifier = qualifier;
	}



	public Type getType() {
		return type;
	}



	public void setType(Type type) {
		this.type = type;
	}
	
	public boolean equals(Object o){
		
		return false;
	}

}


