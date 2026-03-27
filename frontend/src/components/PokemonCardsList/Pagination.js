import React from "react";
import styled from "styled-components";
import Button from "./Button";

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

const Pagination = ({ currentPage, totalPages, onPrevious, onNext }) => {
  if (totalPages <= 1) {
    return null;
  }

  return (
    <PaginationContainer>
      <Button onClick={onPrevious} disabled={currentPage === 1}>
        Previous
      </Button>
      <PageInfo>
        Page {currentPage} of {totalPages}
      </PageInfo>
      <Button onClick={onNext} disabled={currentPage === totalPages}>
        Next
      </Button>
    </PaginationContainer>
  );
};

export default Pagination;
