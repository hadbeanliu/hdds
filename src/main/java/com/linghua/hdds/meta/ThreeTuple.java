package com.linghua.hdds.meta;

public class ThreeTuple<_1,_2,_3> extends TwoTuple<_1, _2> {

	public _3 third;
	
	public ThreeTuple(_1 first, _2 second,_3 third) {
		super(first, second);
		this.third=third;
	}

}
