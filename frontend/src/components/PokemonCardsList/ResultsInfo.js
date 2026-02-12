import React from "react";
import styled from "styled-components";

const ResultsInfoContainer = styled.div`
  margin-bottom: ${(props) => props.theme.spacing.xl};
  padding: ${(props) => props.theme.spacing.lg};
  background-color: ${(props) =>
    props.hasResults
      ? props.theme.colors.successBg
      : props.theme.colors.warningBg};
  border: 1px solid
    ${(props) =>
      props.hasResults
        ? props.theme.colors.successBorder
        : props.theme.colors.warningBorder};
  border-radius: ${(props) => props.theme.spacing.sm};
  text-align: center;
`;

const ResultsTitle = styled.div`
  font-weight: bold;
  color: ${(props) =>
    props.hasResults
      ? props.theme.colors.successText
      : props.theme.colors.warning};
  margin-bottom: 5px;
`;

const ResultsText = styled.div`
  color: ${(props) =>
    props.hasResults
      ? props.theme.colors.successText
      : props.theme.colors.warning};
`;

const ResultsInfo = ({
  totalCards,
  searchName,
  selectedSetName,
  currentPage,
  totalPages,
}) => {
  const hasResults = totalCards > 0;

  return (
    <ResultsInfoContainer hasResults={hasResults}>
      {hasResults ? (
        <div>
          <ResultsTitle hasResults={true}>Search Complete!</ResultsTitle>
          <ResultsText hasResults={true}>
            Found <strong>{totalCards}</strong> cards
            {searchName && ` matching "${searchName}"`}
            {selectedSetName && (
              <span>
                {" "}
                in <strong>{selectedSetName}</strong>
              </span>
            )}
            {totalPages > 1 && (
              <span>
                {" "}
                • Showing page <strong>{currentPage}</strong> of{" "}
                <strong>{totalPages}</strong>
              </span>
            )}
          </ResultsText>
        </div>
      ) : (
        <div>
          <ResultsTitle hasResults={false}>No Results Found</ResultsTitle>
          <ResultsText hasResults={false}>
            {searchName
              ? `No cards found matching "${searchName}" in the selected set. Try a different search term.`
              : selectedSetName
              ? `No cards found in ${selectedSetName}. Try searching for a specific Pokemon name.`
              : "Please select a set to view cards."}
          </ResultsText>
        </div>
      )}
    </ResultsInfoContainer>
  );
};

export default ResultsInfo;
