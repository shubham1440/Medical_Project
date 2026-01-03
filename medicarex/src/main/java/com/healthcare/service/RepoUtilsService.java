package com.healthcare.service;

import com.healthcare.models.Patient;
import com.healthcare.models.Provider;
import com.healthcare.models.User;

public interface RepoUtilsService {
     Provider getProviderByEmail(String email);
     Patient getPatientByEmail(String email);
     User getUserByEmail(String email);
}
