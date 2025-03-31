package com.skillbox.auth.repository;

import com.skillbox.auth.entity.CaptchaEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface CaptchaRepository extends CrudRepository<CaptchaEntity, String> {

}
