package com.onslyde.domain;

import java.util.Date;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Session.class)
public abstract class Session_ {

	public static volatile SingularAttribute<Session, Integer> id;
	public static volatile SingularAttribute<Session, String> sessionCode;
	public static volatile SingularAttribute<Session, String> sessionName;
	public static volatile SingularAttribute<Session, Date> created;
	public static volatile SingularAttribute<Session, Date> start;
	public static volatile SetAttribute<Session, SlideGroup> slideGroups;
	public static volatile SingularAttribute<Session, User> user;
	public static volatile SingularAttribute<Session, Date> end;

}

