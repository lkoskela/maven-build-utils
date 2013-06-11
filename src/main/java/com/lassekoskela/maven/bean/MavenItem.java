package com.lassekoskela.maven.bean;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

public class MavenItem {

	private final String name;
	
	public MavenItem(String name) {
		this.name = name;
	}
	
	public String getItemId() {
		return name;
	}

	@Override
	public int hashCode(){
		return Objects.hashCode(name);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof MavenItem) {
			MavenItem that = (MavenItem) object;
			return Objects.equal(this.name, that.name);
		}
		return false;
	}

	@Override
	public String toString() {
		return toStringHelper()
			.toString();
	}
	
	protected ToStringHelper toStringHelper() {
		return Objects.toStringHelper(this)
			.add("name", name);
	}
}
