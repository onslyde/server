package com.onslyde.domain;

import java.util.Date;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(SlideGroupVotes.class)
public abstract class SlideGroupVotes_ {

	public static volatile SingularAttribute<SlideGroupVotes, Integer> id;
	public static volatile SingularAttribute<SlideGroupVotes, SlideGroup> slideGroup;
	public static volatile SingularAttribute<SlideGroupVotes, Attendee> attendee;
	public static volatile SingularAttribute<SlideGroupVotes, SlideGroupOptions> slideGroupOptions;
	public static volatile SingularAttribute<SlideGroupVotes, Date> voteTime;

}

