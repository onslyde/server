package com.onslyde.domain;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Slide.class)
public abstract class Slide_ {

	public static volatile SingularAttribute<Slide, Integer> id;
	public static volatile SingularAttribute<Slide, SlideGroup> slideGroup;
	public static volatile SingularAttribute<Slide, String> name;
	public static volatile SingularAttribute<Slide, Integer> slideIndex;

}

