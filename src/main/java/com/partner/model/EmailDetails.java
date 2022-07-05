package com.partner.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailDetails implements Serializable {
	private static final long serialVersionUID = 1016069467647371124L;
	private String email;
	private String name;
	private String phoneNumber;
	private String subject;
	private String body;
}
