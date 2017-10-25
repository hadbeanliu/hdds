package com.linghua.hdds.meta;

public class TwoTuple<first,second> {
	
	public final first _1;
    public second _2;
	
	public TwoTuple(first _1 ,second _2 ){{
		this._1=_1;
		this._2=_2;
	}
		
	}

	public String toString(){
		return this._1+":"+ this._2;
	}

}
