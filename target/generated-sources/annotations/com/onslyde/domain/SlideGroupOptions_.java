package com.onslyde.domain;

import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(SlideGroupOptions.class)
public abstract class SlideGroupOptions_ {

	public static volatile SingularAttribute<SlideGroupOptions, Integer> id;
	public static volatile SingularAttribute<SlideGroupOptions, SlideGroup> slideGroup;
	public static volatile SingularAttribute<SlideGroupOptions, String> name;
	public static volatile SingularAttribute<SlideGroupOptions, String> value;
	public static volatile SetAttribute<SlideGroupOptions, SlideGroupVotes> slideGroupVoteses;

}

