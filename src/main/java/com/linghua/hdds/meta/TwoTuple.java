package com.linghua.hdds.meta;

public class TwoTuple<first,second> {
	
	public final first _1;
    public second _2;
	
	public TwoTuple(first _1 ,second _2 ){{
		this._1=_1;
		this._2=_2;
	}
		
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		TwoTuple<?, ?> twoTuple = (TwoTuple<?, ?>) o;

		return _1 != null ? _1.equals(twoTuple._1) : twoTuple._1 == null;
	}

	@Override
	public int hashCode() {
		return _1 != null ? _1.hashCode() : 0;
	}

	public String toString(){
		return this._1+":"+ this._2;
	}

}
