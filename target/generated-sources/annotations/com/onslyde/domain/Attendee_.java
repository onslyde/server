package com.onslyde.domain;

import java.util.Date;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Attendee.class)
public abstract class Attendee_ {

	public static volatile SingularAttribute<Attendee, Integer> id;
	public static volatile SingularAttribute<Attendee, String> socketId;
	public static volatile SingularAttribute<Attendee, Date> created;
	public static volatile SingularAttribute<Attendee, String> email;
	public static volatile SingularAttribute<Attendee, String> name;
	public static volatile SetAttribute<Attendee, SlideGroupVotes> slideGroupVoteses;
	public static volatile SingularAttribute<Attendee, String> ip;

}

