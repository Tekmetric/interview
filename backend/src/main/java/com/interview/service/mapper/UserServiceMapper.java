package com.interview.service.mapper;

import com.interview.repository.model.DocumentEntity;
import com.interview.repository.model.UserEntity;
import com.interview.service.model.DocumentDm;
import com.interview.service.model.UserDm;
import org.mapstruct.Mapper;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserServiceMapper {
    UserDm toDm(UserEntity entity);

    UserEntity toEntity(UserDm userDm);

    List<UserDm> toUserDmList(Iterable<UserEntity> entityList);

    DocumentEntity documentDmToDocumentEntity(DocumentDm documentDm);

    Set<DocumentDm> documentEntityListToDocumentDmList(List<DocumentEntity> documentEntity);

    default List<DocumentEntity> toDocumentEntityList(Collection<DocumentDm> documentDmList, UserDm userDm) {
        if (documentDmList == null) {
            return Collections.emptyList();
        }

        return documentDmList.stream()
                .map(documentDm -> documentDmToDocumentEntity(documentDm, userDm))
                .collect(Collectors.toList());
    }

    default DocumentEntity documentDmToDocumentEntity(DocumentDm documentDm, UserDm userDm) {
        if (documentDm == null) {
            return null;
        }

        DocumentEntity documentEntity = documentDmToDocumentEntity(documentDm);
        if (userDm != null) {
            documentEntity.setUser(toEntity(userDm));
        }

        return documentEntity;
    }
}
