package com.onslyde.domain;

import java.util.Date;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(SlideGroup.class)
public abstract class SlideGroup_ {

	public static volatile SingularAttribute<SlideGroup, Integer> id;
	public static volatile SingularAttribute<SlideGroup, String> groupName;
	public static volatile SetAttribute<SlideGroup, Slide> slides;
	public static volatile SetAttribute<SlideGroup, SlideGroupOptions> slideGroupOptionses;
	public static volatile SingularAttribute<SlideGroup, Date> created;
	public static volatile SingularAttribute<SlideGroup, Session> session;
	public static volatile SetAttribute<SlideGroup, SlideGroupVotes> slideGroupVoteses;

}

