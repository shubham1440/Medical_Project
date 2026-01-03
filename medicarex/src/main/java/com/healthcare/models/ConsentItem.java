package com.healthcare.models;

import com.healthcare.models.enums.ResourceCategory;

public record ConsentItem(ResourceCategory category, String subType) {}