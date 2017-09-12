package com.linghua.hdds.preference.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Maps;

public class StringIndexModel {
	public String[] label;
	public Map<String, Integer> labelToIndex;

	private StringIndexModel(String[] label) {

		this.label = label;

	}

	private Map<String, Integer> labelToIndex() {
		if (labelToIndex == null) {

			labelToIndex = Maps.newHashMapWithExpectedSize(label.length);
			int i = 0;
			for (String s : label) {
				labelToIndex.put(s, i);
				i++;
			}
		}
		return labelToIndex;
	}
	
	public StringIndexModel(Map<String, Integer> labelIndex){
		this.labelToIndex=labelIndex;
	}

	public static StringIndexModel build(List<String> strs) {

		Map<String, Integer> freq = new HashMap<>();
		for (String s : strs) {
			if (freq.containsKey(s))
				freq.put(s, freq.get(s) + 1);
			else
				freq.put(s, 1);

		}
		int length = freq.size();
		List<Entry<String, Integer>> list = new ArrayList<>(freq.entrySet());
		Collections.sort(list, new Comparator<Entry<String, Integer>>() {

			@Override
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {

				return o2.getValue().compareTo(o1.getValue());
			}
		});
		String[] label = new String[length];
		for (int i = 0; i < length; i++)
			label[i] = list.get(i).getKey();


		return new StringIndexModel(label);
	}

	public int[] transform(String... args) {
		Map<String, Integer> indexer=labelToIndex();
		int length = args.length;
		int[] index = new int[length];
		int i = 0;
		for (String s : args) {
			index[i] = indexer.get(s);
			i++;
		}

		return index;
	}

	public static void main(String[] args) {

		Map<String, Integer> map = Maps.newHashMapWithExpectedSize(2);
		map.put("a", 1);
		map.put("b", 2);
		map.put("g", 1);
		map.put("c", 22);

	}

}
