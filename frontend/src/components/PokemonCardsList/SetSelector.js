import React from "react";
import styled from "styled-components";

const SetSelectionContainer = styled.div`
  margin-bottom: ${(props) => props.theme.spacing.xl};
`;

const Label = styled.label`
  display: block;
  margin-bottom: ${(props) => props.theme.spacing.sm};
  font-weight: bold;
`;

const LoadingText = styled.div`
  color: ${(props) => props.theme.colors.secondary};
  font-style: italic;
`;

const ErrorText = styled.div`
  color: ${(props) => props.theme.colors.danger};
`;

const Select = styled.select`
  padding: ${(props) => props.theme.spacing.sm};
  width: 400px;
  border: 1px solid #ccc;
  border-radius: ${(props) => props.theme.spacing.xs};
  font-size: ${(props) => props.theme.fontSizes.base};
`;

const SetSelector = ({ sets, selectedSet, onSetChange, loading, error }) => {
  return (
    <SetSelectionContainer>
      <Label>Select Pokemon Set:</Label>
      {loading ? (
        <LoadingText>Loading sets...</LoadingText>
      ) : error ? (
        <ErrorText>Error loading sets: {error}</ErrorText>
      ) : (
        <Select value={selectedSet} onChange={(e) => onSetChange(e.target.value)}>
          <option value="">Select a set...</option>
          {sets.map((set) => (
            <option key={set.id} value={set.id}>
              {set.name}
            </option>
          ))}
        </Select>
      )}
    </SetSelectionContainer>
  );
};

export default SetSelector;

