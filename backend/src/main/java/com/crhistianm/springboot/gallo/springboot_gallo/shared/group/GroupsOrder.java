package com.crhistianm.springboot.gallo.springboot_gallo.shared.group;

import jakarta.validation.GroupSequence;

@GroupSequence({FirstCheck.class, SecondCheck.class, ThirdCheck.class})
public interface GroupsOrder {
}
