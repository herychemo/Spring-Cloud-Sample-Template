package com.grayraccoon.sample.accountsdomain.values;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public enum Genre implements Serializable {
    @JsonProperty("M")
    M,

    @JsonProperty("F")
    F
}
