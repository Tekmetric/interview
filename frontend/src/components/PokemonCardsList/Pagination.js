import React from "react";
import styled from "styled-components";

const PaginationContainer = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  gap: ${(props) => props.theme.spacing.md};
  margin-top: ${(props) => props.theme.spacing.xl};
  margin-bottom: ${(props) => props.theme.spacing.xl};
`;

const PageInfo = styled.span`
  font-size: ${(props) => props.theme.fontSizes.base};
  color: ${(props) => props.theme.colors.secondary};
  font-weight: 500;
`;

const PaginationButton = styled.button`
  padding: ${(props) => props.theme.spacing.sm}
    ${(props) => props.theme.spacing.lg};
  background-color: ${(props) =>
    props.disabled ? props.theme.colors.lightGray : props.theme.colors.primary};
  color: ${(props) =>
    props.disabled ? props.theme.colors.secondary : props.theme.colors.white};
  border: 1px solid
    ${(props) =>
      props.disabled ? props.theme.colors.border : props.theme.colors.primary};
  border-radius: ${(props) => props.theme.spacing.xs};
  font-size: ${(props) => props.theme.fontSizes.base};
  cursor: ${(props) => (props.disabled ? "not-allowed" : "pointer")};
  transition: all 0.2s ease;

  &:hover {
    background-color: ${(props) =>
      props.disabled ? props.theme.colors.lightGray : "#222"};
    transform: ${(props) => (props.disabled ? "none" : "translateY(-1px)")};
  }

  &:active {
    transform: ${(props) => (props.disabled ? "none" : "translateY(0)")};
  }
`;

const Pagination = ({ currentPage, totalPages, onPrevious, onNext }) => {
  if (totalPages <= 1) {
    return null;
  }

  return (
    <PaginationContainer>
      <PaginationButton onClick={onPrevious} disabled={currentPage === 1}>
        Previous
      </PaginationButton>
      <PageInfo>
        Page {currentPage} of {totalPages}
      </PageInfo>
      <PaginationButton onClick={onNext} disabled={currentPage === totalPages}>
        Next
      </PaginationButton>
    </PaginationContainer>
  );
};

export default Pagination;
