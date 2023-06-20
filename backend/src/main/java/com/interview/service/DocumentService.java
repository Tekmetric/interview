package com.interview.service;

import com.interview.repository.DocumentRepository;
import com.interview.service.mapper.UserServiceMapper;
import com.interview.service.model.DocumentDm;
import com.interview.service.model.UserDm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DocumentService {
    private final DocumentRepository documentRepository;
    private final UserServiceMapper userServiceMapper;

    Set<DocumentDm> saveNew(Collection<DocumentDm> documentsToSave, UserDm userDm) {
        return userServiceMapper.documentEntityListToDocumentDmList(
                documentRepository.saveAll(
                        userServiceMapper.toDocumentEntityList(documentsToSave, userDm)));
    }
}
